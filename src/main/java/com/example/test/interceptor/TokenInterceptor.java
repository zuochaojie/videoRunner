package com.example.test.interceptor;

import com.example.test.utils.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenUtil tokenUtil;
    @Value("${jwt.refresh-threshold}")
    private long refreshThreshold;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
//        if (!StringUtils.hasLength(token)){
//            return false;
//        }
//        boolean result = tokenUtil.verifyToken(token);
//        if (!result){
//            return false;
//        }
//        Date expiredDate = tokenUtil.getExpiredDate(token);
//        Date now = Calendar.getInstance().getTime();
//        if (expiredDate.getTime() - now.getTime() < 0)
//        {
//            System.out.println("token 过期");
//            return false;
//        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String token = request.getHeader("token");
        Date expiredDate = tokenUtil.getExpiredDate(request.getHeader("token"));
        Date now = Calendar.getInstance().getTime();
        if (expiredDate.getTime() - now.getTime() < refreshThreshold)
        {
            String username = tokenUtil.getUsernameFromToken(token);
            String newToken = tokenUtil.generateToken(username);
            response.setHeader("New-Access-Token", newToken);
        }
        System.out.println("请求结束了");
    }
}