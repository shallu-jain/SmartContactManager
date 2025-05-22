package com.example.dao;

import com.example.entities.Contact;
import com.example.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

    /*@Query("from Contact as c where c.user.id =:userId")
    public List<Contact> findContactByUser(@Param("userId") int userId);*/

    // now we have to implement Pagination, so that's why i have changed the above method.
    @Query("from Contact as c where c.user.id =:userId")
    public Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageable);

    public List<Contact> findByNameContainingAndUser(String name, User user);

    // based on Birth Date and Month
    @Query("SELECT c FROM Contact c WHERE c.user = :user AND FUNCTION('DAY', c.birthDate) = :day AND FUNCTION('MONTH', c.birthDate) = :month")
    public List<Contact> findContactBirthdayToday(@Param("user") User user, @Param("day") int day, @Param("month") int month);
}
