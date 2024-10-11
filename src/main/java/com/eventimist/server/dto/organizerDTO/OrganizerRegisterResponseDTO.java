package com.eventimist.server.dto.organizerDTO;

public class OrganizerRegisterResponseDTO {
    private String name;


    private String email;

    private String bio;

    public void setToken(String token) {
        this.token = token;
    }

    private String token;
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    private String profilePic;




}
