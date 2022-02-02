package com.shivani.management.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shivani.management.smart.dao.UserRepository;
import com.shivani.management.smart.entity.User;
import com.shivani.management.smart.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String homeHandler(Model model) {
		model.addAttribute("pageTitle", "Home - Ubiquitacts");
		return "home";
	}

	@GetMapping("/about")
	public String aboutHandler(Model model) {
		model.addAttribute("pageTitle", "About Us");
		return "about";
	}

	@GetMapping("/sign-up")
	public String signUpHandler(Model model) {
		model.addAttribute("pageTitle", "Sign-up Page");
		model.addAttribute("user", new User());
		return "sign-up";
	}

	@PostMapping("/do_register")
	public String registerUserHandler(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model m,
			HttpSession session) {
		try {
			if (!agreement) {
				System.out.println("You have not agreed to terms and conditions.");
				throw new Exception("You have not agreed to terms and conditions.");
			}

			if (bindingResult.hasErrors()) {
				System.out.println("Error in sign-up.. " + bindingResult);
				m.addAttribute("user", user);
				return "sign-up";
			}

			user.setRole("user");
			user.setActive(true);

			// insert user in db
			User result = this.userRepository.save(user);

			// pass new user obj to sign-up page
			m.addAttribute("user", new User());
			session.setAttribute("message",
					new Message(user.getName() + " registered successfully! ", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			m.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went Wrong! " + e.getMessage(), "alert-danger"));
		}
		return "sign-up";

	}
}
