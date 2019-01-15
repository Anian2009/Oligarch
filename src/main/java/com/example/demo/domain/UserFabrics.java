package com.example.demo.domain;

import javax.persistence.*;

@Entity
@Table(name = "user_fabrics_info")
public class UserFabrics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Users master;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fabric_id")
    private Fabrics fabric;

    private Integer fabric_leval;
    private Double fab_mining_p_s;

    public UserFabrics() {
    }

    public UserFabrics(Users master, Fabrics fabric, Integer fabric_leval, Double fab_mining_p_s) {
        this.master = master;
        this.fabric = fabric;
        this.fabric_leval = fabric_leval;
        this.fab_mining_p_s = fab_mining_p_s;
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

    public Integer getFabric_leval() {
        return fabric_leval;
    }

    public void setFabric_leval(Integer fabric_leval) {
        this.fabric_leval = fabric_leval;
    }

    public Double getFab_mining_p_s() {
        return fab_mining_p_s;
    }

    public void setFab_mining_p_s(Double fab_mining_p_s) {
        this.fab_mining_p_s = fab_mining_p_s;
    }
}
