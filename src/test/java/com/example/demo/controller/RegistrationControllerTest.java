package com.example.demo.controller;

import com.example.demo.domain.Users;
import com.example.demo.repository.UsersRepository;
import com.example.demo.security.Pac4jConfig;
import com.example.demo.service.MailSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import junit.framework.Assert;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.mail.MailSendException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@ActiveProfiles("registrationControllerMockProfile")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationControllerTest {

    @Value("${exchange.rateGold}")
    private Integer rateGold;

    @Value("${exchange.rateSilver}")
    private Integer rateSilver;

    @Value("${stripe.price}")
    private Integer price;

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MailSender mailSender;

    @Test
    public void loginAllIsOk() throws JSONException {

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);

        Users userUser = new Users("SomeUser","SomeUser@some.net","USER", bCryptPasswordEncoder.encode("somePassword"),"user-token");
        userUser.setActivationCode("true");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findByEmail("SomeUser@some.net")).thenReturn(userUser);

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("message", userUser.getToken());
        expectedResponse.put("id", userUser.getId());
        expectedResponse.put("role", userUser.getUserRole());
        expectedResponse.put("email", userUser.getEmail());
        expectedResponse.put("price", price);
        expectedResponse.put("rateGold", rateGold);
        expectedResponse.put("rateSilver", rateSilver);

        ResponseEntity<String> response = rest.exchange("/api/guest/log-in?email=SomeUser@some.net&password=somePassword", HttpMethod.GET, new HttpEntity<>(null), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expectedResponse.toString(), response.getBody(), false);
    }

    @Test
    public void noActivationCode() {

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);

        Users userUser = new Users("SomeUser","SomeUser@some.net","USER", bCryptPasswordEncoder.encode("somePassword"),"user-token");
        userUser.setActivationCode("The user has not passed the activation code");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findByEmail("SomeUser@some.net")).thenReturn(userUser);

        ResponseEntity<String> response = rest.exchange("/api/guest/log-in?email=SomeUser@some.net&password=somePassword", HttpMethod.GET, new HttpEntity<>(null), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void loginInvalidEmail(){

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);

        Users userUser = new Users("SomeUser","SomeUser@some.net","USER", bCryptPasswordEncoder.encode("somePassword"),"user-token");
        userUser.setActivationCode("true");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findByEmail("SomeUser@some.net")).thenReturn(userUser);

        ResponseEntity<String> response = rest.exchange("/api/guest/log-in?email=SomeElseUser@some.net&password=somePassword", HttpMethod.GET, new HttpEntity<>(null), String.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void loginInvalidPassword(){

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);

        Users userUser = new Users("SomeUser","SomeUser@some.net","USER", bCryptPasswordEncoder.encode("somePassword"),"user-token");
        userUser.setActivationCode("true");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findByEmail("SomeUser@some.net")).thenReturn(userUser);

        ResponseEntity<String> response = rest.exchange("/api/guest/log-in?email=SomeUser@some.net&password=someElsePassword", HttpMethod.GET, new HttpEntity<>(null), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void loginMissingData(){

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);

        Users userUser = new Users("SomeUser","SomeUser@some.net","USER", bCryptPasswordEncoder.encode("somePassword"),"not-user-token");
        userUser.setActivationCode("true");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findByEmail("SomeUser@some.net")).thenReturn(userUser);

        ResponseEntity<String> response = rest.exchange("/api/guest/log-in?email=SomeUser@some.net&password=someElsePassword", HttpMethod.GET, new HttpEntity<>(null), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void registrationEverythingIsOk() throws JSONException {

        Map<String,String> request = new HashMap<>();
        request.put("name","SomeName");
        request.put("email","SomeUser@some.net");
        request.put("password","SomePassword");

        Mockito.when(usersRepository.findByEmail("SomeUser@some.net")).thenReturn(null);

        ResponseEntity<String> response = rest.exchange("/api/guest/registration", HttpMethod.POST, new HttpEntity<>(request,null), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"name\":\""+request.get("name")+"\"}", response.getBody(), false);
    }

    @Test
    public void registrationNotValidEmail() {

        Map<String,String> request = new HashMap<>();
        request.put("name","SomeName");
        request.put("email","SomeUser@some");//Invalid Email
        request.put("password","SomePassword");

        Mockito.when(usersRepository.findByEmail("SomeUser@some.net")).thenReturn(null);

        ResponseEntity<String> response = rest.exchange("/api/guest/registration", HttpMethod.POST, new HttpEntity<>(request,null), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void registrationEmailAlreadyExist() {

        Map<String,String> request = new HashMap<>();
        request.put("name","SomeName");
        request.put("email","SomeUser@some.net");
        request.put("password","SomePassword");

        Mockito.when(usersRepository.findByEmail("SomeUser@some.net")).thenReturn(new Users());//Email is already exists

        ResponseEntity<String> response = rest.exchange("/api/guest/registration", HttpMethod.POST, new HttpEntity<>(request,null), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void activationCode() throws JSONException {
        Users userUser = new Users("SomeUser","SomeUser@some.net","USER", "somePassword","user-token");
        userUser.setActivationCode("SomeCode");

        Mockito.when(usersRepository.findByActivationCode("SomeCode")).thenReturn(userUser);

        ResponseEntity<String> response = rest.exchange("/activation-code/SomeCode", HttpMethod.PUT, new HttpEntity<>(null), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"OK\"}", response.getBody(), false);

    }

    @Test
    public void activationCodeWrong() {
        Users userUser = new Users("SomeUser","SomeUser@some.net","USER", "somePassword","user-token");
        userUser.setActivationCode("SomeCode");

        Mockito.when(usersRepository.findByActivationCode("SomeCode")).thenReturn(userUser);

        ResponseEntity<String> response = rest.exchange("/activation-code/SomeElseCode", HttpMethod.PUT, new HttpEntity<>(null), String.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }
}