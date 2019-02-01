package com.example.demo.controller;

import com.example.demo.domain.Fabrics;
import com.example.demo.domain.UserFabrics;
import com.example.demo.domain.Users;
import com.example.demo.repository.FabricsRepository;
import com.example.demo.repository.UserFabricsRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@ActiveProfiles("dashboardControllerMockProfile")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DashboardControllerTest {

    @Value("${stripe.apiKey}")
    private String myApiKey;

    @Value("${stripe.price}")
    private Integer price;

    @Value("${stripe.currency}")
    private String currency;

    @Value("${stripe.description}")
    private String description;

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private FabricsRepository fabricsRepository;

    @Autowired
    private UserFabricsRepository userFabricsRepository;

    public List<Fabrics> fabricsListFoTest = new ArrayList<Fabrics>(){
        {new Fabrics(1.0,"firstFabric",3.0,0.00001,"image-1");}
        {new Fabrics(5.0,"secondFabric",15.0,0.00006,"image-2");}
        {new Fabrics(10.0,"threadFabric",30.0,0.00015,"image-3");}
        {new Fabrics(50.0,"forthFabric",150.0,0.0008,"image-4");}
    };

    public List<Users> usersListFoTest = new ArrayList<Users>(){
        {new Users("firstUser","firstUser@some.net","USER","somePassword-1","someToken-1");}
        {new Users("secondUser","secondUser@some.net","USER","somePassword-2","someToken-2");}
        {new Users("threadUser","threadUser@some.net","USER","somePassword-3","someToken-3");}
        {new Users("forthUser","forthUser@some.net","USER","somePassword-4","someToken-4");}
    };

    @Test
    public void myFabricEverythingOK() throws JsonProcessingException, JSONException {
        Users userUser = new Users("SomeUser","User@some.net","USER","somePassword","user-token");

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String expectedResponse = "{\"fabrics\":"+ow.writeValueAsString(fabricsRepository.findAll())+"}";

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME,"user-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findById(7)).thenReturn(userUser);
        Mockito.when(fabricsRepository.findAll()).thenReturn(fabricsListFoTest);
        Mockito.when(usersRepository.findAll()).thenReturn(usersListFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/dashboard?id=7", HttpMethod.GET, new HttpEntity<String>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expectedResponse, response.getBody(), false);
        JSONAssert.assertEquals("{\"user\":"+ow.writeValueAsString(userUser)+"}", response.getBody(), false);
        JSONAssert.assertEquals("{\"users\":"+ow.writeValueAsString(usersRepository.findAll())+"}", response.getBody(), false);
    }

    @Test
    public void myFabricWrongId() {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "user-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findById(7)).thenReturn(userUser);

        ResponseEntity<String> response = rest.exchange("/api/user/dashboard?id=10", HttpMethod.GET, new HttpEntity<String>(headers), String.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void myFabricWithoutData()  {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "user-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findById(7)).thenReturn(userUser);

        ResponseEntity<String> response = rest.exchange("/api/user/dashboard", HttpMethod.GET, new HttpEntity<String>(headers), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void myFabricWrongToken()  {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "some-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findById(7)).thenReturn(userUser);

        ResponseEntity<String> response = rest.exchange("/api/user/dashboard", HttpMethod.GET, new HttpEntity<String>(headers), String.class);

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void factoryMarketListEverythingOK() throws JsonProcessingException, JSONException {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "user-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(fabricsRepository.findAll()).thenReturn(fabricsListFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/factory-market", HttpMethod.GET, new HttpEntity<String>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"fabrics\":"+ow.writeValueAsString(fabricsRepository.findAll())+"}", response.getBody(), false);

    }

    @Test
    public void factoryMarketListWrongToken() throws JsonProcessingException, JSONException {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "some-token");

        ResponseEntity<String> response = rest.exchange("/api/user/factory-market", HttpMethod.GET, new HttpEntity<String>(headers), String.class);

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }

    @Test
    public void bayFabricAllOk() throws JSONException {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");
        userUser.setSilverBalance(10.0);
        Fabrics fabric = new Fabrics(1.0, "OneOfFabric", 3.0, 0.00001);

        Map<String,String> request = new HashMap<>();
        request.put("userID","7");
        request.put("id","1");

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "user-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findById(7)).thenReturn(userUser);
        Mockito.when(fabricsRepository.findById(1)).thenReturn(fabric);

        ResponseEntity<String> response = rest.exchange("/api/user/buy-factory", HttpMethod.POST, new HttpEntity<>(request,headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"OK\"}", response.getBody(), false);
    }

    @Test
    public void bayFabricLuckOfMoney() {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");
        userUser.setSilverBalance(0.0);
        Fabrics fabric = new Fabrics(1.0, "OneOfFabric", 3.0, 0.00001);

        Map<String,String> request = new HashMap<>();
        request.put("userID","7");
        request.put("id","1");

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "user-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findById(7)).thenReturn(userUser);
        Mockito.when(fabricsRepository.findById(1)).thenReturn(fabric);

        ResponseEntity<String> response = rest.exchange("/api/user/buy-factory", HttpMethod.POST, new HttpEntity<>(request,headers), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void bayFabricUserNotFound() {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");
        userUser.setSilverBalance(10.0);
        Fabrics fabric = new Fabrics(1.0, "OneOfFabric", 3.0, 0.00001);

        Map<String,String> request = new HashMap<>();
        request.put("userID","77");
        request.put("id","1");

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "user-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findById(7)).thenReturn(userUser);
        Mockito.when(fabricsRepository.findById(1)).thenReturn(fabric);

        ResponseEntity<String> response = rest.exchange("/api/user/buy-factory", HttpMethod.POST, new HttpEntity<>(request,headers), String.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void bayFabricFabricNotFound() {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");
        userUser.setSilverBalance(10.0);
        Fabrics fabric = new Fabrics(1.0, "OneOfFabric", 3.0, 0.00001);

        Map<String,String> request = new HashMap<>();
        request.put("userID","7");
        request.put("id","5");

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "user-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findById(7)).thenReturn(userUser);
        Mockito.when(fabricsRepository.findById(1)).thenReturn(fabric);

        ResponseEntity<String> response = rest.exchange("/api/user/buy-factory", HttpMethod.POST, new HttpEntity<>(request,headers), String.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void upgradeAllOk() throws JSONException, JsonProcessingException {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");
        userUser.setSilverBalance(10.0);
        Fabrics fabric = new Fabrics(1.0,"SomeFabric",3.0,0.00001,"image-1");
        UserFabrics userFabric = new UserFabrics(userUser,fabric,1,0.00001);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "user-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(userFabricsRepository.findById(2)).thenReturn(userFabric);

        ResponseEntity<String> response = rest.exchange("/api/user/upgrade-factory/2", HttpMethod.PUT, new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"OK\"}", response.getBody(), false);
        JSONAssert.assertEquals("{\"fabrics\":"+ow.writeValueAsString(userFabricsRepository.findByMaster(userUser))+"}", response.getBody(), false);
    }

    @Test
    public void upgradeLackOfMoney() {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");
        userUser.setSilverBalance(1.0);
        Fabrics fabric = new Fabrics(1.0,"SomeFabric",3.0,0.00001,"image-1");
        UserFabrics userFabric = new UserFabrics(userUser,fabric,1,0.00001);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "user-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(userFabricsRepository.findById(2)).thenReturn(userFabric);

        ResponseEntity<String> response = rest.exchange("/api/user/upgrade-factory/2", HttpMethod.PUT, new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void upgradeInvalidId() {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");
        userUser.setSilverBalance(5.0);
        Fabrics fabric = new Fabrics(1.0,"SomeFabric",3.0,0.00001,"image-1");
        UserFabrics userFabric = new UserFabrics(userUser,fabric,1,0.00001);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "user-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(userFabricsRepository.findById(2)).thenReturn(userFabric);

        ResponseEntity<String> response = rest.exchange("/api/user/upgrade-factory/10", HttpMethod.PUT, new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void exchangeGoldToSilverOk() {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");
        userUser.setGoldBalance(2.0);

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "user-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findById(2)).thenReturn(userUser);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=100&myGoldCoins=-1&id=2", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void exchangeSilverToGoldOk() {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");
        userUser.setGoldBalance(0.0);
        userUser.setSilverBalance(300.0);

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "user-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findById(2)).thenReturn(userUser);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=-200&myGoldCoins=1&id=2", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void exchangeGoldToSilverChiter() {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");
        userUser.setGoldBalance(2.0);

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "user-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findById(2)).thenReturn(userUser);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=500&myGoldCoins=-5&id=2", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    public void exchangeSilverToGoldChiter() {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");
        userUser.setGoldBalance(0.0);
        userUser.setSilverBalance(300.0);

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "user-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findById(2)).thenReturn(userUser);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=-1000&myGoldCoins=5&id=2", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    public void exchangeGoldToSilverInvalidId() {
        Users userUser = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");
        userUser.setGoldBalance(2.0);

        HttpHeaders headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "user-token");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userUser);
        Mockito.when(usersRepository.findById(2)).thenReturn(userUser);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=100&myGoldCoins=-1&id=5", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }
}