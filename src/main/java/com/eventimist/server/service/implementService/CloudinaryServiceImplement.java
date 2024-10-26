package com.eventimist.server.service.implementService;

import com.cloudinary.Cloudinary;
import com.eventimist.server.exceptions.ImageUploadException;
import com.eventimist.server.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@Service
public class CloudinaryServiceImplement implements CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String CloudinaryImageUpload (MultipartFile file){

        try{
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return uploadResult.get("url").toString();
        }

        catch (IOException e){
            throw  new ImageUploadException("failed to upload image");
        }
    }



}
