package com.shivani.management.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.shivani.management.smart.dao.ContactRepository;
import com.shivani.management.smart.dao.UserRepository;
import com.shivani.management.smart.entity.Contact;
import com.shivani.management.smart.entity.User;
import com.shivani.management.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;

	/* executes each time */
	@ModelAttribute
	public void addUserData(Model model, Principal principal) {
		String name = principal.getName(); // System.out.println("username : "+name);

		/* get user by username */
		User user = userRepository.getUserByUserName(name);
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
			Model model, Principal principal, HttpSession httpSession) {
		try {

			System.out.println("contact : " + contact);
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
			this.userRepository.save(user);
			httpSession.setAttribute("message",
					new Message(contact.getName() + " added successfully!", "alert-success"));
			model.addAttribute("contact", new Contact());

		} catch (ConstraintViolationException e) {
			System.out.println("ERROR : " + e.getMessage());
			e.printStackTrace();
			model.addAttribute("contact", contact);
			httpSession.setAttribute("message", new Message("Please Check field values! ", "alert-danger"));
		} catch (Exception e) {
			System.out.println("ERROR : " + e.getMessage());
			e.printStackTrace();
			model.addAttribute("contact", contact);
			httpSession.setAttribute("message", new Message("Something went Wrong! " + e.getMessage(), "alert-danger"));
		}
		return "normal/add_contact_form";
	}

	@GetMapping("/show-contacts/{page}")
	public String showContactsHandler(@PathVariable("page") Integer page, Model model) {
		model.addAttribute("pageTitle", "Contacts");

		// list of contacts of logged in user
		User user = (User) model.getAttribute("user");

		// pagination
		// records per page - 5(n),
		// current page index - 0(page)
		Pageable pageRequest = PageRequest.of(page, 5);
		Page<Contact> contactsOfLoggedInUser = this.contactRepository.findContactsByUser(user.getId(), pageRequest);

		model.addAttribute("contacts", contactsOfLoggedInUser);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contactsOfLoggedInUser.getTotalPages());

		System.out.println("toatl :" + contactsOfLoggedInUser.getTotalPages());
		System.out.println("currentPage :" + page);

		return "normal/show_contacts";
	}

	@GetMapping("/{cId}/contact-details")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model) {
		System.out.println("cid :" + cId);

		User user = (User) model.getAttribute("user");

		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		if (user.getId() == contact.getUser().getId()) {

			String contactName = contact.getName().split(" ")[0];
			model.addAttribute("pageTitle", contactName + "'s Details");
			model.addAttribute("contact", contact);

			return "normal/show_contact_details";
		} else {
			model.addAttribute("pageTitle", "404 : Page Not Found");
			return "page_not_found.html";
		}

	}
}
