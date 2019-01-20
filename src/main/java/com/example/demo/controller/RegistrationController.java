package com.example.demo.controller;

import com.example.demo.domain.RoleType;
import com.example.demo.domain.Users;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@RestController
public class RegistrationController {

    @Value("${exchange.rateGold}")
    private Integer rateGold;

    @Value("${exchange.rateSilver}")
    private Integer rateSilver;

    @Value("${stripe.price}")
    private Integer price;

    private final UsersRepository usersRepository;

    private final MailSender mailSender;

    @Autowired
    public RegistrationController(UsersRepository usersRepository, MailSender mailSender) {
        this.usersRepository = usersRepository;
        this.mailSender = mailSender;
    }

    @PostMapping("/api/guest/log-in")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        Map<String, Object> response = new HashMap<>();
        Users user = usersRepository.findByEmail(body.get("name"));
        if (user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (!user.getActivationCode().equals("true"))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        if (!bCryptPasswordEncoder.matches(body.get("password"),user.getPassword()))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        response.put("message", user.getToken());
        response.put("id", user.getId());
        response.put("role", user.getuserRole());
        response.put("email", user.getEmail());
        response.put("price", price);
        response.put("rateGold", rateGold);
        response.put("rateSilver", rateSilver);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/api/guest/registration")
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody Map<String, String> body) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
        Map<String, Object> response = new HashMap<>();
        if (usersRepository.findByEmail(body.get("email")) == null) {
            Users user = new Users(
                    body.get("name"),
                    body.get("email").toLowerCase(),
                    RoleType.USER.toString(),
                    bCryptPasswordEncoder.encode(body.get("password")),
                    md5Hex(body.get("password") + body.get("name")));
            user.setActivationCode(UUID.randomUUID().toString());
            usersRepository.save(user);
            String message = String.format(
                    "Hello %s! \n" +
                            "Welcome to 'Oligarch'. " +
                            "\n" +
                            "To complete registration, please follow the link.: - http://localhost:8080/activation.html?code=%s",
                    user.getName(), user.getActivationCode());
            mailSender.send(user.getEmail(), "Activation code", message);
            response.put("name", usersRepository.findByEmail(body.get("email")).getName());
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        } else
        return new ResponseEntity<>(HttpStatus.LOCKED);
    }

    @GetMapping("activation-code")
    public ResponseEntity<Map<String, Object>> activationCode(@RequestParam String code) {
        Map<String, Object> response = new HashMap<>();
        Users user = usersRepository.findByActivationCode(code);
        if (user != null) {
            user.setActivationCode("true");
            usersRepository.save(user);
            response.put("message","OK");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
