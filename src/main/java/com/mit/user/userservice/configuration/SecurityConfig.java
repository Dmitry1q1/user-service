package com.mit.user.userservice.configuration;

import com.mit.user.userservice.component.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .logout().disable()
//                .logout()
//                .and()
//                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                .logoutSuccessUrl("/courses/").deleteCookies("JSESSIONID")
//                .invalidateHttpSession(true)
//                .and()
                .authorizeRequests()

                .antMatchers("/").permitAll()
                .antMatchers("/courses/").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/registration").permitAll()
                .antMatchers("/logoutMy").permitAll()
                .antMatchers(HttpMethod.POST, "/courses/").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/courses/{\\d+}/").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/courses/{\\d+}/problems/?problemId={\\d+}").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/courses/{\\d+}/problems/?problemId={\\d+}").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/problems/").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/problems/{\\d+}").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/problems/{\\d+}").hasRole("ADMIN")
                .antMatchers("/solution/").hasRole("ADMIN")
//                .antMatchers("/courses/").permitAll()
//                .antMatchers(HttpMethod.GET, "/vehicles/**").permitAll()
//                .antMatchers(HttpMethod.DELETE, "/*/*").hasRole("ADMIN")
//                .antMatchers(HttpMethod.GET, "/v1/vehicles/**").permitAll()
                .anyRequest().authenticated()
//                .anyRequest().permitAll()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }

}

