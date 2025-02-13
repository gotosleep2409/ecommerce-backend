package org.example.apitest.service;

import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.apitest.config.Config;
import org.example.apitest.exception.ApiException;
import org.example.apitest.model.*;
import org.example.apitest.model.dto.BillDetailDTO;
import org.example.apitest.model.dto.BillDetailProductDTO;
import org.example.apitest.model.dto.SizeQuantityDTO;
import org.example.apitest.model.dto.paymentDTO;
import org.example.apitest.model.request.BillReportRequest;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BillsService {
    private BillsRepository billsRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private SizeRepository sizeRepository;
    private BillDetailsRepository detailsRepository;
    private ProductSizeRepository productSizeRepository;
    private CommentsRepository commentsRepository;
    private DiscountCodeRepository discountCodeRepository;

    public Page<Bills> getPageBills(int page, int size, String paymentMethod, String paymentStatus, String status, String code) {
        PageRequest paging = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        paymentMethod = "%" + paymentMethod + "%";
        paymentStatus = "%" + paymentStatus + "%";
        status = "%" + status + "%";
        code = "%" + code + "%";
        return billsRepository.getPageFilter(paymentMethod,paymentStatus,status,code,paging);
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
        bill.setDiscountCode(billResponse.getCodeDiscount());
        bill.setDiscountedAmount(billResponse.getDiscountedAmount());
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
        if (!billResponse.getCodeDiscount().isEmpty()) {
            Optional<DiscountCode> discountCodeOpt = discountCodeRepository.findByCode(billResponse.getCodeDiscount());

            if (discountCodeOpt.isPresent()) {
                DiscountCode discountCode = discountCodeOpt.get();

                int currentUsed = discountCode.getUsedNumber() != null ? Integer.parseInt(discountCode.getUsedNumber()) : 0;
                discountCode.setUsedNumber(String.valueOf(currentUsed + 1));

                discountCodeRepository.save(discountCode);
            }
        }
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
                newProductDTO.setProductId(result.getProduct().getId());

                List<SizeQuantityDTO> newSizeQuantityDTOList = new ArrayList<>();
                newSizeQuantityDTOList.add(new SizeQuantityDTO(result.getSize().getName(), result.getQuantity()));
                newProductDTO.setSizeQuantity(newSizeQuantityDTOList);

                /*Comments reviews = commentsRepository.findByProductIdAndBillIdAndUserId(result.getProduct().getId(), billId, result.getBill().getUser().getId());
                newProductDTO.setReviewed(reviews != null);*/

                if (result.getBill().getUser() != null && result.getBill().getUser().getId() != null) {
                    Comments reviews = commentsRepository.findByProductIdAndBillIdAndUserId(
                            result.getProduct().getId(),
                            billId,
                            result.getBill().getUser().getId()
                    );
                    newProductDTO.setReviewed(reviews != null);
                } else {
                    newProductDTO.setReviewed(false);
                }

                billDetailDTO.getProducts().add(newProductDTO);
            }

        }
        return billDetailDTO;
    }

    public Page<Bills> getPageBillsByID(int page, int size, long id) {
        PageRequest paging = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        return billsRepository.findByUserId(paging, id);
    }

    public Map<String, Object> getLast12MonthsStatistics() {
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        calendar.add(Calendar.MONTH, -11);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();

        List<Bills> bills = billsRepository.findAllByDateRange(startDate, endDate);

        Map<String, Integer> totalOrders = new HashMap<>();
        Map<String, Integer> successfulOrders = new HashMap<>();
        Map<String, Integer> canceledOrders = new HashMap<>();

        List<String> months = new ArrayList<>();
        calendar.setTime(startDate);
        for (int i = 0; i < 12; i++) {
            String monthName = String.format("%1$tB %1$tY", calendar.getTime());
            months.add(monthName);
            totalOrders.put(monthName, 0);
            successfulOrders.put(monthName, 0);
            canceledOrders.put(monthName, 0);
            calendar.add(Calendar.MONTH, 1);
        }

        for (Bills bill : bills) {
            Calendar billCalendar = Calendar.getInstance();
            billCalendar.setTime(bill.getDate());
            String monthName = String.format("%1$tB %1$tY", billCalendar.getTime());

            if (totalOrders.containsKey(monthName)) {
                totalOrders.put(monthName, totalOrders.get(monthName) + 1);
                if ("Đã giao hàng".equalsIgnoreCase(bill.getStatus())) {
                    successfulOrders.put(monthName, successfulOrders.get(monthName) + 1);
                }
                if ("Đơn hàng hủy".equalsIgnoreCase(bill.getStatus())) {
                    canceledOrders.put(monthName, canceledOrders.get(monthName) + 1);
                }
            }
        }

        List<Integer> totalOrdersList = months.stream().map(totalOrders::get).collect(Collectors.toList());
        List<Integer> successfulOrdersList = months.stream().map(successfulOrders::get).collect(Collectors.toList());
        List<Integer> canceledOrdersList = months.stream().map(canceledOrders::get).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("period", "Month");
        result.put("labels", months);
        result.put("datasets", Arrays.asList(
                new BillReportRequest("Tổng số đơn hàng", totalOrdersList),
                new BillReportRequest("Đơn hàng thành công", successfulOrdersList),
                new BillReportRequest("Đơn hàng hủy", canceledOrdersList)
        ));

        return result;
    }

    public Map<String, Object> getLastWeekStatistics() {
        Date endDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        Date startDate = calendar.getTime();

        List<Bills> bills = billsRepository.findAllByDateRange(startDate, endDate);

        Map<String, Integer> totalOrders = new HashMap<>();
        Map<String, Integer> successfulOrders = new HashMap<>();
        Map<String, Integer> canceledOrders = new HashMap<>();

        List<String> days = getLast7Days();
        for (String day : days) {
            totalOrders.put(day, 0);
            successfulOrders.put(day, 0);
            canceledOrders.put(day, 0);
        }

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM");
        SimpleDateFormat weekDayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);

        for (Bills bill : bills) {
            String billDay = dayFormat.format(bill.getDate());
            String dayOfWeek = weekDayFormat.format(bill.getDate());

            if (totalOrders.containsKey(billDay)) {
                totalOrders.put(billDay, totalOrders.get(billDay) + 1);
                if ("Đã giao hàng".equalsIgnoreCase(bill.getStatus())) {
                    successfulOrders.put(billDay, successfulOrders.get(billDay) + 1);
                } else if ("Đơn hàng hủy".equalsIgnoreCase(bill.getStatus())) {
                    canceledOrders.put(billDay, canceledOrders.get(billDay) + 1);
                }
            }
        }

        List<Integer> totalOrdersList = days.stream().map(totalOrders::get).collect(Collectors.toList());
        List<Integer> successfulOrdersList = days.stream().map(successfulOrders::get).collect(Collectors.toList());
        List<Integer> canceledOrdersList = days.stream().map(canceledOrders::get).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("period", "Day");
        result.put("labels", days);
        result.put("datasets", Arrays.asList(
                new BillReportRequest("Tổng số đơn hàng", totalOrdersList),
                new BillReportRequest("Đơn hàng thành công", successfulOrdersList),
                new BillReportRequest("Đơn hàng hủy", canceledOrdersList)
        ));

        return result;
    }

    public Map<String, Object> getLastWeekTurnover() {
        Date endDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        Date startDate = calendar.getTime();

        List<Bills> bills = billsRepository.findAllByDateRange(startDate, endDate);

        Map<String, Double> totalOrderValues = new HashMap<>();
        Map<String, Integer> successfulOrders = new HashMap<>();
        Map<String, Integer> canceledOrders = new HashMap<>();

        List<String> days = getLast7Days();
        for (String day : days) {
            totalOrderValues.put(day, 0.0);
            successfulOrders.put(day, 0);
            canceledOrders.put(day, 0);
        }

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM");
        SimpleDateFormat weekDayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);

        for (Bills bill : bills) {
            String billDay = dayFormat.format(bill.getDate());

            if (totalOrderValues.containsKey(billDay)) {
                double totalAmount = Double.parseDouble(bill.getTotalAmount());
                totalOrderValues.put(billDay, totalOrderValues.get(billDay) + totalAmount);

                if ("Đã giao hàng".equalsIgnoreCase(bill.getStatus())) {
                    successfulOrders.put(billDay, successfulOrders.get(billDay) + 1);
                } else if ("Đơn hàng hủy".equalsIgnoreCase(bill.getStatus())) {
                    canceledOrders.put(billDay, canceledOrders.get(billDay) + 1);
                }
            }
        }

        List<Integer> totalOrderValuesList = days.stream()
                .map(day -> (int)Math.round(totalOrderValues.get(day)))
                .collect(Collectors.toList());

        List<Integer> successfulOrdersList = days.stream().map(successfulOrders::get).collect(Collectors.toList());
        List<Integer> canceledOrdersList = days.stream().map(canceledOrders::get).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("period", "Day");
        result.put("labels", days);
        result.put("datasets", Arrays.asList(
                new BillReportRequest("Tổng giá trị đơn hàng", totalOrderValuesList),
                new BillReportRequest("Đơn hàng thành công", successfulOrdersList),
                new BillReportRequest("Đơn hàng hủy", canceledOrdersList)
        ));

        return result;
    }

    public Map<String, Object> getLast12MonthsTurnover() {
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        calendar.add(Calendar.MONTH, -11);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();

        List<Bills> bills = billsRepository.findAllByDateRange(startDate, endDate);

        Map<String, Double> totalTurnover = new HashMap<>();
        Map<String, Integer> successfulOrders = new HashMap<>();
        Map<String, Integer> canceledOrders = new HashMap<>();

        List<String> months = new ArrayList<>();
        calendar.setTime(startDate);
        for (int i = 0; i < 12; i++) {
            String monthName = String.format("%1$tB %1$tY", calendar.getTime());
            months.add(monthName);
            totalTurnover.put(monthName, 0.0);
            successfulOrders.put(monthName, 0);
            canceledOrders.put(monthName, 0);
            calendar.add(Calendar.MONTH, 1);
        }

        for (Bills bill : bills) {
            Calendar billCalendar = Calendar.getInstance();
            billCalendar.setTime(bill.getDate());
            String monthName = String.format("%1$tB %1$tY", billCalendar.getTime());

            if (totalTurnover.containsKey(monthName)) {
                if (bill.getTotalAmount() != null && !bill.getTotalAmount().trim().isEmpty()) {
                    double billAmount = Double.parseDouble(bill.getTotalAmount().trim());
                    totalTurnover.put(monthName, totalTurnover.get(monthName) + billAmount);
                }

                if ("Đã giao hàng".equalsIgnoreCase(bill.getStatus())) {
                    successfulOrders.put(monthName, successfulOrders.get(monthName) + 1);
                }
                if ("Đơn hàng hủy".equalsIgnoreCase(bill.getStatus())) {
                    canceledOrders.put(monthName, canceledOrders.get(monthName) + 1);
                }
            }
        }

        List<Double> totalTurnoverList = months.stream().map(totalTurnover::get).collect(Collectors.toList());
        List<Integer> totalTurnoverListInt = totalTurnoverList.stream().map(Double::intValue).collect(Collectors.toList());
        List<Integer> successfulOrdersList = months.stream().map(successfulOrders::get).collect(Collectors.toList());
        List<Integer> canceledOrdersList = months.stream().map(canceledOrders::get).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("period", "Month");
        result.put("labels", months);
        result.put("datasets", Arrays.asList(
                new BillReportRequest("Tổng doanh thu", totalTurnoverListInt),
                new BillReportRequest("Đơn hàng thành công", successfulOrdersList),
                new BillReportRequest("Đơn hàng hủy", canceledOrdersList)
        ));

        return result;
    }

    //Top 10 Product
    public Map<String, List<?>> getTop10BestSellingProducts() {
        List<Object[]> results = detailsRepository.findTop10BestSellingProducts();
        List<String> productNames = results.stream()
                .map(result -> (String) result[0])
                .limit(10)
                .collect(Collectors.toList());

        List<Long> quantities = results.stream()
                .map(result -> (Long) result[1])
                .limit(10)
                .collect(Collectors.toList());

        Map<String, List<?>> response = new HashMap<>();
        response.put("productNames", productNames);
        response.put("quantities", quantities);

        return response;
    }

    private List<String> getLast7Days() {
        List<String> days = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            days.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }

        Collections.reverse(days);
        return days;
    }

    public byte[] generateExcelFile(Long billId) {
        Bills bill = billsRepository.findById(billId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy hóa đơn với ID: " + billId));

        List<BillDetails> billDetailsList = detailsRepository.findBillDetailsByBillId(billId);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Hoá đơn " + bill.getCode());

            // Thiết lập font và style cho tiêu đề
            Font titleFont = workbook.createFont();
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setBold(true);

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);


            Row shopTitleRow = sheet.createRow(0);
            Cell shopTitleCell = shopTitleRow.createCell(0);
            shopTitleCell.setCellValue("Shop Nghiện Bóng Đá");
            shopTitleCell.setCellStyle(titleStyle);

            Row orderTitleRow = sheet.createRow(1);
            Cell orderTitleCell = orderTitleRow.createCell(0);
            orderTitleCell.setCellValue("Đơn hàng " + bill.getCode());
            orderTitleCell.setCellStyle(titleStyle);

            // Merge các cột để tiêu đề căn giữa
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

            // Tạo hàng trống sau tiêu đề
            sheet.createRow(2);

            Row recipientRow = sheet.createRow(3);
            recipientRow.createCell(0).setCellValue("Tên người nhận:");
            recipientRow.createCell(1).setCellValue(bill.getName());

            Row phoneRow = sheet.createRow(4);
            phoneRow.createCell(0).setCellValue("Số điện thoại:");
            phoneRow.createCell(1).setCellValue(bill.getPhoneNumber());

            Row addressRow = sheet.createRow(5);
            addressRow.createCell(0).setCellValue("Địa chỉ:");
            addressRow.createCell(1).setCellValue(bill.getShoppingAddress());

            Row paymentMethodRow = sheet.createRow(6);
            paymentMethodRow.createCell(0).setCellValue("Phương thức thanh toán:");
            paymentMethodRow.createCell(1).setCellValue(bill.getPaymentMethod());

            Row printTimeRow = sheet.createRow(7);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            printTimeRow.createCell(0).setCellValue("Thời gian: ");
            printTimeRow.createCell(1).setCellValue(dateFormat.format(new Date()));

            Row headerRow = sheet.createRow(8);
            headerRow.createCell(0).setCellValue("Tên Sản Phẩm");
            headerRow.createCell(1).setCellValue("Số Lượng");
            headerRow.createCell(2).setCellValue("Giá Đơn Vị");
            headerRow.createCell(3).setCellValue("Thành Tiền");

            sheet.setColumnWidth(0, 30 * 256);
            sheet.setColumnWidth(1, 15 * 256);
            sheet.setColumnWidth(2, 15 * 256);
            sheet.setColumnWidth(3, 20 * 256);

            int rowIndex = 9;
            for (BillDetails detail : billDetailsList) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(detail.getProduct().getName());
                row.createCell(1).setCellValue(detail.getQuantity());
                row.createCell(2).setCellValue(detail.getUnitPrice());

                double thanhTien = detail.getQuantity() * Double.parseDouble(detail.getUnitPrice());
                row.createCell(3).setCellValue(thanhTien);
            }

            Row totalRow = sheet.createRow(rowIndex++);
            totalRow.createCell(2).setCellValue("Tổng cộng:");
            totalRow.createCell(3).setCellValue(Double.parseDouble(bill.getTotalAmount()));

            Row discountRow = sheet.createRow(rowIndex++);
            discountRow.createCell(2).setCellValue("Số tiền giảm giá:");
            discountRow.createCell(3).setCellValue(Double.parseDouble(bill.getDiscountedAmount()));

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error while exporting data to Excel", e);
        }
    }
}