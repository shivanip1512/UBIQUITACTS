package com.shivani.management.smart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shivani.management.smart.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("select u from User u where u.email = :emailId")
	public User getUserByUserName(@Param("emailId") String email);
}
