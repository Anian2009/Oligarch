package com.example.demo.controller;

import com.example.demo.domain.Users;
import com.example.demo.repository.UsersRepository;
import com.example.demo.security.Pac4jConfig;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@ActiveProfiles("MyProfile")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminControllerTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    public void factoryMarketList() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME,"admin-token");
        Mockito.when(usersRepository.findByToken("admin-token")).thenReturn(new Users());
        ResponseEntity<String> response = rest.exchange("/api/admin/factory-list", HttpMethod.GET, new HttpEntity<String>(headers), String.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

//    @Test
//    public void addFabric() {
//    }
}