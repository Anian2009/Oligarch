package com.example.demo.domain;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class Users implements Comparable<Users> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;
    private String email;
    public Double silver_balance;
    private Double gold_balance;
    private String user_role;
    private String password;
    private String token;
    private Integer gold_status;
    private Integer silver_status;
    private Double incrice;
    private String activationCode;

    public Users() {
    }

    public Users(String name, String email, Double silver_balance, Double gold_balance, String user_role, String password, String token, Integer gold_status, Integer silver_status, Double incrice) {
        this.name = name;
        this.email = email;
        this.silver_balance = silver_balance;
        this.gold_balance = gold_balance;
        this.user_role = user_role;
        this.password = password;
        this.token = token;
        this.gold_status = gold_status;
        this.silver_status = silver_status;
        this.incrice = incrice;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getSilver_balance() {
        return silver_balance;
    }

    public void setSilver_balance(Double silver_balance) {
        this.silver_balance = silver_balance;
    }

    public Double getGold_balance() {
        return gold_balance;
    }

    public void setGold_balance(Double gold_balance) {
        this.gold_balance = gold_balance;
    }

    public String getUser_role() {
        return user_role;
    }

    public void setUser_role(String user_role) {
        this.user_role = user_role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getGold_status() {
        return gold_status;
    }

    public void setGold_status(Integer gold_status) {
        this.gold_status = gold_status;
    }

    public Integer getSilver_status() {
        return silver_status;
    }

    public void setSilver_status(Integer silver_status) {
        this.silver_status = silver_status;
    }

    public Double getIncrice() {
        return incrice;
    }

    public void setIncrice(Double incrice) {
        this.incrice = incrice;
    }

    @Override
    public int compareTo(Users o) {
        return o.silver_balance.compareTo(silver_balance);
    }
}
