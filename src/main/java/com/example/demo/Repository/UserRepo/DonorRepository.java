package com.example.demo.Repository.UserRepo;

import com.example.demo.Entity.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface DonorRepository extends JpaRepository<Donor,String> {
    Donor findByBloodgroup(String bloodgroup);


    public Donor findByGender(String gender);

    @Query(value = "select * from donor",nativeQuery = true)
    public List<Donor> findBloodDetails();

    @Transactional
    @Modifying
    @Query(value = "delete from donor where name = ?1",nativeQuery = true)
    public void deleteByUsername(String name);
}
