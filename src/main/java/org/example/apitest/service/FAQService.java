package org.example.apitest.service;

import org.example.apitest.model.FAQ;
import org.example.apitest.repository.FAQRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FAQService {
    @Autowired
    private FAQRepository faqRepository;
    public String getAnswer(String question) {
        FAQ faq = faqRepository.findByQuestion(question);
        return faq != null ? faq.getAnswer() : null;
    }

    public FAQ addOrUpdateFAQ(String question, String answer) {
        FAQ faq = faqRepository.findByQuestion(question);
        if (faq == null) {
            faq = new FAQ();
            faq.setQuestion(question);
        }
        faq.setAnswer(answer);
        return faqRepository.save(faq);
    }

    public void deleteFAQ(Long id) {
        faqRepository.deleteById(id);
    }

    public Page<FAQ> getAllFAQs(Pageable pageable) {
        return faqRepository.findAll(pageable);
    }

    public List<String> getAllFAQQuestions() {
        return faqRepository.findAll().stream()
                .map(FAQ::getQuestion)
                .collect(Collectors.toList());
    }
}
