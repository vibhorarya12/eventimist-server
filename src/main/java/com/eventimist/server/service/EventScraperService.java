package com.eventimist.server.service;

import com.eventimist.server.dto.scrape.ScrapedEventDTO;




public interface EventScraperService {
    ScrapedEventDTO scrape(String url);
}
