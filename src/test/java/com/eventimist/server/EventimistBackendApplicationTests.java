package com.eventimist.server;

import com.eventimist.server.dto.ai.AIEventClassificationResponseDTO;
import com.eventimist.server.service.AIEventClassificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EventimistBackendApplicationTests {

	@Autowired
	private AIEventClassificationService aiEventClassificationService;

	@Test
	void classifyEventTest() {

		AIEventClassificationResponseDTO response =
				aiEventClassificationService.classify(
						"Sailors' Cafe OpenMic Mondays",
						"Every Monday, Sailors' Cafe offers this stage to ANY ARTIST of ANY ART FORM for Free. Free to Perform. Free to Watch."
				);

		System.out.println("--------------------------------");
		System.out.println("Category: " + response.getCategory());
		System.out.println("Tags: " + response.getTags());
		System.out.println("--------------------------------");
	}

}