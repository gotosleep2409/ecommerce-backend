package org.example.apitest.service;

import lombok.AllArgsConstructor;
import org.example.apitest.config.Config;
import org.example.apitest.exception.ApiException;
import org.example.apitest.model.*;
import org.example.apitest.model.dto.BillDetailDTO;
import org.example.apitest.model.dto.BillDetailProductDTO;
import org.example.apitest.model.dto.SizeQuantityDTO;
import org.example.apitest.model.dto.paymentDTO;
import org.example.apitest.model.request.BillsRequest;
import org.example.apitest.model.response.BillDetailResponse;
import org.example.apitest.model.response.BillResponse;
import org.example.apitest.repository.*;
import org.example.apitest.util.BeanUtilsAdvanced;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@AllArgsConstructor
public class BillsService {
    private BillsRepository billsRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private SizeRepository sizeRepository;
    private BillDetailsRepository detailsRepository;
    private ProductSizeRepository productSizeRepository;

    public Page<Bills> getPageBills(int page, int size) {
        PageRequest paging = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        return billsRepository.findAll(paging);
    }

    public Bills createdBill(BillResponse billResponse) {
        Bills bill = new Bills();
        bill.setName(billResponse.getName());
        if (billResponse.getUserId() != null) {
            User user = userRepository.findById(billResponse.getUserId()).orElse(null);
            if (user != null) {
                bill.setUser(user);
            }
        }
        bill.setEmail(billResponse.getEmail());
        bill.setShoppingAddress(billResponse.getAddress());
        bill.setPhoneNumber(billResponse.getPhone());
        bill.setPaymentMethod(billResponse.getPaymentMethod());
        if ("Bank transfer".equals(billResponse.getPaymentMethod())) {
            bill.setPaymentStatus("Chờ xác thực");
        } else if ("COD".equals(billResponse.getPaymentMethod())) {
            bill.setPaymentStatus("Chờ thanh toán");
        } else if ("E-payment".equals(billResponse.getPaymentMethod())) {
            bill.setPaymentStatus("Đã thanh toán");
        }
        bill.setStatus("Đang chuẩn bị hàng");
        bill.setTotalAmount(billResponse.getTotalPrice());
        bill.setNote(billResponse.getNotes());
        int randomNumber = new Random().nextInt(9000) + 1000;
        bill.setCode("DH"+ randomNumber);
        billsRepository.save(bill);

        List<BillDetails> billDetailsList = new ArrayList<>();
        for (BillDetailResponse billDetailResponse : billResponse.getBillDetails()) {
            if (billDetailResponse.getSizes() != null && !billDetailResponse.getSizes().isEmpty()) {
                for (SizeQuantityDTO sizeQuantityResponse : billDetailResponse.getSizes()) {
                    BillDetails billDetail = new BillDetails();
                    billDetail.setBill(bill);
                    if (billDetailResponse.getProductId() != null) {
                        Product product = productRepository.findById(billDetailResponse.getProductId()).orElse(null);
                        if (product != null) {
                            billDetail.setProduct(product);
                        }
                        else break;
                    }
                    Size size = sizeRepository.getSizeByName(sizeQuantityResponse.getSize());
                    if (size != null) {
                        billDetail.setSize(size);
                        billDetail.setUnitPrice(billDetailResponse.getPrice());
                        billDetail.setQuantity(sizeQuantityResponse.getQuantity());
                        billDetailsList.add(billDetail);
                    }
                    ProductSize productSize = productSizeRepository.findByProductIdAndSizeName(billDetailResponse.getProductId(), sizeQuantityResponse.getSize());
                    if (productSize != null) {
                        int updatedQuantity = (int) (productSize.getQuantity() - sizeQuantityResponse.getQuantity());
                        if (updatedQuantity >= 0) {
                            productSize.setQuantity(updatedQuantity);
                            productSizeRepository.save(productSize);
                        }
                        else {
                            break;
                        }
                    }
                }
            }
        }
        detailsRepository.saveAll(billDetailsList);
        return bill;
    }

