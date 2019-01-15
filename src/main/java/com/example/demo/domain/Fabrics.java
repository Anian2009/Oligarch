package com.example.demo.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "fabrics")
public class Fabrics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Double price;
    private String fabric_name;
    private Double upgrad;
    private Double mining_p_s;
    private String img;

    public Fabrics() {}

    public Fabrics(Double price, String fabric_name, Double upgrad, Double mining_p_s) {
        this.price = price;
        this.fabric_name = fabric_name;
        this.upgrad = upgrad;
        this.mining_p_s = mining_p_s;
    }

    public Fabrics(Double price, String fabric_name, Double upgrad, Double mining_p_s, String img) {
        this.price = price;
        this.fabric_name = fabric_name;
        this.upgrad = upgrad;
        this.mining_p_s = mining_p_s;
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getFabric_name() {
        return fabric_name;
    }

    public void setFabric_name(String fabric_name) {
        this.fabric_name = fabric_name;
    }

    public Double getUpgrad() {
        return upgrad;
    }

    public void setUpgrad(Double upgrad) {
        this.upgrad = upgrad;
    }

    public Double getMining_p_s() {
        return mining_p_s;
    }

    public void setMining_p_s(Double mining_p_s) {
        this.mining_p_s = mining_p_s;
    }

}
