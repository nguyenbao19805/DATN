package com.poly.thuviendatn.Controller;

import com.poly.thuviendatn.Model.PhieuMuon;
import com.poly.thuviendatn.Repository.PhieuMuonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class PaymentController {

    private final PhieuMuonRepository phieuMuonRepository;

    @Autowired
    public PaymentController(PhieuMuonRepository phieuMuonRepository) {
        this.phieuMuonRepository = phieuMuonRepository;
    }

    @GetMapping("/thanhtoan")
    public String showPaymentPage(Model model) {
        // Lấy tất cả phiếu mượn từ cơ sở dữ liệu
        List<PhieuMuon> phieuMuons = phieuMuonRepository.findAll();

        // Tính trạng thái và tiền phạt dựa trên ngày hiện tại
        LocalDate today = LocalDate.now();
        for (PhieuMuon phieuMuon : phieuMuons) {
            if (phieuMuon.getNgayHetHan().isBefore(today) && phieuMuon.getNgayTra() == null) {
                phieuMuon.setTrangThai("quahan");
                long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(phieuMuon.getNgayHetHan(), today);
                phieuMuon.setTienPhat(BigDecimal.valueOf(daysOverdue * 10000)); // 10,000 VNĐ mỗi ngày
            } else if (phieuMuon.getNgayTra() == null) {
                phieuMuon.setTrangThai("dangmuon");
            } else {
                phieuMuon.setTrangThai("datra");
            }
        }

        // Mock URL thanh toán ngân hàng
        String bankPaymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=50000&vnp_ReturnUrl=http://localhost:8080/thanhtoan/ketqua";

        // Thêm dữ liệu vào model
        model.addAttribute("phieuMuons", phieuMuons);
        model.addAttribute("bankPaymentUrl", bankPaymentUrl);

        return "public/thanhtoan"; // Maps to thanhtoan.html
    }

    @GetMapping("/thanhtoan/thanhcong")
    public String paymentSuccess() {
        return "public/thanhtoan-thanhcong";
    }

    @GetMapping("/thanhtoan/thatbai")
    public String paymentFail() {
        return "public/thanhtoan-thatbai";
    }
}