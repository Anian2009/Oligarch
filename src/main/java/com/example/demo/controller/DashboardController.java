package com.example.demo.controller;

import com.example.demo.domain.Fabrics;
import com.example.demo.domain.UserFabrics;
import com.example.demo.domain.Users;
import com.example.demo.repository.FabricsRepository;
import com.example.demo.repository.UserFabricsRepository;
import com.example.demo.repository.UsersRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DashboardController {

    @Value("${stripe.apiKey}")
    private String myApiKey;

    @Value("${stripe.price}")
    private Integer price;

    @Value("${stripe.currency}")
    private String currency;

    @Value("${stripe.description}")
    private String description;

    @Value("${exchange.rateGold}")
    private Integer rateGold;

    private final static Integer UNO_LEVEL_MOR = 1;

    private final static Integer HAVE_STATUS = 1;

    private final static Integer HAVE_NO_STATUS = 0;

    private final FabricsRepository fabricsRepository;

    private final UserFabricsRepository userFabricsRepository;

    private final UsersRepository usersRepository;

    @Autowired
    public DashboardController(FabricsRepository fabricsRepository, UserFabricsRepository userFabricsRepository, UsersRepository usersRepository) {
        this.fabricsRepository = fabricsRepository;
        this.userFabricsRepository = userFabricsRepository;
        this.usersRepository = usersRepository;
    }

    @GetMapping("api/user/dashboard")
    public ResponseEntity<Map<String, Object>> myFabric(@RequestParam Integer id) {
        Map<String, Object> response = new HashMap<>();
        Users user = usersRepository.findById(id);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        List<UserFabrics> fabrics = userFabricsRepository.findByMaster(user);
        List<Users> users = usersRepository.findAll();
        Collections.sort(users);
        response.put("fabrics", fabrics);
        response.put("user", user);
        response.put("users", users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("api/user/factory-market")
    public Map<String, Object> factoryMarketList() {
        Map<String, Object> response = new HashMap<>();
        List<Fabrics> fabrics = fabricsRepository.findAll();
        response.put("fabrics", fabrics);
        return response;
    }

    @GetMapping("api/user/buy-factory")
    public ResponseEntity<Map<String, Object>> bayFabric(@RequestParam Integer id, @RequestParam Integer userID) {
        Map<String, Object> response = new HashMap<>();
        Users user = usersRepository.findById(userID);
        Fabrics fabric = fabricsRepository.findById(id);
        if (user == null || fabric == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (user.getsilverBalance() < fabric.getPrice())
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        else {
            UserFabrics userFabric = new UserFabrics(user, fabric, fabric.getminingPerSecond());
            userFabricsRepository.save(userFabric);
            user.setincrease(user.getincrease() + fabric.getminingPerSecond());
            user.setsilverBalance(user.getsilverBalance() - fabric.getPrice());
            usersRepository.save(user);
        }
        response.put("message", "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/user/upgrade-factory")
    public ResponseEntity<Map<String, Object>> upgrade(@RequestParam Integer id) {
        Map<String, Object> response = new HashMap<>();
        UserFabrics fabric = userFabricsRepository.findById(id);
        Users user = fabric.getMaster();
        if (user==null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (user.getsilverBalance() < fabric.getFabric().getupgrade())
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        else {
            user.setincrease(user.getincrease() + fabric.getminingPerSecond());
            fabric.setminingPerSecond(fabric.getminingPerSecond() + fabric.getminingPerSecond());
            fabric.setfabricLevel(fabric.getfabricLevel()+UNO_LEVEL_MOR);
            user.setsilverBalance(user.getsilverBalance() - fabric.getFabric().getupgrade());
            usersRepository.save(user);
            userFabricsRepository.save(fabric);
            List<UserFabrics> fabrics = userFabricsRepository.findByMaster(user);
            response.put("fabrics", fabrics);
            response.put("message", "OK");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("api/user/buy-gold-status")
    public ResponseEntity<Map<String, Object>> buyGoldStatus(@RequestParam Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        Stripe.apiKey = myApiKey;
        String token = request.get("stripeToken").toString();
        Map<String, Object> params = new HashMap<>();
        params.put("amount", price);
        params.put("currency", currency);
        params.put("description", description);
        params.put("source", token);
        try {
            Charge charge = Charge.create(params);
            System.out.println(charge.getStatus());
            if (charge.getStatus().equals("succeeded")) {
                Users user = usersRepository.findById(Integer.parseInt(request.get("id").toString()));
                user.setsilverStatus(HAVE_NO_STATUS);
                user.setgoldStatus(HAVE_STATUS);
                usersRepository.save(user);
                response.put("message", "Ok");
            } else
                return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("api/user/exchange")
    public ResponseEntity<Map<String, Object>> exchange(@RequestParam Map<String, Object> request) {
        int coins = Integer.parseInt(request.get("coins").toString());
        Users user = usersRepository.findById(Integer.parseInt(request.get("id").toString()));
        if (user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        user.setgoldBalance(user.getgoldBalance() - coins);
        user.setsilverBalance(user.getsilverBalance() + (coins * rateGold));
        usersRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
