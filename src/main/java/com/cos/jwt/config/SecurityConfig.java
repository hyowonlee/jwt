package com.cos.jwt.config;


import com.cos.jwt.config.jwt.JwtAuthenticationFilter;
import com.cos.jwt.config.jwt.JwtAuthorizationFilter;
import com.cos.jwt.filter.MyFilter3;
import com.cos.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;


@Configuration // ioc 할수있게 빈 등록
@EnableWebSecurity // 이 스프링 시큐리티 활성화
@RequiredArgsConstructor // di를 생성자 주입으로 하기위해 간단한 방법 여기 선언된 corsFilter가 생성자 매개변수에 들어갈텐데 이 어노테이션을 쓰면 final변수 선언만으로도 생성자주입이 가능하게 됨
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter; //CorsConfig.java에서 우리가 만든 필터를 스프링시큐리티에 등록할거임 @RequiredArgsConstructor로 생성자 주입 되서 자동 di됨
    private final UserRepository userRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    //jwt로 서버를 만드려면 이런식으로 세션을 사용하지 않고 http 로그인 방식을 사용하지 않는등 기존의 방식을 쓰지않고 jwt 방식으로 사용함
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //http.addFilterBefore(new MyFilter3(), BasicAuthenticationFilter.class); //내가만든 필터인 MyFilter1은 springSecurityFilterChain에 등록이 안됨 타입이 필터라 그래서 필터체인 적용 전이나 후에 걸어라(addFilterBefore or addFilterAfter)
        // https://url.kr/1n4caw 여기 보면 시큐리티 필터체인 목록이 그림에 있는데 우린 BasicAuthenticationFilter적용 전에 우리걸 적용하겠다라는 의미
        // 이 방식은 시큐리티 필터체인에 필터를 적용시키는 방식으로 굳이 이런식으로 적용할 필요는 없다 우린 FilterConfig.java에서 따로 적용할거임
        // 만약 시큐리티 필터체인중에서도 가장 빨리 수행되는 SecurityContextPersistenceFilter의 before에 addfilter 해주면 가장빨리 실행되는 필터가 될것
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않겠다는 의미(stateless서버로 만들겠다)로 우리는 세션 대신 jwt 사용 예정
        //기본적으로 web은 stateless방식으로 연결이 계속되지 않음 하지만 state방식처럼 쓰려고 세션, 쿠키등등을 사용하는것
        .and()
        .addFilter(corsFilter) // CorsConfig.java에서 만든 필터를 등록, 컨트롤러에서 @CrossOrigin 어노테이션으로 등록해줄수도 있지만 이러면 스프링 시큐리티 인증이 필요한 요청이 오면 필터보다 스프링 시큐리티 인증을 먼저 체크하기때문에 필터 적용전에 다 거부됨
        //@CrossOrigin(인증이 필요없을때 필터등록하려 사용), 시큐리티 필터에 이런식으로 등록해주는건 인증이 있을때 필터등록하려 사용
        .formLogin().disable() //jwt 서버라 id pw를 <form>으로 전송하지 않을것 (form로그인을 안쓰니 스프링시큐리티 기본 로그인창인 /login url도 안먹힘)
        .httpBasic().disable() // http 로그인방식을 사용하지 않음
        .addFilter(new JwtAuthenticationFilter(authenticationManager())) // 이 객체는 우리가 만든 필터가 아닌 시큐리티 필터를 상속받고있기에 before, after를 붙일 필요없이 시큐리티 필터체인에 그냥 등록가능
        // 그리고 원래 로그인 진행하는 필터라 이 authenticationManager를 통해서 로그인을 진행해서 이것도 넘겨줘야함
        .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository)) // jwt 토큰을 검증하는 필터로 시큐리티 필터를 상속하므로 시큐리티 필터체인에 걸었음
        .authorizeRequests()
        .antMatchers("/api/v1/user/**") // 이 url 접속시
        .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") // 이 권한을 가진놈만 접속가능
        .antMatchers("/api/v1/manager/**") // 이 url 접속시
        .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") // 이 권한을 가진놈만 접속가능
        .antMatchers("/api/v1/admin/**") // 이 url 접속시
        .access("hasRole('ROLE_ADMIN')") // 이 권한을 가진놈만 접속가능
        .anyRequest().permitAll(); // 다른 요청은 전부 권한없이 접속가능


    }
}