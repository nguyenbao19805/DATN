package com.poly.thuviendatn.Service;

import com.poly.thuviendatn.Model.NhanVien;
import com.poly.thuviendatn.Model.Quyen;
import com.poly.thuviendatn.Model.TaiKhoan;
import com.poly.thuviendatn.Repository.NhanVienRepository;
import com.poly.thuviendatn.Repository.QuyenRepository;
import com.poly.thuviendatn.Repository.TaiKhoanRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private QuyenRepository quyenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String generateRandomPassword(int length) {
        Random random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public void createNhanVienAndSendPassword(NhanVien nhanVien) {
        String rawPassword = generateRandomPassword(8);
        String encodedPassword = passwordEncoder.encode(rawPassword);
        nhanVien.setPassword(encodedPassword);

        // Lưu nhân viên
        NhanVien saved = nhanVienRepository.save(nhanVien);

        // Tạo tài khoản tương ứng
        TaiKhoan tk = new TaiKhoan();
        tk.setMaTaiKhoan(saved.getMaNV());
        tk.setUsername(saved.getTenNhanVien());
        tk.setEmail(saved.getEmail());
        tk.setPassword(encodedPassword);
        tk.setEnabled(saved.isEnabled());

        Quyen quyen = quyenRepository.findByMaQuyen(2) // giả sử quyền nhân viên là mã 2
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền cho nhân viên"));
        tk.setQuyen(quyen);

        taiKhoanRepository.save(tk);

        // Gửi mail chứa mật khẩu
        sendPasswordEmail(saved.getEmail(), saved.getMaNV(), rawPassword);
    }

    public void sendPasswordEmail(String to, Integer maNV, String password) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Thông tin tài khoản nhân viên");
        msg.setText("Chào bạn,\n\nMã đăng nhập của bạn là: " + maNV +
                "\nMật khẩu là: " + password +
                "\n\nVui lòng đổi mật khẩu sau khi đăng nhập.\n\nThư viện kính báo!");

        mailSender.send(msg);
    }

    public void updateNhanVienAndTaiKhoan(NhanVien updated) {
        NhanVien existing = nhanVienRepository.findById(updated.getMaNV())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));

        updated.setPassword(existing.getPassword());
        updated.setEnabled(existing.isEnabled());

        nhanVienRepository.save(updated);

        taiKhoanRepository.findById(updated.getMaNV()).ifPresent(tk -> {
            tk.setUsername(updated.getTenNhanVien());
            tk.setEmail(updated.getEmail());
            tk.setEnabled(updated.isEnabled());
            taiKhoanRepository.save(tk);
        });
    }
}
