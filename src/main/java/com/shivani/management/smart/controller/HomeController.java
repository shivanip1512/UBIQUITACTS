package com.shivani.management.smart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String homeHandler(Model model) {
		model.addAttribute("pageTitle","Home - Ubiquitacts");
		return "home";
	}
	
	@GetMapping("/about")
	public String aboutHandler(Model model) {
		model.addAttribute("pageTitle","About Us");
		return "about";
	}
	
	@GetMapping("/sign-up")
	public String signUpHandler(Model model) {
		model.addAttribute("pageTitle","Sign-up Page");
		return "sign-up";
	}
}
