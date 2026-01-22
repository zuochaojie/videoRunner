package com.example.test.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    TokenInterceptor getinInterceptor(){
        return new TokenInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册声明的拦截器
        registry.addInterceptor(getinInterceptor())
                .addPathPatterns("/**")   // 对数据获取接口进行拦截
                .excludePathPatterns("/user/login","/message/send/**"); // 放行 登录接口
    }
}
