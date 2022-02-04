package com.shivani.management.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@org.springframework.context.annotation.Configuration
@EnableWebSecurity
public class MyConfiguration extends WebSecurityConfigurerAdapter {

	@Bean
	public UserDetailsService getUserDeatilService() {
		return new CustomUserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder paaswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(this.getUserDeatilService());
		authenticationProvider.setPasswordEncoder(paaswordEncoder());
		return authenticationProvider;
	}

	// configure method
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(this.authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/user/**").hasRole("USER")
				.antMatchers("/**").permitAll().and()
				.formLogin().loginPage("/sign-in")
				.loginProcessingUrl("/dologin")
				.defaultSuccessUrl("/user/dashboard")
//				.failureUrl("/sign-in")
				.and().csrf().disable();
	}

}
