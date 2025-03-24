package com.example.demo.Entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "donor", indexes = {
        @Index(name = "idx_bloodgroup", columnList = "bloodgroup"),
        @Index(name = "idx_gender", columnList = "gender"),
        @Index(name = "idx_city", columnList = "city")
})
public class Donor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 10)
    private String bloodgroup;

    @Column(nullable = false)
    private int units;

    @Column(nullable = false, unique = true, length = 15)
    private String mobile;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 20)
    private String date;

    public Donor() {
        super();
    }
}
