package com.lawzone.market.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.lawzone.market.externalLink.util.BootpayUtils;
import com.lawzone.market.oAuth2.CustomOAuth2AuthService;
import com.lawzone.market.oAuth2.CustomOidcUserService;
import com.lawzone.market.oAuth2.KakaoOAuth2UserService;
import com.lawzone.market.oAuth2.OAuth2AuthenticationFailureHandler;
import com.lawzone.market.oAuth2.OAuth2AuthenticationSuccessHandler;
import com.lawzone.market.user.UserSecurityService;
import com.lawzone.market.util.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	//private final UserSecurityService userSecurityService;
	
	//private final KakaoOAuth2UserService kakaoOAuth2UserService;
	
	private final  CustomOAuth2AuthService customOAuth2AuthService;
	
	private final  CustomOidcUserService customOidcUserService;

	private final  OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	
	private final  OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
	
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	
    private final JwtTokenUtil jwtTokenUtil;
    
	@Autowired
	private final CorsConfig corsConfig;
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.httpBasic().disable()
    		.csrf().disable()	
    		.addFilterBefore(corsConfig.corsFilter(),UsernamePasswordAuthenticationFilter.class)
    		.exceptionHandling()
    		.authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .accessDeniedHandler(jwtAccessDeniedHandler)
    		//.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
    		.and()
    			.sessionManagement()
    			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    		.and()
                .authorizeRequests()
                .antMatchers("/garak/admin/login").permitAll()
                .antMatchers("/css/**").permitAll()
                //.antMatchers("/**").permitAll()
                //.antMatchers("/**").hasRole("ADMIN")// 회원가입 경로는 인증없이 호출 가능
                .anyRequest().authenticated() // 나머지 경로는 jwt 인증 해야함
           .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/admin/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("Authorization")
    		.and()
    			.formLogin().disable()
    			.oauth2Login()
    				.userInfoEndpoint()
    				.oidcUserService(customOidcUserService)
    				.userService(customOAuth2AuthService)
    		.and()
    			.successHandler(oAuth2AuthenticationSuccessHandler)
    			.failureHandler(oAuth2AuthenticationFailureHandler)
//           .and()
//	        	.formLogin()
//	        	.loginPage("/admin/login")
//	        	.defaultSuccessUrl("/admin/order/list")	
//	        .and()
//                .logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
//                .logoutSuccessUrl("/market/login")
//                .invalidateHttpSession(true)
    		.and()
    			.addFilterAfter(new JwtAuthenticationFilter(jwtTokenUtil),UsernamePasswordAuthenticationFilter.class)
    		;
    }
	
//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
//		.and()
//			.csrf().disable()
//            .authorizeRequests().antMatchers("/member/*").permitAll()
//            .antMatchers("/**").permitAll()
//            .anyRequest().authenticated()
//        //.and()
//        	//.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
//            //.accessDeniedHandler(jwtAccessDeniedHandler)
//        .and()
//        	.sessionManagement()
//            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//		.and()
//			.oauth2Login().userInfoEndpoint()
//			//.userService(kakaoOAuth2UserService) 
//			//.defaultSuccessUrl("/login/oauth2/code/kakao")
//			//.defaultSuccessUrl("/login-success")
//			//.successHandler(oAuth2AuthenticationSuccessHandler)
//		;
//		
////        http.authorizeRequests().antMatchers("/**").permitAll()
////        .and()
////	        .csrf().ignoringAntMatchers("/h2-console/**","/question/**")
////	        .and()
////	        .headers()
////	        .addHeaderWriter(new XFrameOptionsHeaderWriter(
////	                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
////        .and()
////	        //.formLogin()
////	        //.loginPage("/user/login")
////	        //.defaultSuccessUrl("/")
////        	.oauth2Login()
////        .and()
////	        .logout()
////	        .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
////	        .logoutSuccessUrl("/")
////	        .invalidateHttpSession(true)
////        ;
//        return http.build();
//    }
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
