package com.example.demo.controller;

import com.example.demo.domain.Fabrics;
import com.example.demo.domain.Users;
import com.example.demo.repository.FabricsRepository;
import com.example.demo.security.Pac4jConfig;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import junit.framework.Assert;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminControllerTest {

    @Autowired
    private TestRestTemplate rest;

    private AdminController subject;

    @Mock
    private FabricsRepository fabricsRepository;

    @Before
    public void setUp() {
        initMocks(this);
        subject = new AdminController(fabricsRepository);

    }

    @Test
    public void factoryMarketList() {
        Fabrics someFabric = new Fabrics(1.0,"SomeName",2.0,0.00001,"/img/someImg");
        Fabrics anotherFabric = new Fabrics(2.0,"SomeName",4.0,0.00002,"/img/anotherImg");
        fabricsRepository.save(someFabric);
        fabricsRepository.save(anotherFabric);
        List<Fabrics> list = new ArrayList<>();
        list.add(someFabric);
        list.add(anotherFabric);
        Map<String,Object> testResponse = new HashMap<>();
        testResponse.put("fabrics",list);

        given(fabricsRepository.findAll()).willReturn(list);
        Map<String,Object> response = subject.factoryMarketList();
        assertEquals(testResponse, response);
    }

//    @Test
//    public void addFabric() {
//    }
}