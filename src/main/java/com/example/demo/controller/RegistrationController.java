package com.example.demo.controller;

import com.example.demo.domain.RoleType;
import com.example.demo.domain.Users;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@RestController
public class RegistrationController {

    private final UsersRepository usersRepository;

    private final MailSender mailSender;

    @Autowired
    public RegistrationController(UsersRepository usersRepository, MailSender mailSender) {
        this.usersRepository = usersRepository;
        this.mailSender = mailSender;
    }

    @PostMapping("/api/guest/log-in")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        Users user = usersRepository.findByNameAndPassword(body.get("name"), md5Hex(body.get("password")));
        if (user == null) {
            response.put("message", "AbortNameOrPassword");
        } else {
            if (user.getActivationCode().equals("true")) {
                response.put("message", user.getToken());
                response.put("id", user.getId());
                response.put("role", user.getuserRole());
                response.put("email", user.getEmail());
            } else
                response.put("message", "AbortCode");
        }
        return response;
    }

    @PostMapping("/api/guest/registration")
    public Map<String, Object> addUser(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        if (usersRepository.findByName(body.get("name")) == null) {
            Users user = new Users(
                    body.get("name"),
                    body.get("email").toLowerCase(),
                    0.0,
                    0.0,
                    RoleType.USER.toString(),
                    md5Hex(body.get("password")),
                    md5Hex(body.get("password") + body.get("name")),
                    0,
                    1,
                    0.00001);
            user.setActivationCode(UUID.randomUUID().toString());
            usersRepository.save(user);
            String message = String.format(
                    "Hello %s! \n" +
                            "Welcome to 'Oligarch'. " +
                            "\n" +
                            "To complete registration, please follow the link.: - http://localhost:8080/activation.html?code=%s",
                    user.getName(), user.getActivationCode()
            );
            mailSender.send(user.getEmail(), "Activation code", message);
            response.put("name", user.getName());
        } else
            response.put("message", "Abort");
        return response;
    }

    @GetMapping("/activation.html")
    public String activation(@RequestParam String code) {
        Users user = usersRepository.findByActivationCode(code);
        if (user != null) {
            user.setActivationCode("true");
            usersRepository.save(user);
            return "<p><div><h3 align=\"center\">You have successfully completed the registration and you can log into the game using your name and password.</h3></div></p>" +
                    " \n" +
                    "<a href=\"login.html\">Login page</a>";
        } else {
            return "<p><div><h3 align=\"center\">Sorry something went wrong. Repeat registration will help resolve this error.</h3></div></p>" +
                    " \n" +
                    "<a href=\"registration.html\">Registration page</a>";
        }
    }
}
