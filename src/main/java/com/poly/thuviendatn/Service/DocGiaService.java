package com.poly.thuviendatn.Service;

import com.poly.thuviendatn.Model.DocGia;
import com.poly.thuviendatn.Model.LichSuNap;
import com.poly.thuviendatn.Model.Quyen;
import com.poly.thuviendatn.Model.TaiKhoan;
import com.poly.thuviendatn.Model.TaiKhoanThe;
import com.poly.thuviendatn.Repository.DocGiaRepository;
import com.poly.thuviendatn.Repository.LichSuNapRepository;
import com.poly.thuviendatn.Repository.QuyenRepository;
import com.poly.thuviendatn.Repository.TaiKhoanRepository;
import com.poly.thuviendatn.Repository.TaiKhoanTheRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

@Service
public class DocGiaService {

    @Autowired
    private LichSuNapRepository lichSuNapRepository;

    @Autowired
    private TaiKhoanTheRepository taiKhoanTheRepository;

    @Autowired
    private DocGiaRepository docGiaRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private QuyenRepository quyenRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String generateRandomPassword(int length) {
        Random random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }

        @Transactional
        public void createDocGiaAndSendPassword(DocGia docGia) {
        // ✅ 1. Tạo mật khẩu ngẫu nhiên và mã hóa
            String plainPassword = generateRandomPassword(8);
            String encodedPassword = passwordEncoder.encode(plainPassword);
            docGia.setPassword(encodedPassword);

            // ✅ 2. Lưu độc giả để lấy maDocGia
            DocGia savedDocGia = docGiaRepository.save(docGia);

            // ✅ 3. Tạo tài khoản ứng với độc giả
            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setMaTaiKhoan(savedDocGia.getMaDocGia()); // dùng mã độc giả làm mã tài khoản
            taiKhoan.setUsername(savedDocGia.getTenDocGia());
            taiKhoan.setEmail(savedDocGia.getEmail());
            taiKhoan.setPassword(encodedPassword);
            taiKhoan.setEnabled(true);

            // ✅ 4. Gán quyền mặc định
            Quyen quyenUser = quyenRepository.findByMaQuyen(3)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền USER"));
            taiKhoan.setQuyen(quyenUser);

            // ✅ 5. Lưu tài khoản và nhận lại entity đã được quản lý
            TaiKhoan savedTaiKhoan = taiKhoanRepository.save(taiKhoan);

            // ✅ 6. Tạo tài khoản thẻ liên kết với TaiKhoan đã quản lý
            TaiKhoanThe taiKhoanThe = new TaiKhoanThe();
            taiKhoanThe.setTaiKhoan(savedTaiKhoan);  // dùng entity đã lưu, tránh lỗi Hibernate session
            taiKhoanThe.setSoDu(0.0);
            taiKhoanTheRepository.save(taiKhoanThe);
                // ✅ Gửi email sau khi đã có maTaiKhoan
                sendPasswordEmail(savedDocGia.getEmail(), savedDocGia.getMaDocGia().toString(), plainPassword);
    }

    public void sendPasswordEmail(String toEmail, String maTaiKhoan, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Thông tin tài khoản thư viện");
        message.setText("Chào bạn,\n\nMã truy cập hệ thống thư viện của bạn là: " + maTaiKhoan +
                "\n\nMật khẩu truy cập hệ thống thư viện của bạn là: " + password +
                "\n\nVui lòng đổi mật khẩu sau khi đăng nhập lần đầu.\n\nTrân trọng!");

        mailSender.send(message);
    }

    public void updateDocGiaAndTaiKhoan(DocGia updatedDocGia) {
        // Lấy độc giả cũ
        DocGia existingDocGia = docGiaRepository.findById(updatedDocGia.getMaDocGia())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy độc giả với ID: " + updatedDocGia.getMaDocGia()));

        // Giữ lại mật khẩu và trạng thái
        updatedDocGia.setPassword(existingDocGia.getPassword());
        updatedDocGia.setEnabled(existingDocGia.isEnabled());

        // Lưu lại thông tin độc giả
        docGiaRepository.save(updatedDocGia);

        // Cập nhật thông tin tài khoản nếu có
        taiKhoanRepository.findById(updatedDocGia.getMaDocGia()).ifPresent(taiKhoan -> {
            taiKhoan.setUsername(updatedDocGia.getTenDocGia());
            taiKhoan.setEmail(updatedDocGia.getEmail());
            taiKhoan.setEnabled(updatedDocGia.isEnabled());
            taiKhoanRepository.save(taiKhoan);
        });
    }
    public void napTien(Integer maTaiKhoan, Double soTien) {
    // 1. Tìm tài khoản
    TaiKhoan taiKhoan = taiKhoanRepository.findById(maTaiKhoan)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

    // 2. Cập nhật số dư
    TaiKhoanThe taiKhoanThe = taiKhoanTheRepository.findById(maTaiKhoan)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy thẻ"));

    Double soDuMoi = taiKhoanThe.getSoDu() + soTien;
    taiKhoanThe.setSoDu(soDuMoi);
    taiKhoanTheRepository.save(taiKhoanThe);

    // 3. Ghi lịch sử nạp
    LichSuNap lichSu = new LichSuNap();
    lichSu.setTaiKhoan(taiKhoan);
    lichSu.setNgayNap(new Date());
    lichSu.setSoTien(soTien);
    lichSuNapRepository.save(lichSu);
}
}
