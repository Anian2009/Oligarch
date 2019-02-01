package com.example.demo.repository;

import com.example.demo.domain.Users;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadAndWriteToDB {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private UsersRepository usersRepository;


    @Test
    public void readAndWriteToDB() {

        Users userToDB = new Users("UserForTest","Admin@some.net","ADMIN","somePassword","admin-token");

        usersRepository.save(userToDB);
        System.out.println(userToDB+" - write to db");

        Users userFromDB = usersRepository.findByName("UserForTest");
        System.out.println(userFromDB+" - read from db");

        usersRepository.delete(userFromDB);
        if (usersRepository.findByName("UserForTest")==null){
            System.out.println(userFromDB+" - deleted from db");
            System.out.println(">>>>>>>>>> READ, WRITE and DELETE are WORKS GOOD. <<<<<<<<<<");
        }

    }
}