package com.cos.jwt.config.auth;

import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// http://localhost:8080/login 원래는 스프링시큐리티 기본 로그인 주소인 이 주소 요청시 이게 동작함 하지만 우리가 formlogin을 SecurityConfig.java
// 에서 비활성화 해서 이 주소가 동작을 안하는데 그럼 로그인시 작동하는 필터인 UsernamePasswordAuthenticationFilter 가 작동을 안해서 이 클래스가 동작을 안함
// 그래서 직접 UsernamePasswordAuthenticationFilter이게 동작하게 해서 PrincipalDetailsService 이놈을 때려주는 필터를 하나 만들어야함 (JwtAuthenticationFilter.java)
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("PrincipalDetailsService의 loadUserByUsername() 실행");
        User userEntity = userRepository.findByUsername(username);
        System.out.println("userEntity: "+userEntity);
        return new PrincipalDetails(userEntity);
    }
}
