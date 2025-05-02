package com.example.controller;

import com.example.dao.UserRepository;
import com.example.entities.Contact;
import com.example.entities.User;
import com.example.helper.Message;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

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
                                     Principal principal, HttpSession httpSession, RedirectAttributes redirectAttributes) {
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

            //model.addAttribute("message", new Message("Contact Saved Successfully", "alert-success"));
            // httpSession.setAttribute("message", new Message("Contact Saved Successfully", "success"));
            redirectAttributes.addFlashAttribute("message", new Message("Contact Saved Successfully", "success"));
            model.addAttribute("contact", new Contact());
            System.out.println("NO ERROR");
            System.out.println("New chsange");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("some exception occurred");
            //httpSession.setAttribute("message", new Message("Something went wrong!!!", "danger"));
            redirectAttributes.addFlashAttribute("message", new Message("Something went wrong !!!", "danger"));
            return "redirect:/user/addcontact";
        }
        //return "general/add_contact";
        return "redirect:/user/addcontact";
    }
}
