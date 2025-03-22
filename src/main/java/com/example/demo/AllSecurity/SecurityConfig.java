package com.example.demo.AllSecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/authenticate"),
                                new AntPathRequestMatcher("/getUserProfile/**"),
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/register"),
                                new AntPathRequestMatcher("/verify"),
                                new AntPathRequestMatcher("/updateuser/**"),
                                new AntPathRequestMatcher("/bloodDetails"),
                                new AntPathRequestMatcher("/addDonor"),
                                new AntPathRequestMatcher("/addAsDonor"),
                                new AntPathRequestMatcher("/acceptstatus/**"),
                                new AntPathRequestMatcher("/rejectstatus/**"),
                                new AntPathRequestMatcher("/donorlist"),
                                new AntPathRequestMatcher("/requestHistory/**"),
                                new AntPathRequestMatcher("/getTotalUsers"),
                                new AntPathRequestMatcher("/getTotalDonors"),
                                new AntPathRequestMatcher("/getTotalBloodGroups"),
                                new AntPathRequestMatcher("/getTotalUnits"),
                                new AntPathRequestMatcher("/getTotalRequests/**"),
                                new AntPathRequestMatcher("/getTotalDonationCount/**"),
                                new AntPathRequestMatcher("/userlist"),
                                new AntPathRequestMatcher("/requestblood"),
                                new AntPathRequestMatcher("/forgot-password"),
                                new AntPathRequestMatcher("/reset-password"),
                                new AntPathRequestMatcher("/{userId}"),
                                new AntPathRequestMatcher("/resend-registration"),
                                new AntPathRequestMatcher("/resend-forgot-password")
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public HttpFirewall allowUrlEncodedNewlineHttpFirewall() {
        return new DefaultHttpFirewall();
    }
}
