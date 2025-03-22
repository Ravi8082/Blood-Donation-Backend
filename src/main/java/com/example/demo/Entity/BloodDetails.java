package com.example.demo.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BloodDetails {
    private String bloodgroup;
    private int count;
    private int units;

    public BloodDetails(String bloodgroup, int count, int units){
        this.bloodgroup = bloodgroup;
        this.count = count;
        this.units = units;
    }
}
