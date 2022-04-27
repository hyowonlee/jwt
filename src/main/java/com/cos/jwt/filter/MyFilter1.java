package com.cos.jwt.filter;

import javax.servlet.*;
import java.io.IOException;

//내가 만든 필터로 이걸 SecurityConfig에 설정해서 시큐리티 필터체인에 필터를 추가해줄수도 있는데 그건 안하고 FilterConfig.java파일에서 필터를 하나 만들어서 설정할 예정
public class MyFilter1 implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("필터1");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

