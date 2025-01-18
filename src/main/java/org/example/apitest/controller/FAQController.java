package org.example.apitest.controller;

import lombok.RequiredArgsConstructor;
import org.example.apitest.model.FAQ;
import org.example.apitest.service.FAQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faq")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FAQController {
    @Autowired
    private FAQService faqService;

    @PostMapping
    public ResponseEntity<FAQ> addOrUpdateFAQ(@RequestBody FAQ faq) {
        FAQ savedFAQ = faqService.addOrUpdateFAQ(faq.getQuestion(), faq.getAnswer());
        return ResponseEntity.ok(savedFAQ);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFAQ(@PathVariable Long id) {
        faqService.deleteFAQ(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<FAQ>> getAllFAQs(Pageable pageable) {
        Page<FAQ> faqs = faqService.getAllFAQs(pageable);
        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/faqs")
    public ResponseEntity<List<String>> getAllFAQQuestions() {
        List<String> questions = faqService.getAllFAQQuestions();
        return ResponseEntity.ok(questions);
    }
}
