package com.biglol.getinline.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.biglol.getinline.service.AdminService;

// Spring Security 5. 최신 버전은 6인데 많이 바껴서 새로 알아봐야 함
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // password encoder의 상태를 미리 한다. 상태에 따라서 다른 passwordencoder을 쓰겠다는 뜻임
    // 즉. Admin entity에 1234를 넣는데 security관련 prefix를 넣음 (예시: {noop}1234). 이걸 읽어서 그거에 따라 적당한 패스워드 선택
    // {bcrypt}를 쓰고 싶으면 암호화된 값을 넣어야 함
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(
            AuthenticationManagerBuilder auth,
            PasswordEncoder passwordEncoder,
            AdminService adminService)
            throws Exception {
        auth.userDetailsService(adminService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/events/**", "/places/**")
                .permitAll() // root, events, places는 인증 예외. /**는 ant 패턴... permitAl -> // 전부 허용하는
                // 예외 룰
                .anyRequest()
                .authenticated() // 그 어떤 request도 인증을 타야함
                .and()
                .formLogin()
                .permitAll() // 로그인 페이지는 누구나 허용
                .loginPage("/login") // 기본값이 /login이지만 직접 넣어주면 기본 login, logout페이지가 사라짐
                .defaultSuccessUrl("/admin/places")
                .and()
                .logout()
                .permitAll()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/");
        //                .and()
        //                .csrf()
        //                .disable();
        // 스프링 시큐리티가 기본적인 csrf 보안 기능을 갖추고 있으므로
        // 데이터를 저장, 수정하는 어드민 뷰 페이지에서는
        // csrf token 을 전달할 필요가 있음
        // 이에 토큰을 설정하는 히든 엘리먼트를 추가 (<input type="hidden" id="csrf">)

        // <attr sel="#csrf" th:value="${_csrf.token}" th:name="${_csrf.parameterName}" />를 넣어서 추가나 삭제버튼 누를 때 403에러가 뜨지 않게끔 방지
        // post submit 하는 곳은 토큰을 보내주도록
    }
}
