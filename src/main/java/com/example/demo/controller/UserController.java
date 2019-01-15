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
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private FabricsRepository fabricsRepository;

    @Autowired
    private UserFabricsRepository userFabricsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/api/user/dashboard")
    public Map<String, Object> myFabric(@RequestParam Integer id) {
        Map<String, Object> response = new HashMap<>();
        Users user = usersRepository.findById(id);
        Iterable<UserFabrics> fabrics = userFabricsRepository.findByMaster(user);
        List<Users> users = usersRepository.findAll();
        Collections.sort(users);
        response.put("fabrics", fabrics);
        response.put("user", user);
        response.put("users", users);
        return response;
    }

    @GetMapping("/api/user/factory-market")
    public Map<String, Object> factory_market_list() {
        Map<String, Object> response = new HashMap<>();
        Iterable<Fabrics> fabrics = fabricsRepository.findAll();
        response.put("fabrics", fabrics);
        return response;
    }

    @GetMapping("/api/user/buy-factory")
    public Map<String, Object> bayFabric(@RequestParam Integer id, @RequestParam Integer userID) {
        Map<String, Object> response = new HashMap<>();
        Users user = usersRepository.findById(userID);
        Fabrics fabric = fabricsRepository.findById(id);
        if (user.getSilver_balance() < fabric.getPrice())
            response.put("message", "Abort");
        else {
            UserFabrics userFabric = new UserFabrics(user, fabric, 1, fabric.getMining_p_s());
            userFabricsRepository.save(userFabric);
            user.setIncrice(user.getIncrice() + fabric.getMining_p_s());
            user.setSilver_balance(user.getSilver_balance() - fabric.getPrice());
            usersRepository.save(user);
            response.put("message", "OK");
        }
        return response;
    }

    @GetMapping("/api/user/upgrade-factory")
    public Map<String, Object> upgrade(@RequestParam Integer id) {
        Map<String, Object> response = new HashMap<>();
        UserFabrics fabric = userFabricsRepository.findById(id);
        Users user = fabric.getMaster();
        if (user.getSilver_balance() < fabric.getFabric().getUpgrad())
            response.put("message", "Abort");
        else {
            user.setIncrice(user.getIncrice() + fabric.getFab_mining_p_s());
            fabric.setFab_mining_p_s(2 * fabric.getFab_mining_p_s());
            fabric.setFabric_leval(fabric.getFabric_leval() + 1);
            user.setSilver_balance(user.getSilver_balance() - fabric.getFabric().getUpgrad());
            usersRepository.save(user);
            userFabricsRepository.save(fabric);
            Iterable<UserFabrics> fabrics = userFabricsRepository.findByMaster(user);
            response.put("fabrics", fabrics);
            response.put("message", "OK");
        }
        return response;
    }

    @GetMapping("api/user/buy-gold-status")
    public Map<String, Object> buyGoldStatus(@RequestParam Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        Stripe.apiKey = "sk_test_mnbdM2ZBI3jtghYkXzIiIb3q";
        String token = request.get("stripeToken").toString();
        Map<String, Object> params = new HashMap<>();
        params.put("amount", 2000);
        params.put("currency", "usd");
        params.put("description", "Example charge");
        params.put("source", token);
        try {
            Charge charge = Charge.create(params);
            System.out.println(charge.getStatus());
            if (charge.getStatus().equals("succeeded")) {
                Users user = usersRepository.findById(Integer.parseInt(request.get("id").toString()));
                user.setSilver_status(0);
                user.setGold_status(1);
                usersRepository.save(user);
                response.put("message", "Ok");
            } else
                response.put("message", "Abort");
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return response;
    }

    @GetMapping("api/user/exchange")
    public void exchange(@RequestParam Map<String, Object> request) {
        int coins = Integer.parseInt(request.get("coins").toString());
        Users user = usersRepository.findById(Integer.parseInt(request.get("id").toString()));
        user.setGold_balance(user.getGold_balance() - coins);
        user.setSilver_balance(user.getSilver_balance() + (coins * 100));
        usersRepository.save(user);
    }
}
