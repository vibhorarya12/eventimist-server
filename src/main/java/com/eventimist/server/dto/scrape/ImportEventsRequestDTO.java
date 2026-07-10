package com.eventimist.server.dto.scrape;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ImportEventsRequestDTO {

    @NotEmpty
    private List<String> urls;
}