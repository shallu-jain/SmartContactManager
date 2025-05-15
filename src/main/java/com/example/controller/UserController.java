package com.example.controller;

import com.example.dao.ContactRepository;
import com.example.dao.UserRepository;
import com.example.entities.Contact;
import com.example.entities.User;
import com.example.helper.Message;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    // adding common method to get the user
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();
        System.out.println("USERNAME : " + userName);
        User byUserName = userRepository.getUserByUserName(userName);
        model.addAttribute("user", byUserName);
    }

    @RequestMapping("/dashboard")
    public String dashboard(Model model) {
        System.out.println("In UserController.java -> dashboard()");
        model.addAttribute("title", "User Dashboard");
        return "general/user_dashboard";
    }

    // open the Add Contact Page controller.
    @RequestMapping("/addcontact")
    public String openAddContactForm(Model model) {
        System.out.println("UserController -> openAddContactForm");
        model.addAttribute("title", "Add Contact Form");
        model.addAttribute("contact", new Contact());
        return "general/add_contact";
    }

    @RequestMapping(value = "/process-contact", method = RequestMethod.POST)
    public String processContactForm(@Valid @ModelAttribute("contact") Contact contact, BindingResult bindingResult, Model model,
                                     Principal principal, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("UserController -> processContactForm()");
            if (bindingResult.hasErrors()) {
                System.out.println("ERROR : " + bindingResult.toString());
                model.addAttribute("contact", contact);
                return "general/add_contact";
            }
            String name = principal.getName();
            User user = userRepository.getUserByUserName(name);
            contact.setUser(user);
            user.getContact().add(contact);

            User save = userRepository.save(user);
            System.out.println("User Added " + save);

            redirectAttributes.addFlashAttribute("message", new Message("Contact Saved Successfully", "success"));
            model.addAttribute("contact", new Contact());
            System.out.println("NO ERROR");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("some exception occurred");
            redirectAttributes.addFlashAttribute("message", new Message("Something went wrong !!!", "danger"));
            return "redirect:/user/addcontact";
        }
        //return "general/add_contact";
        return "redirect:/user/addcontact";
    }

    // method for view_contact 5/5/2025
    @RequestMapping(value = "/view_contact/{page}")
    public String viewContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
        System.out.println("In UserController.java -> viewContacts()");
        model.addAttribute("title", "Show Contacts");
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        Pageable pageable = PageRequest.of(page, 5);
        Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);
        model.addAttribute("listOfContacts", contacts);

        // current page
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        return "general/view_contact";
    }

    // method to show particular contact detail based on ID, on contact_detail page
    // Created on 7/5/2025
    @RequestMapping(value = "/contact/{cId}")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
        try {
            model.addAttribute("title", "Contact Detail");
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);
            Optional<Contact> contact = this.contactRepository.findById(cId);
            Contact contactDetail = contact.get();
            if (user.getId() == contactDetail.getUser().getId()) {
                model.addAttribute("contactDetail", contactDetail);
            }
        } catch (Exception e) {
            System.out.println("error-message" + e);
        }
        return "general/contact_detail";
    }

    // method to delete the contact, based on the ID given.
    // Created on 7/5/2025
    @RequestMapping(value = "/deletecontact/{contactId}")
    public String deleteContactById(@PathVariable("contactId") Integer id, Principal principal) {
        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            Optional<Contact> contactById = this.contactRepository.findById(id);
            Contact contact = contactById.get();

            if (user.getId() == contact.getUser().getId()) {
                this.contactRepository.delete(contact);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return "redirect:/user/view_contact/0";
    }

    // method to update the contact, based on ID.
    // created on 12/5/2025
    @RequestMapping(value = "/updatecontact/{contactId}", method = {RequestMethod.POST, RequestMethod.GET})
    public String updateContactById(@PathVariable("contactId") Integer id, Model model) {
        System.out.println("UserController.java -> updateContactById()");
        model.addAttribute("title", "Update Contact");
        // get the contact details based on ID.
        Contact contact = this.contactRepository.findById(id).get();
        model.addAttribute("contactData", contact);
        return "general/update_contact";
    }

    // process the UPDATED Contact data details.
    @RequestMapping(value = "/process_update_contact_detail", method = RequestMethod.POST)
    public String processContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult bindingResult,
                                 Model model, Principal principal, RedirectAttributes redirectAttributes) {
        System.out.println("UserController.java -> processContact()");
        if (bindingResult.hasErrors()) {
            System.out.println("processContact() ERROR -> " + bindingResult.toString());
            model.addAttribute("contactData", contact);
            // return "redirect:/user/updatecontact/" + contact.getId();
            return "general/update_contact";
        }
        // old contact details
        Contact oldContactDetails = this.contactRepository.findById(contact.getId()).get();
        // used Principal, so that we can get to know who is updating the details.
        // but here the data will be shown only for the logged-in user only.
        User user = this.userRepository.getUserByUserName(principal.getName());
        contact.setUser(user);
        this.contactRepository.save(contact);
        redirectAttributes.addFlashAttribute("message", new Message("Contact Updated Successfully", "success"));
        return "redirect:/user/updatecontact/" + contact.getId();
    }
}
