package com.ylb.dataservice.service;

import com.ylb.api.model.FinanceAccount;
import com.ylb.api.model.User;
import com.ylb.api.pojo.UserAccountInfo;
import com.ylb.api.service.UserService;
import com.ylb.common.util.CommonUtil;
import com.ylb.dataservice.mapper.FinanceAccountMapper;
import com.ylb.dataservice.mapper.UserMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@DubboService(interfaceClass = UserService.class,version = "1.0")
public class UserServiceImpl implements UserService {
    @Resource
    private FinanceAccountMapper financeAccountMapper;
    @Resource
    private UserMapper userMapper;
    @Value("${ylb.config.password-salt}")
    private String passwordSalt;
    @Override
    public User queryByPhone(String phone) {
        User user = null;
        if (CommonUtil.checkPhone(phone)){
            user = userMapper.selectByPhone(phone);
        }
        return user;
    }
    //用户注册
    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized int userRegister(String phone, String password) {
        int result = 0;
        if (CommonUtil.checkPhone(phone) && (password != null && password.length()==32)){
            //判断手机号在库中是否存在
            User queryUser = userMapper.selectByPhone(phone);
            if (queryUser == null){
                //注册密码的md5二次加密，给原始的密码加盐(salt)
                String newPassword = DigestUtils.md5Hex(password + passwordSalt);
                //注册u_user
                User user = new User();
                user.setPhone(phone);
                user.setLoginPassword(newPassword);
                user.setAddTime(new Date());
                userMapper.insertReturnPrimaryKey(user);
                //获取主键user.getId()
                FinanceAccount account = new FinanceAccount();
                account.setUid(user.getId());
                account.setAvailableMoney(new BigDecimal("0"));
                financeAccountMapper.insertSelective(account);
                //成功 result = 1
                result = 1;
            }else {
                //手机号存在
                result = 2;
            }
        }
        return result;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public User userLogin(String phone, String password) {
        User user = null;
        if (CommonUtil.checkPhone(phone) && (password != null && password.length() == 32)){
            String newPassword = DigestUtils.md5Hex(password + passwordSalt);
            user = userMapper.selectLogin(phone,newPassword);
            //更新最后登录时间
            if (user != null){
                user.setLastLoginTime(new Date());
                userMapper.updateByPrimaryKeySelective(user);
            }
        }
        return user;
    }
    //更新实名认证信息
    @Override
    public boolean modifyRealname(String phone, String name, String idCard) {
        int rows = 0;
        if (!StringUtils.isAnyBlank(phone,name,idCard)){
           rows = userMapper.updateRealname(phone,name,idCard);
        }
        return rows > 0;
    }

    @Override
    public UserAccountInfo queryUserAllInfo(Integer uid) {
        UserAccountInfo info = null;
        if (uid != null && uid > 0){
           info = userMapper.selectUserAccountById(uid);
        }
        return info;
    }
    //查询用户
    @Override
    public User queryById(Integer uid) {
        User user = null;
        if (uid != null && uid > 0){
            user = userMapper.selectByPrimaryKey(uid);
        }
        return user;
    }
}
