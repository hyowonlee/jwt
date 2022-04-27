package com.cos.jwt.config;

import com.cos.jwt.filter.MyFilter1;
import com.cos.jwt.filter.MyFilter2;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//굳이 SecurityConfig.java에서 시큐리티 필터체인을 걸어줄 필요없이 이렇게 따로 필터를 걸어주는게 좋다
//하지만 시큐리티 필터체인에 등록하는게 이쪽에서 등록하는거 보다 먼저 수행됨 즉 시큐리티 필터체인이 전부 수행되고 나서 이 필터가 실행됨
@Configuration
public class FilterConfig {

    @Bean // IOC가 되어있어서 request 요청이 들어올때 필터가 동작함
    public FilterRegistrationBean<MyFilter1> filter1(){
        FilterRegistrationBean<MyFilter1> bean = new FilterRegistrationBean<>(new MyFilter1()); // 내가 만든 필터인 MyFilter1을 필터로 등록할 것
        bean.addUrlPatterns("/*"); //모든 요청에서 이 필터를 걸 예정
        bean.setOrder(0); // 필터 우선순위 선정으로 낮은번호가 우선순위가 높음
        return bean;
    }

    @Bean // IOC가 되어있어서 request 요청이 들어올때 필터가 동작함
    public FilterRegistrationBean<MyFilter2> filter2(){
        FilterRegistrationBean<MyFilter2> bean = new FilterRegistrationBean<>(new MyFilter2()); // 내가 만든 필터인 MyFilter1을 필터로 등록할 것
        bean.addUrlPatterns("/*"); //모든 요청에서 이 필터를 걸 예정
        bean.setOrder(1); // 필터 우선순위 선정으로 낮은번호가 우선순위가 높음
        return bean;
    }
}
