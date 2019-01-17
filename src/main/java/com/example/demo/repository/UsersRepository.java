package com.example.demo.repository;

import com.example.demo.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByNameAndPassword (String name, String password);
    Users findByToken (String token);
    Users findById (Integer id);
    Users findByActivationCode(String activationCode);
    Users findByEmail(String email);
}
