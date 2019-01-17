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
    private String fabricName;
    private Double upgrade;
    private Double miningPerSecond;
    private String img;

    public Fabrics() {}

    public Fabrics(Double price, String fabricName, Double upgrade, Double miningPerSecond) {
        this.price = price;
        this.fabricName = fabricName;
        this.upgrade = upgrade;
        this.miningPerSecond = miningPerSecond;
    }

    public Fabrics(Double price, String fabricName, Double upgrade, Double miningPerSecond, String img) {
        this.price = price;
        this.fabricName = fabricName;
        this.upgrade = upgrade;
        this.miningPerSecond = miningPerSecond;
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

    public String getfabricName() {
        return fabricName;
    }

    public void setfabricName(String fabricName) {
        this.fabricName = fabricName;
    }

    public Double getupgrade() {
        return upgrade;
    }

    public void setupgrade(Double upgrade) {
        this.upgrade = upgrade;
    }

    public Double getminingPerSecond() {
        return miningPerSecond;
    }

    public void setminingPerSecond(Double miningPerSecond) {
        this.miningPerSecond = miningPerSecond;
    }

}
