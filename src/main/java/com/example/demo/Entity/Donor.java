package com.example.demo.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Donor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String bloodgroup;
    private int units;
    private String mobile;
    private String gender;
    private int age;
    private String city;
    private String address;
    private String date;
    public Donor(){
        super();
    }

}

