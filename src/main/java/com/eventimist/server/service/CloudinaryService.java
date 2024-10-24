package com.eventimist.server.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    String  CloudinaryImageUpload (MultipartFile file);

}
