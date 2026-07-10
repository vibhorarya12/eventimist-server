package com.eventimist.server.service;

import com.eventimist.server.entities.EventEntity;

public interface EventImportService {

    EventEntity importEvent (String url);
}
