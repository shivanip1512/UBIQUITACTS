package com.shivani.management.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shivani.management.smart.dao.UserRepository;
import com.shivani.management.smart.entity.User;

@Controller
public class HomeController {

	@Autowired
	private UserRepository repository;
	
	@GetMapping("/test")
	@ResponseBody
	public String test() {
		User user = new User();
		user.setName("Shivani");
		user.setPassword("123");
		repository.save(user);
		return "working";
	}
}
