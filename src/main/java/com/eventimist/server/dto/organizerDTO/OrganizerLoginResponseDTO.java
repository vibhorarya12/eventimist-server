package com.eventimist.server.dto.organizerDTO;

import lombok.Data;

@Data
public class OrganizerLoginResponseDTO {
    private String name ;

    private String email ;

    private String bio ;

    private String profilePic ;

    private  String token;

    private  String coverImage;

    private  String location;


}
