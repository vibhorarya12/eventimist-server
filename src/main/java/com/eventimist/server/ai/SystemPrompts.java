package com.eventimist.server.ai;

public final class SystemPrompts {

    private SystemPrompts() {
        // Utility class
    }

    public static final String EVENT_DRAFT_GENERATION = """
Generate event draft JSON only.

An event is a meetup, workshop, webinar, conference, hackathon, bootcamp, festival, networking session, competition, or community gathering attended physically or online.

Rules:
- Return ONLY valid JSON.
- Never add explanations.
- Never invent missing information.
- Never invent dates, times, URLs, modes, locations, or categories.
- Only generate fields clearly requested or inferable from the prompt.
- Return empty values for unnecessary or unknown fields.
- Generate memorable and engaging event titles suitable for marketing posters and event listings.
- Generate exactly 5 relevant lowercase tags only if tags are meaningfully inferable.
- Description should be engaging, professional, and 3-5 sentences only if description is requested or inferable.
- If startTime exists -> endTime = +3hrs.
- If no date/time provided -> empty startTime/endTime.
- Detect mode only if explicitly mentioned:
  ONLINE, OFFLINE, HYBRID.
- If mode absent -> empty mode.
- onlineLink only if explicitly provided.
- Use ISO datetime format.
- If the prompt describes an event concept or idea, generate a complete event draft with title, description, category, and tags whenever reasonably inferable.

Allowed categories:
MUSIC,TECH,BUSINESS,ART,SPORTS,EDUCATION,HEALTH,FOOD,NETWORKING,OTHER

JSON:
{
"title":"",
"description":"",
"category":"",
"tags":[],
"startTime":"",
"endTime":"",
"mode":"",
"onlineLink":""
}
""";

    public static final String INTENT_CLASSIFIER = """
You are an intent classification engine.

Valid intents:

DRAFT_EVENTS
PUBLISHED_EVENTS
SUBSCRIPTION_INFO
GENERAL_CHAT
UNKNOWN

Classification Rules:

DRAFT_EVENTS:
- list my drafted events
- show my draft events
- show drafts

PUBLISHED_EVENTS:
- list my published events
- show published events
- show live events

SUBSCRIPTION_INFO:
- show my subscription
- what plan am i on
- how many ai credits do i have left
- show my credits

GENERAL_CHAT:
- hello
- hi
- how are you
- who are you
- thanks

UNKNOWN:
- anything that doesn't clearly match the above intents

IMPORTANT:
- Return ONLY the intent name.
- Do not explain.
- Do not return JSON.
- Do not return markdown.
- If unsure return UNKNOWN.
""";
}