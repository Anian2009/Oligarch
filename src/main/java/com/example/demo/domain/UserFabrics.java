package com.example.demo.domain;

import com.example.demo.repository.UserFabricsRepository;
import com.example.demo.repository.UsersRepository;

import javax.persistence.*;

@Entity
@Table(name = "user_fabrics_info")
public class UserFabrics {




    private static final Integer START_LEVEL = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Users master;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fabric_id")
    private Fabrics fabric;

    @JoinColumn(name = "fabric_leval")
    private Integer fabricLevel;

    @JoinColumn(name = "fab_mining_p_s")
    private Double miningPerSecond;

    public UserFabrics() {
    }

    public UserFabrics(Users master, Fabrics fabric, Integer fabricLevel, Double miningPerSecond) {
        this.master = master;
        this.fabric = fabric;
        this.fabricLevel = fabricLevel;
        this.miningPerSecond = miningPerSecond;
    }

    public UserFabrics(Users user, Fabrics fabric, Double miningPerSecond) {
        this.master = user;
        this.fabric = fabric;
        this.fabricLevel = START_LEVEL;
        this.miningPerSecond = miningPerSecond;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Users getMaster() {
        return master;
    }

    public void setMaster(Users master) {
        this.master = master;
    }

    public Fabrics getFabric() {
        return fabric;
    }

    public void setFabric(Fabrics fabric) {
        this.fabric = fabric;
    }

    public Integer getfabricLevel() {
        return fabricLevel;
    }

    public void setfabricLevel(Integer fabricLevel) {
        this.fabricLevel = fabricLevel;
    }

    public Double getminingPerSecond() {
        return miningPerSecond;
    }

    public void setminingPerSecond(Double miningPerSecond) {
        this.miningPerSecond = miningPerSecond;
    }

}
