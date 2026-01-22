package com.example.test.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class TokenUtil {
    @Value("${jwt.issuer}")
    private String ISSUER;//project-name

    @Value("${jwt.expires_in}")
    private int EXPIRES_IN;//30
    private String encryKey = "project-name";
    private Algorithm ALGORITHM = Algorithm.HMAC256(encryKey);

    public String generateToken(String username) {
        Calendar instance = Calendar.getInstance();
        Calendar expires = Calendar.getInstance();
        expires.setTime(instance.getTime());
        expires.add(Calendar.MINUTE, EXPIRES_IN);
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withIssuedAt(instance.getTime())// 签发时间
                .withExpiresAt(expires.getTime())// 过期时间戳
                .withClaim("userName", username)//自定义参数
                .sign(ALGORITHM);
        return token;
    }

    public String getUsernameFromToken(String token) {
        return getJwt(token).getClaim("userName").toString();
    }

    public boolean verifyToken(String token) {
        try {
            getJwt(token);
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Date getExpiredDate(String token) {
        return getJwt(token).getExpiresAt();
    }

    private DecodedJWT getJwt(String token) {
        return JWT.require(Algorithm.HMAC256(encryKey)).build().verify(token);
    }
}
