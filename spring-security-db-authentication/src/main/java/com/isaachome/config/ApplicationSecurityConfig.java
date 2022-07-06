package com.isaachome.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig {

	private final PasswordEncoder passwordEncoder;

	public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
		.csrf().disable()
		.authorizeRequests()
		.antMatchers("/", "index", "/css/*", "/js/*").permitAll()
		.antMatchers("/api/**").hasRole(AppUserRoles.STUDENT.name())
		.anyRequest()
		.authenticated()
		.and()
		.formLogin()
		.loginPage("/login").permitAll()
		.defaultSuccessUrl("/courses",true)
		.and()
		.rememberMe()
		.tokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(21))
		.key("verysomethingsecured")
		.and()
		.logout()
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout","GET"))
		.logoutUrl("/logout")
		.clearAuthentication(true)
		.invalidateHttpSession(true)
		.deleteCookies("JSESSIONID","remember-me")
		.logoutSuccessUrl("/login");
		
		return http.build();
	}

	@Bean
	UserDetailsService userDetailsService() {
		UserDetails studentUser = User.builder()
				.username("annasmith")
				.password(passwordEncoder.encode("password"))
//				.roles(AppUserRoles.STUDENT.name())// ROLE_STUDENT spring security will generate.
				.authorities(AppUserRoles.STUDENT.getGrantedAuthorities())
				.build();
		var adminUser = User.builder().username("linda").password(passwordEncoder.encode("password"))
//				.roles(AppUserRoles.ADMIN.name())
				.authorities(AppUserRoles.ADMIN.getGrantedAuthorities())
				.build();
		var tomUser = User.builder().username("tom").password(passwordEncoder.encode("password"))
//				.roles(AppUserRoles.ADMINTRAINEE.name())
				.authorities(AppUserRoles.ADMINTRAINEE.getGrantedAuthorities())
				.build();
		return new InMemoryUserDetailsManager(studentUser, adminUser, tomUser);
	}
}
