package com.shivani.management.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
		System.out.println("user : " + user);
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
	public String processAddContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, Principal principal) {
		try {

			System.out.println("contact : " + contact);
			model.addAttribute("contact", contact);
			User user = (User) model.getAttribute("user");

			/* processing and uploading file */
			if (!file.isEmpty()) {
				String imgName = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + "_"
						+ file.getOriginalFilename();
				;
				contact.setImage(imgName);
				File saveFile = new ClassPathResource("static/images/contact/").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + imgName);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image uploaded..");
			}

			// bidirectional mapping
			user.getContacts().add(contact);
			contact.setUser(user);

			System.out.println("user : " + user);
			this.repository.save(user);

		} catch (Exception e) {
			System.out.println("ERROR : " + e.getMessage());
			e.printStackTrace();
		}
		return "normal/add_contact_form";
	}
}
