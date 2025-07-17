package com.poly.thuviendatn.AdminController;

import com.poly.thuviendatn.Model.DocGia;
import com.poly.thuviendatn.Model.NhanVien;
import com.poly.thuviendatn.Repository.NhanVienRepository;
import com.poly.thuviendatn.Repository.TaiKhoanRepository;
import com.poly.thuviendatn.Service.NhanVienService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/nhanvien")
public class NhanVienController {

    private final NhanVienService nhanVienService;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private NhanVienRepository nhanVienRepository;
    @Autowired
    public NhanVienController(NhanVienService nhanVienService, NhanVienRepository nhanVienRepository) {
        this.nhanVienService = nhanVienService;
        this.nhanVienRepository = nhanVienRepository;
    }

    // Hiển thị danh sách nhân viên
    @GetMapping
    public String hienThiDanhSachNhanVien(
            @RequestParam(defaultValue = "0") int nhanVienPage,
            @RequestParam(defaultValue = "5") int nhanVienSize,
            @RequestParam(required = false) String nhanVienKeyword,
            Model model) {

        Pageable pageable = PageRequest.of(nhanVienPage, nhanVienSize);
        Page<NhanVien> page;

        if (nhanVienKeyword != null && !nhanVienKeyword.trim().isEmpty()) {
            List<NhanVien> filteredList = nhanVienRepository.findAll().stream()
                    .filter(nv -> nv.getTenNhanVien().toLowerCase().contains(nhanVienKeyword.toLowerCase()))
                    .toList();

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), filteredList.size());
            page = new PageImpl<>(filteredList.subList(start, end), pageable, filteredList.size());
        } else {
            page = nhanVienRepository.findAll(pageable);
        }

        model.addAttribute("nhanVienPage", page);
        model.addAttribute("nhanVienKeyword", nhanVienKeyword);
        model.addAttribute("activeSection", "nhanvien");
        return "Admin/Nhanvien/nhanvien";
    }

    // Hiển thị form thêm nhân viên
    @GetMapping("/them")
    public String showAddNhanVienForm(Model model) {
        model.addAttribute("nhanVien", new NhanVien());
        model.addAttribute("activeSection", "nhanvien");
        return "Admin/Nhanvien/themnhanvien";
    }

    // Xử lý thêm nhân viên
    @PostMapping("/them")
    public String addNhanVien(
            @Valid @ModelAttribute("nhanVien") NhanVien nhanVien,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
        model.addAttribute("activeSection", "nhanvien");
        return "Admin/Nhanvien/themnhanvien";
    }

    // Kiểm tra email đã tồn tại
    if (nhanVienRepository.existsByEmail(nhanVien.getEmail())) {
        result.rejectValue("email", "error.nhanVien", "Email đã tồn tại trong hệ thống.");
        model.addAttribute("activeSection", "nhanVien");
        return "Admin/Nhanvien/themnhanvien";
    }

    // Kiểm tra CCCD đã tồn tại
    if (nhanVienRepository.existsByCccd(nhanVien.getCccd())) {
        result.rejectValue("cccd", "error.nhanVien", "CCCD đã tồn tại trong hệ thống.");
        model.addAttribute("activeSection", "nhanvien");
        return "Admin/Nhanvien/themnhanvien";
    }

        nhanVienService.createNhanVienAndSendPassword(nhanVien); // Tự set mật khẩu, ngày bắt đầu, lương...

        redirectAttributes.addFlashAttribute("successMessage", "Thêm độc giả thành công! Mật khẩu đã được gửi qua email.");
    return "redirect:/admin/nhanvien";
    }

    @GetMapping("/sua-nhanvien")
        public String showEditForm(@RequestParam("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<NhanVien> optionalNhanVien = nhanVienRepository.findById(id);
        if (optionalNhanVien.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhân viên để sửa.");
            return "redirect:/admin/nhanvien";
        }

        model.addAttribute("nhanVien", optionalNhanVien.get());
        model.addAttribute("activeSection", "nhanvien");
        return "Admin/Nhanvien/themnhanvien";
    }

    @PostMapping("/edit")
    public String updateNhanVien(
        @Valid @ModelAttribute("nhanVien") NhanVien nhanVien,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        model.addAttribute("activeSection", "nhanvien");
        return "Admin/Nhanvien/themnhanvien";
    }

    // Kiểm tra email nếu đã đổi
    Optional<NhanVien> emailNhanVien = nhanVienRepository.findByEmail(nhanVien.getEmail());
    if (emailNhanVien.isPresent() && !emailNhanVien.get().getMaNV().equals(nhanVien.getMaNV())) {
        result.rejectValue("email", "error.nhanVien", "Email đã tồn tại trong hệ thống.");
        model.addAttribute("activeSection", "nhanVien");
        return "Admin/Nhanvien/themnhanvien";
    }

    // Kiểm tra CCCD nếu đã đổi
    Optional<NhanVien> cccdNhanVien = nhanVienRepository.findByCccd(nhanVien.getCccd());
    if (cccdNhanVien.isPresent() && !cccdNhanVien.get().getMaNV().equals(nhanVien.getMaNV())) {
        result.rejectValue("cccd", "error.nhanVien", "CCCD đã tồn tại trong hệ thống.");
        model.addAttribute("activeSection", "nhanVien");
        return "Admin/Nhanvien/themnhanvien";
    }

    // Tiếp tục cập nhật
    NhanVien existingNhanVien = nhanVienRepository.findById(nhanVien.getMaNV())
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên có ID: " + nhanVien.getMaNV()));

    nhanVien.setPassword(existingNhanVien.getPassword());
    nhanVien.setEnabled(existingNhanVien.isEnabled());

    nhanVienRepository.save(nhanVien);

    taiKhoanRepository.findById(nhanVien.getMaNV()).ifPresent(taiKhoan -> {
        taiKhoan.setUsername(nhanVien.getTenNhanVien());
        taiKhoan.setEmail(nhanVien.getEmail());
        taiKhoan.setEnabled(nhanVien.isEnabled());
        taiKhoanRepository.save(taiKhoan);
    });

    redirectAttributes.addFlashAttribute("successMessage", "Cập nhật nhân viên và tài khoản thành công!");
    return "redirect:/admin/nhanvien";
}

    @GetMapping("/xoa-nhanvien")
    public String deleteNhanVien(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        if (!nhanVienRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhân viên để xóa.");
            return "redirect:/admin/docgia";
        }

        // Xóa độc giả
        nhanVienRepository.deleteById(id);

        // Tùy chọn: Xóa tài khoản luôn nếu cần
        taiKhoanRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("successMessage", "Xóa nhân viên và tài khoản thành công!");
        return "redirect:/admin/nhanvien";
    }
}
