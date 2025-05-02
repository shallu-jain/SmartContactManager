package com.example.dao;

import com.example.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);

    @Query("select u from User u where u.email = :email")
    public User getUserByUserName(@Param("email") String email);

    /*@Query("select u from User u where u.email = :email")
    public User existEmail(@Param("email") String email);*/

}
