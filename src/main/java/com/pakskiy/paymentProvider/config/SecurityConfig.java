//package com.pakskiy.paymentProvider.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
//@Configuration
//@EnableWebFluxSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
////    @Bean
////    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
////                                                         AuthenticationManager authenticationManager) {
////        return http
////                .csrf().disable()
////                .authorizeExchange()
////                .pathMatchers("/api/public/**").permitAll()
////                .anyExchange().authenticated()
////                .and()
////                .httpBasic()
////                .and()
////                .authenticationManager(authenticationManager)
////                .build();
////    }
////
////    @Bean
////    public AuthenticationManager authenticationManager(UserRepository userRepository, PasswordEncoder passwordEncoder) {
////        return new AuthenticationManager(userRepository, passwordEncoder);
////    }
//}