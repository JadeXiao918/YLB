package com.ylb.front.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.ylb.common.enums.RCode;
import com.ylb.common.util.JwtUtil;
import com.ylb.front.view.RespResult;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class TokenInterceptor implements HandlerInterceptor {

    public TokenInterceptor(String secret) {
        this.secret = secret;
    }

    private String secret = "";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //如果是OPTION，放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())){
            return true;
        }
        boolean requestSend = false;
        try {
            String headerUid = request.getHeader("uid");
            String headerToken = request.getHeader("Authorization");
            if (StringUtils.isNotBlank(headerToken)){
                //Bearer
                String jwt = headerToken.substring(7);
                //读jwt
                JwtUtil jwtUtil = new JwtUtil(secret);
                Claims claims = jwtUtil.readJwt(jwt);
                //获取jwt中的数据，uid
                Integer jwtUid = claims.get("uid",Integer.class);
                if (headerUid.equals(String.valueOf(jwtUid))){
                    //token和发起请求用户是同一个，请求可以被处理
                    requestSend = true;
                }
            }
        }catch (Exception e){
            requestSend = false;
            e.printStackTrace();
        }
        //token没有验证通过，需要给vue提供错误提示
        if (requestSend == false){
            //返回json数据给前端
            RespResult result = RespResult.fail();
            result.setRCode(RCode.TOKEN_INVALID);
            //使用HttpServletResponse输出json
            String respJson = JSONObject.toJSONString(result);
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.println(respJson);
            out.flush();
            out.close();
        }
        return requestSend;
    }
}
