package com.example.demo.controller;

import com.example.demo.domain.Users;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.MailSender;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegistrationControllerTest {

    private RegistrationController subject;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private MailSender mailSender;

    @Before
    public void setUp() {
        initMocks(this);
        subject = new RegistrationController(usersRepository, mailSender);
    }

    @Value("${exchange.rateGold}")
    public Integer rateGold;

    @Value("${exchange.rateSilver}")
    private Integer rateSilver;

    @Value("${stripe.price}")
    private Integer price;

    @Test
    public void activationCodeShouldReturnMessageOk() throws JSONException {
        Users user = new Users("Anian", "Anian2009@ukr.net", "USER", "12", "myToken");
        user.setActivationCode("aaaaa");
        given(usersRepository.findByActivationCode(user.getActivationCode())).willReturn(user);
        ResponseEntity<Map<String, Object>> message = subject.activationCode("aaaaa");
        assertEquals(HttpStatus.OK, message.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"OK\"}", String.valueOf(message.getBody()), false);
    }

    @Test
    public void activationCodeShouldReturnNotFound() {
        Users user = new Users("Anian", "Anian2009@ukr.net", "USER", "12", "myToken");
        user.setActivationCode("aaaaa");
        given(usersRepository.findByActivationCode(user.getActivationCode())).willReturn(user);
        ResponseEntity<Map<String, Object>> message = subject.activationCode("bbbbb");
        assertEquals(HttpStatus.NOT_FOUND, message.getStatusCode());
    }

    @Test
    public void loginShouldReturnNotFoundByEmail() {
        Users user = new Users("Anian", "Anian2009@ukr.net", "USER", "12", "myToken");
        user.setActivationCode("aaaaa");
        Map<String, String> request = new HashMap<>();
        request.put("name", "Some@some.com");
        request.put("password", "SomePassword");
        given(usersRepository.findByEmail(request.get("name"))).willReturn(null);
        ResponseEntity<Map<String, Object>> message = subject.login(request);
        assertEquals(HttpStatus.NOT_FOUND, message.getStatusCode());
    }

    @Test
    public void loginShouldReturnUnauthorizedByActivationCode() {
        Users user = new Users("Anian", "Anian2009@ukr.net", "USER", "12", "myToken");
        user.setActivationCode("aaaaa");
        Map<String, String> request = new HashMap<>();
        request.put("name", "Anian2009@ukr.net");
        request.put("password", "SomePassword");
        given(usersRepository.findByEmail(request.get("name"))).willReturn(user);
        ResponseEntity<Map<String, Object>> message = subject.login(request);
        assertEquals(HttpStatus.UNAUTHORIZED, message.getStatusCode());
    }

    @Test
    public void loginShouldReturnNotFoundByPassword() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
        Users user = new Users("Anian", "Anian2009@ukr.net", "USER", bCryptPasswordEncoder.encode("12"), "myToken");
        user.setActivationCode("true");
        Map<String, String> request = new HashMap<>();
        request.put("name", "Anian2009@ukr.net");
        request.put("password", "SomePassword");
        given(usersRepository.findByEmail(request.get("name"))).willReturn(user);
        ResponseEntity<Map<String, Object>> message = subject.login(request);
        assertEquals(HttpStatus.NOT_FOUND, message.getStatusCode());

    }

    @Test
    public void loginShouldReturnOK() throws JSONException {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
        Users user = new Users("Anian", "Anian2009@ukr.net", "USER", bCryptPasswordEncoder.encode("12"), "myToken");
        user.setActivationCode("true");
        Map<String, String> request = new HashMap<>();
        request.put("name", "Anian2009@ukr.net");
        request.put("password", "12");
        given(usersRepository.findByEmail(request.get("name"))).willReturn(user);
        ResponseEntity<Map<String, Object>> message = subject.login(request);
        assertEquals(HttpStatus.OK, message.getStatusCode());
        JSONAssert.assertEquals("{\"message\":" + user.getToken() + "," +
                "\"id\":" + user.getId() + "," +
                "\"role\":" + user.getuserRole() + "," +
                "\"email\":" + user.getEmail() + "," +
                "\"price\":" + price + "," +
                "\"rateGold\":" + rateGold + "," +
                "\"rateSilver\":" + rateSilver + "}", String.valueOf(message.getBody()), false);
    }

    @Test
    public void addUserShouldReturnLockedByEmail() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
        Users user = new Users("Anian", "Anian2009@ukr.net", "USER", bCryptPasswordEncoder.encode("12"), "myToken");
        user.setActivationCode("true");
        Map<String, String> request = new HashMap<>();
        request.put("name", "SomeName");
        request.put("email", "Anian2009@ukr.net");
        request.put("password", "SomePassword");
        given(usersRepository.findByEmail(request.get("email"))).willReturn(user);
        ResponseEntity<Map<String, Object>> message = subject.addUser(request);
        assertEquals(HttpStatus.LOCKED, message.getStatusCode());
    }

    @Test
    public void addUserShouldReturnOkAndNameNewUser() throws JSONException {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
        UsersRepository usersRepository = mock(UsersRepository.class);

        Map<String, String> request = new HashMap<>();
        request.put("name", "SomeName");
        request.put("email", "Some@some.com");
        request.put("password", "SomePassword");
        Users user = new Users("SomeName", "Some@some.com", "USER", bCryptPasswordEncoder.encode("SomePassword"), "SomeToken");

        when(usersRepository.save(user)).thenReturn(user);
        ResponseEntity<Map<String, Object>> message = subject.addUser(request);
        assertEquals(HttpStatus.CREATED, message.getStatusCode());
        JSONAssert.assertEquals("{\"name\":" + request.get("name") + "}", String.valueOf(message.getBody()), false);
    }

}