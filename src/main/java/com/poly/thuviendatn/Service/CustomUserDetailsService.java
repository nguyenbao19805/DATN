package com.poly.thuviendatn.Service;

import com.poly.thuviendatn.Model.TaiKhoan;
import com.poly.thuviendatn.Repository.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Override
    public UserDetails loadUserByUsername(String maTaiKhoanStr) throws UsernameNotFoundException {
        Integer maTaiKhoan;
        try {
            maTaiKhoan = Integer.parseInt(maTaiKhoanStr); // Chuyển chuỗi thành số nguyên
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Mã tài khoản không hợp lệ: " + maTaiKhoanStr);
        }

        TaiKhoan taiKhoan = taiKhoanRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản với mã: " + maTaiKhoan));

        String role = switch (taiKhoan.getQuyen().getMaQuyen()) {
            case 1 -> "ROLE_ADMIN";
            case 2 -> "ROLE_STAFF";
            case 3 -> "ROLE_USER";
            default -> throw new IllegalStateException("Quyền không hợp lệ: " + taiKhoan.getQuyen().getMaQuyen());
        };

        return new User(
                String.valueOf(taiKhoan.getMaTaiKhoan()), // Dùng mã tài khoản làm "username" cho Security
                taiKhoan.getPassword(),
                taiKhoan.isEnabled(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}
