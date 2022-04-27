package com.cos.jwt.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// 이 필터3 은 SecurityConfig에 설정해서 시큐리티 필터체인에 필터를 추가해줌
// 이 놈은 시큐리티가 동작되기 전에 걸러내기 위해 SecurityConfig에서 addFilterBefore로 작성
public class MyFilter3 implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest; // 다운캐스팅
        HttpServletResponse res = (HttpServletResponse) servletResponse; // 다운캐스팅

        // 토큰:cos 이걸 만들어줘야함. id pw 가 정상적으로 들어와서 로그인이 완료되면 토큰을 만들어주고 그걸 응답(response)을 해줌
        // 그럼 요청(request)할때마다 header의 Authorization value값으로 토큰을 가지고 올것
        // 그때 토큰이 넘어오면 이 토큰이 내가 만든 토큰이 맞는지만 검증만 하면됨 (이 토큰이 cos인지 아닌지는 별로 중요하지않음 그냥 내가 만든게 맞나만 확인되면 됨
        // 토큰검증은 RSA, HS256으로 검증함 예를들어 내가 만든 토큰을 개인키로 잠궈서 넘겨주고 내 공개키로 열리면 내가 만든거라는 전자서명이 됨)
        if(req.getMethod().equals("POST")) // 만약 오는 요청이 POST 방식이면
        {
            System.out.println("POST 요청됨");
            String headerAuth = req.getHeader("Authorization"); // request의 Authorization라는 헤더를 가져올것
            System.out.println(headerAuth);
            System.out.println("필터1");

            if(headerAuth.equals("cos")) // 만약 Authorization 헤더에 cos라는 토큰이 있을때만 진입을 허용해줄것
            {
                //cos 필터가 있을때만 필터에서 허용해줄것
                filterChain.doFilter(req, res); //다운캐스팅한 객체 필터 등록 (filterChain은 필터체인의 다음 필터를 가리키고 doFilter는 다음필터를 호출해 넘어가겠다 라는 의미)
            }
            else // cos 필터가 없으면 거부하겠다
            {
                PrintWriter out = res.getWriter();//response 즉 응답의 writer를 담았으니 응답해서 보여주는 페이지에 문자열을 보여주겠다는 의미
                out.print("인증안됨");
            }
        }
        else // post가 아닐때는 일반적으로 반환
        {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }
}
