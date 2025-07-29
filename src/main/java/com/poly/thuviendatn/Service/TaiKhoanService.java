// package com.poly.thuviendatn.Service;

// import com.poly.thuviendatn.Model.TaiKhoan;
// import com.poly.thuviendatn.Repository.TaiKhoanRepository;
// import com.poly.thuviendatn.Dto.RegisterFormDTO;
// import com.poly.thuviendatn.Model.Quyen;
// import com.poly.thuviendatn.Repository.QuyenRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// @Service
// public class TaiKhoanService {

//     @Autowired
//     private TaiKhoanRepository taiKhoanRepository;

//     @Autowired
//     private QuyenRepository quyenRepository;

//     @Autowired
//     private PasswordEncoder passwordEncoder;

//     public void registerAccount(RegisterFormDTO form) {
//         // Check if username or email already exists
//         if (taiKhoanRepository.findByUsername(form.getUsername()).isPresent()) {
//             throw new RuntimeException("Tên đăng nhập đã tồn tại");
//         }
//         if (taiKhoanRepository.findByEmail(form.getEmail()).isPresent()) {
//             throw new RuntimeException("Email đã được sử dụng");
//         }
//         // Check password confirmation
//         if (!form.getPassword().equals(form.getConfirmPassword())) {
//             throw new RuntimeException("Mật khẩu và xác nhận mật khẩu không khớp");
//         }

//         // Create new account
//         TaiKhoan taiKhoan = new TaiKhoan();
//         taiKhoan.setUsername(form.getUsername());
//         taiKhoan.setEmail(form.getEmail());
//         taiKhoan.setPassword(passwordEncoder.encode(form.getPassword()));
//         taiKhoan.setEnabled(true); // Set account as enabled

//         // Assign USER role
//         Quyen userRole = quyenRepository.findByMaQuyen(3)
//                 .orElseThrow(() -> new RuntimeException("Vai trò USER không tồn tại"));
//         taiKhoan.setQuyen(userRole);

//         taiKhoanRepository.save(taiKhoan);
//     }

//     public TaiKhoan findByUsername(String username) {
//         return taiKhoanRepository.findByUsername(username)
//                 .map(taiKhoan -> {
//                     taiKhoan.getQuyen(); // Ensure Quyen is loaded
//                     return taiKhoan;
//                 })
//                 .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với username: " + username));
//     }

//     public void updateProfile(String username, String email) {
//         TaiKhoan taiKhoan = findByUsername(username);
//         if (!taiKhoan.getEmail().equals(email) && taiKhoanRepository.findByEmail(email).isPresent()) {
//             throw new RuntimeException("Email đã được sử dụng");
//         }
//         taiKhoan.setEmail(email);
//         taiKhoanRepository.save(taiKhoan);
//     }

//     public Object createAccountForDocGia(String email, String tenDocGia, String plainPassword) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'createAccountForDocGia'");
//     }
    
// }