package com.cos.jwt.config.jwt;

// 토큰만드는 설정값을 좀 더 깔끔하게 관리하기 위해 만든 인터페이스 JwtAuthenticationFilter랑 JwtAuthorizationFilter에서 이 설정값을 가져다 쓰면 되는것
// 하드코딩을 피하기위해 만드는 설정값
public interface JwtProperties {
    String SECRET = "cos"; // 우리 서버만 알고있는 비밀 값
    int EXPIRATION_TIME = 60000*10; // 10분 (1/1000초)
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
