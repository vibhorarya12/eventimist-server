package com.eventimist.server.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseDTO {

    private int status;
    private String message;
    private LocalDateTime timestamp;

    // ✅ Success helper
    public static ApiResponseDTO success(String message) {
        return new ApiResponseDTO(
                200,
                message,
                LocalDateTime.now()
        );
    }

    // ✅ Error helper
    public static ApiResponseDTO error(int status, String message) {
        return new ApiResponseDTO(
                status,
                message,
                LocalDateTime.now()
        );
    }
}