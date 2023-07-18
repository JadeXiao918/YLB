package com.ylb.front.controller;

import com.ylb.api.model.User;
import com.ylb.api.pojo.UserAccountInfo;
import com.ylb.common.constants.RedisKey;
import com.ylb.common.enums.RCode;
import com.ylb.common.util.CommonUtil;
import com.ylb.common.util.JwtUtil;
import com.ylb.front.service.RealnameServiceImpl;
import com.ylb.front.service.SmsService;
import com.ylb.front.view.RespResult;
import com.ylb.front.vo.RealnameVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "用户功能")
@RestController
@RequestMapping("/v1/user")
public class UserController extends BaseController{
    @Resource(name = "smsCodeRegisterImpl")
    private SmsService smsService;
    @Resource(name = "smsCodeLoginImpl")
    private SmsService loginSmsService;
    @Resource
    private RealnameServiceImpl realnameService;
    @Resource
    private JwtUtil jwtUtil;
    //手机号注册用户
    @ApiOperation(value = "手机号注册用户")
    @PostMapping("/register")
    public RespResult userRegister(@RequestParam String phone,
                                   @RequestParam String pword,
                                   @RequestParam String scode){
        RespResult result = RespResult.fail();
        //检查参数
        if (CommonUtil.checkPhone(phone)){
            if (pword != null && pword.length() == 32){
                //检查短信验证码
                if (smsService.checkSmsCode(phone,scode)){
                    //可以注册
                    int registerResult = userService.userRegister(phone,pword);
                    if (registerResult == 1){
                        result = RespResult.ok();
                    }else if (registerResult == 2){
                        result.setRCode(RCode.PHONE_EXISTS);
                    }else {
                        result.setRCode(RCode.REQUEST_PARAM_ERR);
                    }
                }else {
                    //短信验证码无效
                    result.setRCode(RCode.SMS_CODE_INVALID);
                }
            }else {
                result.setRCode(RCode.REQUEST_PARAM_ERR);
            }
        }else {
            //手机号格式不正确
            result.setRCode(RCode.PHONE_FORMAT_ERR);
        }
        return result;
    }
    //手机号是否存在
    @ApiOperation(value = "手机号是否注册过",notes = "在注册功能中，判断手机号是否可以注册")
    @ApiImplicitParam(name = "phone",value = "手机号")
    @GetMapping("/phone/exists")
    public RespResult phoneExists(@RequestParam("phone") String phone){
        RespResult result = new RespResult();
        result.setCode(RCode.PHONE_EXISTS);
        //检查请求参数是否符合要求
        if (CommonUtil.checkPhone(phone)){
            //可以执行，查询数据库
            User user = userService.queryByPhone(phone);
            if (user == null){
                //可以注册
                result = RespResult.ok();
            }

        }else {
            result.setRCode(RCode.PHONE_FORMAT_ERR);
        }
        return result;
    }
    //登录，获取token-jwt
    @ApiOperation(value = "用户登录-获取访问token")
    @PostMapping("/login")
    public RespResult userLogin(@RequestParam String phone,
                                @RequestParam String pword,
                                @RequestParam String scode) throws Exception{
        RespResult result = RespResult.fail();
        if (CommonUtil.checkPhone(phone) && (pword != null && pword.length() == 32)){
            if (loginSmsService.checkSmsCode(phone,scode)) {
                //访问data-service
                User user = userService.userLogin(phone,pword);
                if (user != null){
                    //登录成功，生成token
                    Map<String,Object> data = new HashMap<>();
                    data.put("uid" ,user.getId());
                    String jwtToken = jwtUtil.createJwt(data,120);
                    result = RespResult.ok();
                    result.setAccessToken(jwtToken);
                    Map<String,Object> userInfo = new HashMap<>();
                    userInfo.put("uid",user.getId());
                    userInfo.put("phone",user.getPhone());
                    userInfo.put("name",user.getName());
                    result.setData(userInfo);
                } else {
                    result.setRCode(RCode.PHONE_LOGIN_PASSWORD_INVALID);
                }
            }else {
                result.setRCode(RCode.SMS_CODE_INVALID);
            }
        }else {
            result.setRCode(RCode.REQUEST_PARAM_ERR);
        }
        return result;
    }
    //实名认证
    @ApiOperation(value = "实名认证" ,notes = "提供手机号和姓名，身份证号。认证姓名和身份证号是否一致")
    @PostMapping("/realname")
    public RespResult userRealName(@RequestBody RealnameVo realnameVo){
        RespResult result = RespResult.fail();
        result.setRCode(RCode.REQUEST_PARAM_ERR);
        //验证请求参数
        if (CommonUtil.checkPhone(realnameVo.getPhone())){
            if (StringUtils.isNotBlank(realnameVo.getName()) && StringUtils.isNotBlank(realnameVo.getIdCard())){
                //判断用户已经做过认证
                User user = userService.queryByPhone(realnameVo.getPhone());
                if (user != null){
                    if ( StringUtils.isNotBlank(user.getName())){
                        result.setRCode(RCode.REALNAME_RETRY);
                    }else {
                        //调用第三方接口，判断认证结果
                        boolean realnameResult = realnameService.handleRealname(realnameVo.getPhone(), realnameVo.getName(), realnameVo.getIdCard());
                        if (realnameResult == true){
                            result = RespResult.ok();
                        }else {
                            result.setRCode(RCode.REALNAME_FAIL);
                        }
                    }
                }
            }
        }
        return result;
    }

    //用户中心
    @ApiOperation(value = "用户中心")
    @GetMapping("/usercenter")
    public RespResult userCenter(@RequestHeader(value = "uid",required = false) Integer uid){
        RespResult result = RespResult.fail();
        if (uid != null && uid > 0){
            UserAccountInfo userAccountInfo = userService.queryUserAllInfo(uid);
            if (userAccountInfo != null){
                result = RespResult.ok();
                Map<String,Object> data = new HashMap<>();
                data.put("name",userAccountInfo.getName());
                data.put("phone",userAccountInfo.getPhone());
                data.put("headerUrl",userAccountInfo.getHeaderImage());
                data.put("money",userAccountInfo.getAvailableMoney());
                if (userAccountInfo.getLastLoginTime() != null){
                    data.put("loginTime", DateFormatUtils.format(userAccountInfo.getLastLoginTime(),"yyyy-MM-dd HH:mm:ss"));
                }else {
                    data.put("loginTime","-");
                }
                result.setData(data);
            }
        }
        return result;
    }
    @ApiOperation(value = "退出登录")
    @GetMapping("/logOut")
    public RespResult LogOut(@RequestParam String phone){
        RespResult result = RespResult.fail();
        if (phone != null){
            String key = RedisKey.KEY_KEY_CODE_LOGIN + phone;
            stringRedisTemplate.delete(key);
            result = RespResult.ok();
        }
        return result;
    }
}
