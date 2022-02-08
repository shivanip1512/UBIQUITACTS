package com.shivani.management.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.websocket.Session;

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
//		System.out.println("user : " + user);
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

//			System.out.println("contact : " + contact);
			User user = (User) model.getAttribute("user");

			addContactProfilePhoto(contact, file);

			// bidirectional mapping
			user.getContacts().add(contact);
			contact.setUser(user);

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

	private void addContactProfilePhoto(Contact contact, MultipartFile file) throws IOException {
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
		// incase user has profile pic and he updates his profile
		else if (contact.getImage() == null) {
			String image = this.contactRepository.findById(contact.getcId()).get().getImage();
			contact.setImage(image);
		}
	}

	private void deleteContactProfilePhoto(Contact contact) throws IOException {
		int id = contact.getcId();
		Contact oldContact = this.contactRepository.findById(id).get();
		/* deleting file */
		if (oldContact.getImage() != null) {
			File deleteFile = new ClassPathResource("static/images/contact/").getFile();
			File file = new File(deleteFile, oldContact.getImage());
			file.delete();
			contact.setImage(null);
			System.out.println("Image deleted..");
		}
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

		return "normal/show_contacts";
	}

	@GetMapping("/{cId}/contact-details")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model) {
//		System.out.println("cid :" + cId);

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

	@GetMapping("/delete-contact/{cId}")
	public String deleteContactHandler(@PathVariable("cId") Integer cId, Model model, HttpSession session) {
		User user = (User) model.getAttribute("user");
		Contact contact = this.contactRepository.findById(cId).get();

		boolean match = isValidContactOfUser(user, contact);
		try {
			if (match) {
				// delete image of contact
				deleteContactProfilePhoto(contact);
				// delete entry in db
				contact.setUser(null);
				this.contactRepository.delete(contact);
				session.setAttribute("message", new Message("Contact deleted Successfully !", "alert-success"));
			}
		} catch (IOException e) {
			System.out.println("ERROR : " + e.getMessage());
			e.printStackTrace();
			model.addAttribute("contact", contact);
			session.setAttribute("message", new Message("Something went Wrong! " + e.getMessage(), "alert-danger"));
		}

		return "redirect:/user/show-contacts/0";
	}

	private boolean isValidContactOfUser(User user, Contact contact) {
		boolean match = false;
		if (user != null && contact != null)
			match = user.getContacts().stream().anyMatch(c -> contact.equals(c));
		return match;
	}

	@GetMapping("/update-contact/{cId}")
	public String updateContactHandler(@PathVariable("cId") Integer cId, Model model) {
		model.addAttribute("pageTitle", "Edit Contact");
		Contact contact = this.contactRepository.findById(cId).get();
		model.addAttribute("contact", contact);
		return "normal/update_contact_form";
	}

	@PostMapping("/update-process-contact")
	public String updateContactProcessingHandler(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file, @RequestParam("cId") Integer cId, Model model,
			HttpSession session) {
		User user = (User) model.getAttribute("user");

		boolean isContactOfUser = isValidContactOfUser(user, contact);
		if (isContactOfUser) {
			try {
				deleteContactProfilePhoto(contact);
				addContactProfilePhoto(contact, file);
				contact.setUser(user);
				this.contactRepository.save(contact);
				session.setAttribute("message", new Message("Contact Updated Successfully !", "alert-success"));
			} catch (IOException e) {
				System.out.println("ERROR : " + e.getMessage());
				e.printStackTrace();
				model.addAttribute("contact", contact);
				session.setAttribute("message", new Message("Something went Wrong! " + e.getMessage(), "alert-danger"));
			}
		}

		return "redirect:/user/" + contact.getcId() + "/contact-details";
	}

	@GetMapping("/contact/delete-image/{cId}")
	public String deleteContactImageHandler(@PathVariable("cId") Integer cId, Model model, HttpSession session) {
		Contact contact = this.contactRepository.findById(cId).get();
		try {
			deleteContactProfilePhoto(contact);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Photo deleted Successfully !", "alert-success"));
		} catch (IOException e) {
			System.out.println("ERROR : " + e.getMessage());
			e.printStackTrace();
			model.addAttribute("contact", contact);
			session.setAttribute("message", new Message("Something went Wrong! " + e.getMessage(), "alert-danger"));
		}

		return "redirect:/user/" + contact.getcId() + "/contact-details";
	}
}
