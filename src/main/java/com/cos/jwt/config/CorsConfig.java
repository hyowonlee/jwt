package com.cos.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration // 스프링에서 관리할 수 있도록 어노테이션 등록
public class CorsConfig {

    @Bean // 스프링에서 관리할 수 있도록 어노테이션 등록 메서드 위에 bean 어노테이션 쓰면 해당 메서드에서 반환하는 객체를 bean으로 등록해줌
    public CorsFilter corsFilter() // 이런 설정들을 스프링의 filter에 등록을 시켜줘야 함 그래서 이 함수를 필터에 등록해야됨 SecurityConfig.java에 등록
    {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 내 서버가 응답을 할 때 json을 자바스크립트에서 처리할 수 있게 할지를 설정하는 것 만약 false면 javascript로 요청을 했을때 응답이 없음
        config.addAllowedOriginPattern("*"); // 원래는 바로밑의 addAllowedOrigin가 강의에서 쓰는거였는데 업데이트로 setAllowCredentials(true) true로 설정시 addAllowedOrigin("*")의 *패턴을 동시에 사용할수 없도록 업데이트됨
        //config.addAllowedOrigin("*"); // 모든ip에서 응답을 허용하겠다
        config.addAllowedHeader("*"); // 모든 헤더에서 응답을 허용하겠다
        config.addAllowedMethod("*"); // 모든 put get post delete patch 등의 요청을 허용
        source.registerCorsConfiguration("/api/**", config); // 이 경로로 들어오는 모든 요청들은 다 config 변수에서 설정한 설정을 따라라

        return new CorsFilter(source);
    }
}
