package com.shivani.management.smart.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shivani.management.smart.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
