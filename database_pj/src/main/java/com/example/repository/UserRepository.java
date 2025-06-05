package com.example.repository;


import com.example.dto.UserDTO;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByName(String name);
    boolean existsByName(String name);

    User findByUsername(String username);


    boolean existsByUsername(String username);

    @Query("SELECT new com.example.dto.UserDTO(u.username, u.name, u.userId, u.contactInfo, u.registerTime) FROM User u")
    List<UserDTO> findAllUsersSummary();
}
