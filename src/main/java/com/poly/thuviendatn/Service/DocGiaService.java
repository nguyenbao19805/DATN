package com.poly.thuviendatn.Service;

import com.poly.thuviendatn.Model.DocGia;
import com.poly.thuviendatn.Model.Quyen;
import com.poly.thuviendatn.Model.TaiKhoan;
import com.poly.thuviendatn.Repository.DocGiaRepository;
import com.poly.thuviendatn.Repository.QuyenRepository;
import com.poly.thuviendatn.Repository.TaiKhoanRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class DocGiaService {

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

    public void createDocGiaAndSendPassword(DocGia docGia) {
        String plainPassword = generateRandomPassword(8);

        // Mã hóa mật khẩu rồi lưu
        String encodedPassword = passwordEncoder.encode(plainPassword);
        docGia.setPassword(encodedPassword);

        // ✅ Lưu độc giả trước để lấy maDocGia
        DocGia savedDocGia = docGiaRepository.save(docGia);

        // ✅ Tạo tài khoản ứng với độc giả
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setMaTaiKhoan(savedDocGia.getMaDocGia()); // <-- Dùng mã độc giả
        taiKhoan.setUsername(savedDocGia.getTenDocGia());
        taiKhoan.setEmail(savedDocGia.getEmail());
        taiKhoan.setPassword(encodedPassword);
        taiKhoan.setEnabled(true);

        // Gán quyền mặc định (giả sử mã quyền 3 là USER)
        Quyen quyenUser = quyenRepository.findByMaQuyen(3)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền USER"));
        taiKhoan.setQuyen(quyenUser);

        taiKhoanRepository.save(taiKhoan);

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
}
