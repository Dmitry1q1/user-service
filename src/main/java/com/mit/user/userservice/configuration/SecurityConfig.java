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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

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
                .authorizeRequests()

                .antMatchers("/").permitAll()
                .antMatchers("/courses/").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/registration").permitAll()
                .antMatchers("/logout").permitAll()
                .antMatchers(HttpMethod.POST, "/courses/").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/users/{\\d+}").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/courses/{\\d+}").hasRole("ADMIN")
//                .antMatchers(HttpMethod.DELETE, "/courses/{\\d+}/?userId={\\d+}").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/courses/{\\d+}/problems/?problemId={\\d+}").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/courses/{\\d+}/problems/?problemId={\\d+}").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/problems/").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/problems/{\\d+}").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/problems/{\\d+}").hasRole("ADMIN")
                .antMatchers("/solution/").hasRole("ADMIN")
                .anyRequest().authenticated()
//                .anyRequest().permitAll()
                .and()
                .apply(new JwtConfigurator(jwtTokenProvider));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000/")
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(-1)
                .allowCredentials(true);
    }
}

