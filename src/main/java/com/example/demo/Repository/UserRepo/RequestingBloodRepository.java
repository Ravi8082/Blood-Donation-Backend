package com.example.demo.Repository.UserRepo;

import com.example.demo.Entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface RequestingBloodRepository extends JpaRepository<Request, Integer> {
    List<Request> findByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE request SET status = 'accept' WHERE email = ?1", nativeQuery = true)
    void updateStatus(String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE request SET status = 'reject' WHERE email = ?1", nativeQuery = true)
    void rejectStatus(String email);
}
