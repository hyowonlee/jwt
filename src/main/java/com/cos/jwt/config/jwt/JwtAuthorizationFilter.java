package com.cos.jwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.config.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 이 필터는 jwt 토큰의 유효성을 확인하는 필터로 유효하다면 민감정보에 접근할 수 있도록 함

// 시큐리티가 filter를 가지고 있는데 그 필터중에 BasicAuthenticationFilter 라는 것이 있음.
// 권한이나 인증이 필요한 특정 주소를 요청했을때 위 필터를 무조건 타게 되어있음.
// 만약 권한이나 인증이 필요한 주소가 아니라면 이 필터를 안탐
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    //인증이나 권한이 필요한 주소요청이 있을 때 해당 필터를 타게 됨 (SecurityConfig.java 에서 antMatcher로 권한필요로 만든것들)
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        super.doFilterInternal(request, response, chain);
        System.out.println("인증이나 권한이 필요한 주소 요청이 되어서 BasicAuthenticationFilter를 상속받는 JwtAuthorizationFilter 탔음");

        String jwtHeader = request.getHeader("Authorization");
        System.out.println("jwtHeader : "+ jwtHeader);

        // header가 있는지 확인
        if(jwtHeader == null || !jwtHeader.startsWith("Bearer")) // 헤더가 없거나 헤더의 맨앞이 Bearer이 아니면 문제가있음 우리 헤더는 맨앞에 다 Bearer로 나오게 JwtAuthenticationFilter.java 에서 만들었기때문
        {
            chain.doFilter(request, response); //(filterChain) chain은 필터체인의 다음 필터를 가리키고 doFilter는 다음필터를 호출해 넘어가겠다 라는 의미
            return;
        }

        // 여기서 가져온 JWT 토큰을 검증해 정상적인 사용자인지 확인할것
        String jwtToken = request.getHeader("Authorization").replace("Bearer ", ""); // 토큰앞에 Bearer 문자열 빼기

        String username = JWT.require(Algorithm.HMAC512("cos")).build().verify(jwtToken).getClaim("username").asString(); // jwt토큰을 가져와서 토큰안에 있는 값인 username값을 가져올거
        if(username != null) // username이 null이 아니면 서명이 정상적으로 된것
        {
            User userEntity = userRepository.findByUsername(username); // username이 db에 있는 회원이 맞는지 확인

            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());// JwtAuthenticationFilter.java에서는 로그인 시도를 통해 Authentication를 생성하지만 여기처럼 강제로 Authentication으로 만들수도 있다
            // 여기의 null 들어간 부분은 password를 넣는부분임 우리는 서비스를 통해 로그인을 진행하는게 아닌 강제로 진행시켜주는거니 걍 null로 username도 db에 있는게 위에서 확인이 되니 그걸 근거로 만들어준거
        }

    }
}

