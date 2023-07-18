package com.ylb;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtTest {
    //创建jwt
    @Test
    public void testCreateJwt(){
        String key = "b638690e1a2b441ab432207b522d4640";
        //创建SecretKey
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        //创建jwt，使用jwts类
        Date curDate = new Date();
        Map<String,Object> data = new HashMap<>();
        data.put("userId",1001);
        data.put("name","李四");
        data.put("role","经理");
        String jwt = Jwts.builder().signWith(secretKey, SignatureAlgorithm.HS256)
                .setExpiration(DateUtils.addMinutes(curDate, 10))
                .setIssuedAt(curDate)
                .setId(UUID.randomUUID().toString())
                .addClaims(data).compact();
        System.out.println(jwt);
    }
    @Test
    public void testReadJwt(){
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2ODkxMzE1NzcsImlhdCI6MTY4OTEzMDk3NywianRpIjoiNjc1MzNiZjEtMzgzZS00ZmFkLWJlMDEtYmI0OTcxNzM4YTE5Iiwicm9sZSI6Iue7j-eQhiIsIm5hbWUiOiLmnY7lm5siLCJ1c2VySWQiOjEwMDF9.7dIzUnzA7ApASdcdHzdlt8lnfQseaxJU-hBqKW80_Jg";
        String key = "b638690e1a2b441ab432207b522d4640";
        //创建SecretKey
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        //解析jwt
        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(jwt);
        //读数据
        Claims body = claims.getBody();
        Integer userId = body.get("userId", Integer.class);
        System.out.println("userId = " + userId);
        Object uid = body.get("userId");
        System.out.println("uid = " + uid);
        Object name = body.get("name");
        if (name != null){
            String str = (String) name;
            System.out.println("str = " + str);
        }
        String jwtId = body.getId();
        System.out.println("jwtId = " + jwtId);
        Date expiration = body.getExpiration();
        System.out.println("expiration = " + expiration);
    }
}
