package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Ward;
import com.Heart2Hub.Heart2Hub_Backend.entity.WardClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WardClassRepository extends JpaRepository<WardClass, Long> {

    List<WardClass> findByWardClassNameContainingIgnoreCase(String name);
}
