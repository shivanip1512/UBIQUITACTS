package com.shivani.management.smart.controller;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shivani.management.smart.dao.UserRepository;
import com.shivani.management.smart.entity.Contact;
import com.shivani.management.smart.entity.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository repository;

	/* executes each time */
	@ModelAttribute
	public void addUserData(Model model, Principal principal) {
		String name = principal.getName(); // System.out.println("username : "+name);

		/* get user by username */
		User user = repository.getUserByUserName(name);
		model.addAttribute("user", user);
		System.out.println("user  :" + user);
	}

	@RequestMapping("/dashboard")
	public String dashboardHandler(Model model, Principal principal) {

		model.addAttribute("pageTitle", "My Dashboard");
		return "normal/user_dashboard.html";
	}

	@GetMapping("/add-contact")
	public String openAddContactFormHandler(Model model) {
		model.addAttribute("pageTitle", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@PostMapping("/process-contact")
	public String processAddContact(@ModelAttribute Contact contact, Model model, Principal principal) {
		System.out.println("contact : " + contact);
		model.addAttribute("contact", contact);
		User user = (User) model.getAttribute("user");

		// bidirectional mapping
		user.getContacts().add(contact);
		contact.setUser(user);

		System.out.println("user : " + user);
		this.repository.save(user);
		return "normal/add_contact_form";
	}

}
