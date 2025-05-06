package com.example.dao;

import com.example.entities.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

    /*@Query("from Contact as c where c.user.id =:userId")
    public List<Contact> findContactByUser(@Param("userId") int userId);*/

    // now we have to implement Pagination, so that's why i have changed the above method.
    @Query("from Contact as c where c.user.id =:userId")
    public Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageable);
}
