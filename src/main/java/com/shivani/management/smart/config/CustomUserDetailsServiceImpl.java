package com.shivani.management.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.shivani.management.smart.dao.UserRepository;
import com.shivani.management.smart.entity.User;

public class CustomUserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// fetching user fm db
		User user = repository.getUserByUserName(username);
		if (user == null) {
			throw new UsernameNotFoundException("Could not find user!");
		}

		UserDetails details = new CustomUserDetails(user);
		return details;
	}

}
