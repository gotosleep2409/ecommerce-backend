package org.example.apitest.controller;

import lombok.RequiredArgsConstructor;
import org.example.apitest.service.ChatGPTService;
import org.example.apitest.service.FAQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatbotController {
    @Autowired
    private FAQService faqService;

    @Autowired
    private ChatGPTService chatGPTService;

    /*@PostMapping("/ask")
    public ResponseEntity<String> askQuestion(@RequestBody Map<String, String> request) {
        String question = request.get("question");

        String answer = faqService.getAnswer(question);
        if (answer != null) {
            return ResponseEntity.ok(answer);
        }

        answer = chatGPTService.getResponseFromChatGPT(question);
        return ResponseEntity.ok(answer.trim());
    }*/

    @PostMapping("/ask")
    public ResponseEntity<Map<String, String>> askQuestion(@RequestBody Map<String, String> request) {
        String question = request.get("question");

        String answer = faqService.getAnswer(question);
        if (answer != null) {
            return ResponseEntity.ok(Map.of("answer", answer));
        }

        answer = chatGPTService.getResponseFromChatGPT(question);
        return ResponseEntity.ok(Map.of("answer", answer.trim()));
    }
}
