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
    private Double silverBalance;
    private Double goldBalance;
    private String userRole;
    private String password;
    private String token;
    private Integer goldStatus;
    private Integer silverStatus;
    private Double increase;
    private String activationCode;

    public Users() {
    }

    public Users(String name, String email, Double silverBalance, Double goldBalance, String userRole, String password, String token, Integer goldStatus, Integer silverStatus, Double increase) {
        this.name = name;
        this.email = email;
        this.silverBalance = silverBalance;
        this.goldBalance = goldBalance;
        this.userRole = userRole;
        this.password = password;
        this.token = token;
        this.goldStatus = goldStatus;
        this.silverStatus = silverStatus;
        this.increase = increase;
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

    public Double getsilverBalance() {
        return silverBalance;
    }

    public void setsilverBalance(Double silverBalance) {
        this.silverBalance = silverBalance;
    }

    public Double getgoldBalance() {
        return goldBalance;
    }

    public void setgoldBalance(Double goldBalance) {
        this.goldBalance = goldBalance;
    }

    public String getuserRole() {
        return userRole;
    }

    public void setuserRole(String userRole) {
        this.userRole = userRole;
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

    public Integer getgoldStatus() {
        return goldStatus;
    }

    public void setgoldStatus(Integer goldStatus) {
        this.goldStatus = goldStatus;
    }

    public Integer getsilverStatus() {
        return silverStatus;
    }

    public void setsilverStatus(Integer silverStatus) {
        this.silverStatus = silverStatus;
    }

    public Double getincrease() {
        return increase;
    }

    public void setincrease(Double increase) {
        this.increase = increase;
    }

    @Override
    public int compareTo(Users o) {
        return o.silverBalance.compareTo(silverBalance);
    }
}
