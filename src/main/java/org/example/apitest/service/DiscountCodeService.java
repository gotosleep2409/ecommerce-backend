package org.example.apitest.service;

import lombok.AllArgsConstructor;
import org.example.apitest.exception.ApiException;
import org.example.apitest.model.Category;
import org.example.apitest.model.DiscountCode;
import org.example.apitest.model.request.CategoriesRequest;
import org.example.apitest.model.request.DiscountCodeRequest;
import org.example.apitest.repository.DiscountCodeRepository;
import org.example.apitest.util.BeanUtilsAdvanced;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class DiscountCodeService {
    private DiscountCodeRepository discountCodeRepository;

    public DiscountCode createDiscountCode(DiscountCodeRequest discountCodeRequest){
        DiscountCode discountCode = new DiscountCode();
        BeanUtils.copyProperties(discountCodeRequest, discountCode);
        discountCode.setUsedNumber("0");
        return discountCodeRepository.save(discountCode);
    }

    public DiscountCode updateDiscountCode(Long id, DiscountCodeRequest discountCodeRequest) throws ApiException {
        Optional<DiscountCode> DiscountCodeExisted = discountCodeRepository.findById(id);
        if (!DiscountCodeExisted.isPresent()) {
            throw new ApiException("Not found with id=" + id);
        }
        DiscountCode discountCode = DiscountCodeExisted.get();
        BeanUtilsAdvanced.copyProperties(discountCodeRequest, discountCode);
        return discountCodeRepository.save(discountCode);
    }

    public Page<DiscountCode> getPageDiscountCode(int page, int size) {
        PageRequest paging = PageRequest.of(page -1, size, Sort.by(Sort.Direction.DESC, "id"));
        return discountCodeRepository.findAll(paging);
    }

    public void deleteDiscountCode(Long id) throws ApiException {
        Optional<DiscountCode> discountCodeExisted = discountCodeRepository.findById(id);
        if (!discountCodeExisted.isPresent()) {
            throw new ApiException("Not found with id=" + id);
        }
        discountCodeRepository.delete(discountCodeExisted.get());
    }

    public Optional<DiscountCode> checkDiscountCode(String code) {
        return discountCodeRepository.findByCode(code);
    }
}
