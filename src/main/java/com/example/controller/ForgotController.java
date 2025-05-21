package com.example.controller;

import com.example.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class ForgotController {

    @Autowired
    private EmailService emailService;

    Random random = new Random(1000);

    @RequestMapping("/forgot")
    public String forgotPassword(Model model) {
        System.out.println("ForgotController.java --->>> forgotPassword() ");
        model.addAttribute("title", "Forgot Password");
        return "forgot_password";
    }

    @PostMapping("/send_otp")
    public String otpGenerator(@RequestParam("email") String email, Model model) {
        System.out.println("ForgotController.java -->> otpGenerator()");
        System.out.println("Email " + email);
        model.addAttribute("title", "Verify OTP..");
        int otpValue = random.nextInt(9999);
        System.out.println(otpValue);

        String subject = "OTP from SCM";
        String message = "<h1> OTP = " + otpValue + "</h1>";
        String to = email;
        boolean isSend = emailService.sendEmail(to, subject, message);
        if (isSend) {
            return "change_password";
        } else {
            return "verify_otp";
        }
    }
}
