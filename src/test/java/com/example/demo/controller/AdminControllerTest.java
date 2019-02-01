package com.example.demo.controller;

import com.example.demo.domain.Users;
import com.example.demo.repository.FabricsRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.security.Pac4jConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import junit.framework.Assert;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@ActiveProfiles("adminControllerMockProfile")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminControllerTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private FabricsRepository fabricsRepository;

    @Test
    public void factoryMarketList() throws JSONException, JsonProcessingException {

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String expectedResponse = "{\"fabrics\":"+ow.writeValueAsString(fabricsRepository.findAll())+"}";

        Users adminUser = new Users("Admin","Admin@some.net","ADMIN","somePassword","admin-token");

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME,"admin-token");

        Mockito.when(usersRepository.findByToken("admin-token")).thenReturn(adminUser);

        ResponseEntity<String> response = rest.exchange("/api/admin/factory-list", HttpMethod.GET, new HttpEntity<String>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expectedResponse, response.getBody(), false);
    }

    @Test
    public void addFabric() throws JSONException {
        Users adminUser = new Users("Admin","Admin@some.net","ADMIN","somePassword","admin-token");

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME,"admin-token");

        Mockito.when(usersRepository.findByToken("admin-token")).thenReturn(adminUser);

        Map<String,String> request = new HashMap<>();
        request.put("newPrice","1");
        request.put("newName","SomeName");
        request.put("newUpgrade","1");
        request.put("newMining","1");
        request.put("image","../immage/fab_none-13.jpg");

        ResponseEntity<String> response = rest.exchange("/api/admin/add-factory", HttpMethod.POST, new HttpEntity<>(request,headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"OK\"}", response.getBody(), false);
    }

    @Test
    public void tryAddFabricWithoutData() {
        Users adminUser = new Users("Admin","Admin@some.net","ADMIN","somePassword","admin-token");

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME,"admin-token");

        Mockito.when(usersRepository.findByToken("admin-token")).thenReturn(adminUser);

        Map<String,String> request = new HashMap<>();
        request.put("newPrice","1");
        request.put("newName","SomeName");
        request.put("newUpgrade",null);//NullPointerException generated
        request.put("newMining","1");
        request.put("image","../immage/fab_none-13.jpg");

        ResponseEntity<String> response = rest.exchange("/api/admin/add-factory", HttpMethod.POST, new HttpEntity<>(request,headers), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}