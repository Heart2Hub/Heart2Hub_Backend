package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageDocumentRepository extends JpaRepository<ImageDocument, Long> {

}
