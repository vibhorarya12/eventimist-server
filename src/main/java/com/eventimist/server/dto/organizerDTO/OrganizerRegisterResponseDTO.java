package com.eventimist.server.dto.organizerDTO;

public class OrganizerRegisterResponseDTO {
    private String name = "";


    private String email = "";

    private String bio = "";


    private String profilePic = "";

    private String token = "";
    public void setToken(String token) {
        this.token = token;
    }


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

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getToken() {
        return token;
    }
}
