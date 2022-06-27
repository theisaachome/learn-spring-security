package com.isaachome.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig {

	
	private final PasswordEncoder passwordEncoder;
	
	public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests()
        		.antMatchers("/","index","/css/*","/js/*")
        		.permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();

        return http.build();
    }
    
    @Bean
    UserDetailsService userDetailsService() {
    	UserDetails studentUser =
    			User.builder()
    			.username("annasmith")
    			.password(passwordEncoder.encode("password"))
    			.roles(AppUserRoles.STUDENT.name())// ROLE_STUDENT spring security will generate.
    			.build();
    	var adminUser = User.builder()
    			.username("linda")
    			.password(passwordEncoder.encode("password"))
    			.roles(AppUserRoles.ADMIN.name())
    			.build();
    		return new InMemoryUserDetailsManager(studentUser,adminUser);	
    }
}