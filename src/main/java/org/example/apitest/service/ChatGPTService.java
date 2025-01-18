package org.example.apitest.service;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatGPTService {
    private final String OPENAI_API_KEY = "sk-abcdqrstefgh5678abcdqrstefgh5678abcdqrst";
    private final String OPENAI_URL = "https://api.openai.com/v1/completions";

    public String getResponseFromChatGPT(String userMessage) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + OPENAI_API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject request = new JSONObject();
        request.put("model", "gpt-4o-mini");
        request.put("prompt", userMessage);
        request.put("max_tokens", 150);

        HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(
                OPENAI_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        return new JSONObject(response.getBody()).getJSONArray("choices")
                .getJSONObject(0)
                .getString("text");
    }
}
