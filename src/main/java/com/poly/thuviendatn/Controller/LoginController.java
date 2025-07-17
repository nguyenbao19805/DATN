package com.poly.thuviendatn.Controller;

import com.poly.thuviendatn.Dto.RegisterFormDTO;
import com.poly.thuviendatn.Model.TaiKhoan;
import com.poly.thuviendatn.Model.Quyen;
import com.poly.thuviendatn.Repository.TaiKhoanRepository;
import com.poly.thuviendatn.Repository.QuyenRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private QuyenRepository quyenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginPage() {
        return "Public/login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerForm", new RegisterFormDTO());
        return "Public/register";
    }

    @PostMapping("/register")
public String register(@Valid RegisterFormDTO registerForm, BindingResult result, Model model) {
    if (result.hasErrors()) {
        return "Public/register";
    }

    if (!registerForm.getPassword().equals(registerForm.getConfirmPassword())) {
        model.addAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
        return "Public/register";
    }

    // Kiểm tra username/email trùng
    if (taiKhoanRepository.findByUsername(registerForm.getUsername()).isPresent()) {
        model.addAttribute("errorMessage", "Tên đăng nhập đã tồn tại!");
        return "Public/register";
    }

    if (taiKhoanRepository.findByEmail(registerForm.getEmail()).isPresent()) {
        model.addAttribute("errorMessage", "Email đã được sử dụng!");
        return "Public/register";
    }

    // ✅ Kiểm tra trùng mã tài khoản (nếu nhập tay)
    if (taiKhoanRepository.findById(registerForm.getMaTaiKhoan()).isPresent()) {
        model.addAttribute("errorMessage", "Mã tài khoản đã tồn tại!");
        return "Public/register";
    }

    // Tạo tài khoản
    TaiKhoan taiKhoan = new TaiKhoan();
    taiKhoan.setMaTaiKhoan(registerForm.getMaTaiKhoan()); // ✅ Gán ID từ form
    taiKhoan.setUsername(registerForm.getUsername());
    taiKhoan.setEmail(registerForm.getEmail());
    taiKhoan.setPassword(passwordEncoder.encode(registerForm.getPassword()));
    taiKhoan.setEnabled(true);

    Quyen userRole = quyenRepository.findByMaQuyen(1)
            .orElseThrow(() -> new IllegalStateException("Không tìm thấy quyền người dùng"));
    taiKhoan.setQuyen(userRole);

    taiKhoanRepository.save(taiKhoan);

    model.addAttribute("registerForm", new RegisterFormDTO()); // Reset form
    model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
    return "Public/register";
}

    @GetMapping("/password/success")
    public String passwordChangeSuccess() {
        return "Public/password-change-success";
    }

    @GetMapping("/password/fail")
    public String passwordChangeFail() {
        return "Public/password-change-failed";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(Model model) {
        return "public/reset-password"; // Maps to reset-password.html
    }

            @GetMapping("/quenmatkhau")
    public String showQuenMatKhau(Model model) {
        return "public/password-change";
    }
}
