package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateDepartmentException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateImageDocumentException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ImageDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ImageDocumentService {

    ImageDocumentRepository imageDocumentRepository;

    public ImageDocumentService(ImageDocumentRepository imageDocumentRepository) {
        this.imageDocumentRepository = imageDocumentRepository;
    }

    public ImageDocument createImageDocument(ImageDocument imageDocument) throws UnableToCreateImageDocumentException {
        try {
            imageDocumentRepository.save(imageDocument);
            return imageDocument;
        } catch (Exception ex) {
            throw new UnableToCreateDepartmentException(ex.getMessage());
        }
    }
}
