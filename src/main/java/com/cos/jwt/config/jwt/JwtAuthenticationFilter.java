package com.cos.jwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.config.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 이 필터가 있음.
// 원래는 /login 요청해서 username, password를 전송하면(post) UsernamePasswordAuthenticationFilter가 동작함
// 하지만 우리가 SecurityConfig.java에서 formlogin을 비활성화해서 로그인 페이지가 안뜨니 이 필터가 동작하지 않음 그래서 이 필터를 SecurityConfig.java에서
// 시큐리티 필터체인에 강제로 등록시켜줄것 그리고 localhost:8080/login 에 body부분에 id, pw를 강제로 넣어서 로그인 시도할것
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    // /login 요청을 하면 UsernamePasswordAuthenticationFilter이 필터가 낚아채서 로그인 시도를 위해서 자동 실행되는 함수
    // 여기 매개변수 request 안에 id, pw가 들어있음
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter : 로그인시도중");

        try {
            // form 형태의 로그인시 request에서 id pw를 가져오기
//            BufferedReader br = request.getReader();
//
//            String input = null;
//            while((input = br.readLine()) != null)
//            {
//                System.out.println(input);
//            }

            // json 형태의 로그인시 request에서 id pw를 가져오기
            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class);
            System.out.println(user);

            // 로그인 토큰 생성, form 로그인 하면 자동으로 해주는 거임 근데 지금 form로그인 안쓰니 우리가 해줌
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()); // 로그인 토큰생성
            //로그인 시도, 이거 실행시 PrincipalDetilsService의 loadUserByUsername() 이 함수가 실행됨
            //PrincipalDetilsService의 loadUserByUsername() 함수가 실행된 후 로그인이 정상이면 Authentication이 리턴됨
            Authentication authentication = // 이 authentication 객체가 그 노트에 정리했던 세션이 저장되는 객체임(내 로그인 정보가 담김)Authorization
                    authenticationManager.authenticate(authenticationToken); // authenticationManager를 통해 토큰을 날리면 인증을 해주는데 그러면 authentication변수에 내 로그인한 정보가 담김 (이 의미는 세션에 이 사용자가 담긴다는 의미)

            // authentication 객체가 세션영역에 저장됨 => 로그인이 되었다는 의미
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("로그인 완료됨 : " + principalDetails.getUser().getUsername()); // 로그인이 잘됐는지 객체 안에있는 id 한번 확인해본거 id,pw가 db에 있으면 로그인이 정상적으로 된것
            // return 될때 authentication 객체가 세션영역에 저장됨
            // return을 해서 굳이 세션영역에 집어넣는이유는 권한관리를 security가 대신 해주기때문에 편하려고 return 하는거
            // 굳이 JWT 토큰 쓰면서 세션을 만들 이유가 없는데 단지 권한 처리때문에 세션에 집어 넣는것
            return authentication;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //attemptAuthentication 함수 실행 후 인증이 정상적으로 되었으면 이 함수 successfulAuthentication가 실행됨
    // 여기서 JWT토큰을 만들어서 request 요청한 사용자에게 JWT토큰을 response해주면 됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication함수 실행됨 : 인증이 완료되었다는 의미");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        //HMAC512 방식으로 만든 토큰(RSA방식[공개,개인키]은 아니고 HASH 암호화방식)
        String jwtToken = JWT.create() // jwt토큰 만드는 빌더패턴
                .withSubject("cos토큰")//토큰이름
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 10)))//토큰만료시간
                .withClaim("id", principalDetails.getUser().getId()) //비공개 클레임으로 내가 넣고싶은 값을 넣는것 여기선 id와 username을 담음
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512("cos")); // 서명값으로 hmac512 알고리즘의 비밀키가 될것 HMAC알고리즘은 받은 값과 비밀키를 이어붙여 해쉬화하는 알고리즘

        response.addHeader("Authorization", "Bearer " + jwtToken);//사용자에게 응답하는 respose 헤더로 Authorization:jwtToken값 을 헤더에 넣고 응답하는 방식인 Bearer방식으로 헤더에 jwt토큰을 담아 응답
        //여기서 만든 토큰을 넣어서 전송하는데 우리는 항상 앞에 Bearer라는 문자열을 넣을것

        // 이 토큰을 이용해서 토큰의 유효성을 검사해 인증된다면 민감정보에 접근할수있는 필터가 있어야됨
    }
}

