package com.eventimist.server.exceptions;

// image upload exception for cloudinary //
public class ImageUploadException extends RuntimeException{

    public ImageUploadException (String message){
        super(message);
    }
}
