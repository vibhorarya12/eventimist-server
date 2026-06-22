package com.eventimist.server.dto.publicDTO;

import com.eventimist.server.enums.EventCategory;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AIEventSearchFilterDTO {

    private EventCategory category;

    private Double radius;

    private LocalDate startDate;

    private LocalDate endDate;
}