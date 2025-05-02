package com.example.controller;

import com.example.dao.UserRepository;
import com.example.entities.User;
import com.example.helper.Message;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping({"/", "/home", "/smartcontactmanager"})
    public String getHome(Model model) {
        System.out.println("In getHome()");
        model.addAttribute("title", "Home Page");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Logged-in user: " + auth.getPrincipal());
        System.out.println("Authorities: " + auth.getAuthorities());
        return "home";
    }

    @RequestMapping("/signup")
    public String getSignUp(Model model, HttpSession session) {
        System.out.println("In getSignUp()");
        model.addAttribute("title", "SignUp Page");

        Message message = (Message) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message");
        }
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "signup";
    }

    // for registering USER:
    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
                               @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
                               Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            if (bindingResult.hasErrors()) {
                System.out.println("ERROR " + bindingResult.toString());
                model.addAttribute("user", user);
                return "signup";
            }
            if (!agreement) {
                System.out.println("Not Agreed Terms & Conditions....");
                throw new Exception("Not Agreed Terms & Conditions....");
            }
            String email = user.getEmail();
            System.out.println(email);

            boolean existsByEmailAddress = userRepository.existsByEmail(email);
            if (existsByEmailAddress) {
                bindingResult.rejectValue("email", "emailExists", "Email Already used.Please enter another Email.");
                return "signup";
            }
            user.setRole(user.getRole());
            user.setEnabled(true);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            System.out.println("Agreement " + agreement);
            System.out.println("User " + user);

            User result = userRepository.save(user);
            model.addAttribute("user", new User());
            session.setAttribute("message", new Message("Successfully Registered", "alert-success"));
            return "redirect:/signup";
        } catch (Exception e) {
            e.printStackTrace();
            //model.addAttribute("user", user);
            redirectAttributes.addFlashAttribute("user", user);
            //session.setAttribute("message", new Message("Something went wrong " + e.getMessage(), "alert-danger"));
            model.addAttribute("message", new Message("Something went wrong " + e.getMessage(), "alert-danger"));
            //return "redirect:/signup";
            return "signup";
        }
    }

    // for login
    @RequestMapping(value = "/loginpage")
    public String getLoginPage(Model model, HttpSession session) {
        System.out.println("In Login Page");
        model.addAttribute("title", "Log In");
        Message message = (Message) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message");
        }
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "login";
    }

    @RequestMapping("/dologout")
    public String logOutControl(HttpServletRequest request, HttpServletResponse response, Model model) {
        System.out.println("In logOutControl() -> TestController.java");
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        model.addAttribute("message", new Message("You have been Logged out Successfully", "alert-success"));
        return "redirect:/login";
    }

    @RequestMapping(value = "/check_credential", method = RequestMethod.POST)
    public String checkLoginCredential(@ModelAttribute("user") User user, Model model, HttpServletRequest request) {
        String email = user.getEmail();
        String pass = user.getPassword();
        System.out.println(email + " " + pass);

        User byUserName = userRepository.getUserByUserName(email);
        if (null != byUserName && bCryptPasswordEncoder.matches(pass, byUserName.getPassword())) {
            System.out.println("Login Successful for user : " + email);
            // Create Authentication object
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(byUserName.getEmail(),
                            null, List.of(new SimpleGrantedAuthority(
                            byUserName.getRole())));

            // Set into security context
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // Optionally also store it in session
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            //model.addAttribute("user",byUserName);
            session.setAttribute("user", byUserName);
            //return "redirect:/home";
            return "redirect:/user/dashboard";
        } else {
            System.out.println("Invalid credentials for: " + email);
            model.addAttribute("message", new Message("InValid Credentials Entered", "alert-danger"));
            return "login";
        }
    }
}