    public Bills updateBills(Long id, BillsRequest billToUpdate) throws ApiException {
        Optional<Bills> billsExisted = billsRepository.findById(id);
        if (!billsExisted.isPresent()) {
            throw new ApiException("Not found with id=" + id);
        }
        Bills bill = billsExisted.get();
        BeanUtilsAdvanced.copyProperties(billToUpdate, bill);

        return billsRepository.save(bill);
    }

    @Transactional
    public void deleteBill (Long id) throws ApiException {
        Optional<Bills> billExisted = billsRepository.findById(id);
        if (!billExisted.isPresent()) {
            throw new ApiException("Not found with id=" + id);
        }
        detailsRepository.deleteByBillId(id);
        billsRepository.delete(billExisted.get());
    }

    public paymentDTO vnPay(BillResponse billResponse) throws UnsupportedEncodingException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        String total= billResponse.getTotalPrice();
        long amount = Long.parseLong(total) * 100;
        String bankCode = "NCB";
        String vnp_TxnRef = Config.getRandomNumber(8);;
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = Config.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        /*vnp_Params.put("vnp_BankCode", bankCode);*/
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
        paymentDTO paymentDTO = new paymentDTO();
        paymentDTO.setPaymentUrl(paymentUrl);
        paymentDTO.setStatus("OK");
        paymentDTO.setMessage("Successfully");
        return paymentDTO;
    }

    public BillDetailDTO getBillDetails(Long billId) {
        List<BillDetails> results = detailsRepository.findBillDetailsByBillId(billId);
        if (results.isEmpty()) {
            return null;
        }

        BillDetailDTO billDetailDTO = new BillDetailDTO();
        billDetailDTO.setProducts(new ArrayList<>());
        for (BillDetails result: results){
            billDetailDTO.setId(result.getBill().getId());
            billDetailDTO.setCode(result.getBill().getCode());
            billDetailDTO.setNote(result.getBill().getNote());
            billDetailDTO.setStatus(result.getBill().getStatus());
            billDetailDTO.setDate(result.getBill().getDate());
            billDetailDTO.setTotalAmount(result.getBill().getTotalAmount());
            billDetailDTO.setPaymentMethod(result.getBill().getPaymentMethod());
            billDetailDTO.setPaymentStatus(result.getBill().getPaymentStatus());
            billDetailDTO.setShoppingAddress(result.getBill().getShoppingAddress());
            billDetailDTO.setPhoneNumber(result.getBill().getPhoneNumber());
            billDetailDTO.setEmail(result.getBill().getEmail());
            billDetailDTO.setName(result.getBill().getName());
            billDetailDTO.setUser(result.getBill().getUser());

            boolean productExists = false;

            for (BillDetailProductDTO productDTO : billDetailDTO.getProducts()) {
                if (productDTO.getProductName().equals(result.getProduct().getName())) {
                    productExists = true;
                    productDTO.getSizeQuantity().add(new SizeQuantityDTO(result.getSize().getName(), result.getQuantity()));
                    break;
                }
            }

            if (!productExists) {
                BillDetailProductDTO newProductDTO = new BillDetailProductDTO();
                newProductDTO.setProductName(result.getProduct().getName());

                List<SizeQuantityDTO> newSizeQuantityDTOList = new ArrayList<>();
                newSizeQuantityDTOList.add(new SizeQuantityDTO(result.getSize().getName(), result.getQuantity()));
                newProductDTO.setSizeQuantity(newSizeQuantityDTOList);

                billDetailDTO.getProducts().add(newProductDTO);
            }

        }
        return billDetailDTO;
    }

    public Page<Bills> getPageBillsByID(int page, int size, long id) {
        PageRequest paging = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        return billsRepository.findByUserId(paging, id);
    }
}