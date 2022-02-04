package com.shivani.management.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shivani.management.smart.dao.UserRepository;
import com.shivani.management.smart.entity.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository repository;

	@RequestMapping("/dashboard")
	public String dashboardHandler(Model model, Principal principal) {
		String name = principal.getName();
//		System.out.println("username : "+name);

		/* get user by username */
		User user = repository.getUserByUserName(name);
		model.addAttribute("user", user);

		System.out.println("user :" + user);

		return "normal/user_dashboard.html";
	}
	
}
