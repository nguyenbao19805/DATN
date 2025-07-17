package com.poly.thuviendatn.AdminController;

import com.poly.thuviendatn.Model.DocGia;
import com.poly.thuviendatn.Model.TaiKhoan;
import com.poly.thuviendatn.Repository.DocGiaRepository;
import com.poly.thuviendatn.Repository.TaiKhoanRepository;
import com.poly.thuviendatn.Service.DocGiaService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/docgia")
public class DocGiaController {
    @Autowired
    private DocGiaService docGiaService;
    @Autowired
    private DocGiaRepository docGiaRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    // 1️⃣ Danh sách độc giả (có tìm kiếm)
    @GetMapping
    public String listDocGia(Model model,
        @RequestParam(name = "docGiaPage", defaultValue = "0") int page,
        @RequestParam(name = "docGiaSize", defaultValue = "10") int size,
        @RequestParam(name = "docGiaKeyword", required = false) String keyword) {

        Page<DocGia> docGiaPage;
        if (keyword != null && !keyword.isEmpty()) {
            docGiaPage = docGiaRepository.findByTenDocGiaContainingIgnoreCaseOrCccdContainingIgnoreCase(keyword, keyword, PageRequest.of(page, size));
        } else {
            docGiaPage = docGiaRepository.findAll(PageRequest.of(page, size));
        }

        model.addAttribute("docGiaPage", docGiaPage);
        model.addAttribute("docGiaKeyword", keyword);
        model.addAttribute("activeSection", "docgia");

        return "Admin/Docgia/docgia";
    }

    // 2️⃣ Hiển thị form thêm
    @GetMapping("/them")
    public String showAddForm(Model model) {
        model.addAttribute("docGia", new DocGia());
        model.addAttribute("activeSection", "docgia");
        return "Admin/Docgia/themdocgia";
    }

@PostMapping("/them")
public String addDocGia(
        @Valid @ModelAttribute("docGia") DocGia docGia,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes) {

    // Nếu validate từ @Valid có lỗi
    if (result.hasErrors()) {
        model.addAttribute("activeSection", "docgia");
        return "Admin/Docgia/themdocgia";
    }

    // Kiểm tra email đã tồn tại
    if (docGiaRepository.existsByEmail(docGia.getEmail())) {
        result.rejectValue("email", "error.docGia", "Email đã tồn tại trong hệ thống.");
        model.addAttribute("activeSection", "docgia");
        return "Admin/Docgia/themdocgia";
    }

    // Kiểm tra CCCD đã tồn tại
    if (docGiaRepository.existsByCccd(docGia.getCccd())) {
        result.rejectValue("cccd", "error.docGia", "CCCD đã tồn tại trong hệ thống.");
        model.addAttribute("activeSection", "docgia");
        return "Admin/Docgia/themdocgia";
    }
    

    // Gọi service xử lý lưu + gửi mail
    docGiaService.createDocGiaAndSendPassword(docGia);

    redirectAttributes.addFlashAttribute("successMessage", "Thêm độc giả thành công! Mật khẩu đã được gửi qua email.");
    return "redirect:/admin/docgia";
}

   // 3️⃣ Hiển thị form sửa
    @GetMapping("/sua-docgia")
    public String showEditForm(@RequestParam("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<DocGia> optionalDocGia = docGiaRepository.findById(id);
        if (optionalDocGia.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy độc giả để sửa.");
            return "redirect:/admin/docgia";
        }

        model.addAttribute("docGia", optionalDocGia.get());
        model.addAttribute("activeSection", "docgia");
        return "Admin/Docgia/themdocgia";
    }

    // 4️⃣ Xử lý cập nhật độc giả và tài khoản
@PostMapping("/edit")
public String updateDocGia(
        @Valid @ModelAttribute("docGia") DocGia docGia,
        BindingResult result,
        Model model,
        RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
        model.addAttribute("activeSection", "docgia");
        return "Admin/Docgia/themdocgia";
    }

    // Kiểm tra email nếu đã đổi
    Optional<DocGia> emailDocGia = docGiaRepository.findByEmail(docGia.getEmail());
    if (emailDocGia.isPresent() && !emailDocGia.get().getMaDocGia().equals(docGia.getMaDocGia())) {
        result.rejectValue("email", "error.docGia", "Email đã tồn tại trong hệ thống.");
        model.addAttribute("activeSection", "docgia");
        return "Admin/Docgia/themdocgia";
    }

    // Kiểm tra CCCD nếu đã đổi
    Optional<DocGia> cccdDocGia = docGiaRepository.findByCccd(docGia.getCccd());
    if (cccdDocGia.isPresent() && !cccdDocGia.get().getMaDocGia().equals(docGia.getMaDocGia())) {
        result.rejectValue("cccd", "error.docGia", "CCCD đã tồn tại trong hệ thống.");
        model.addAttribute("activeSection", "docgia");
        return "Admin/Docgia/themdocgia";
    }

    // Tiếp tục cập nhật
    DocGia existingDocGia = docGiaRepository.findById(docGia.getMaDocGia())
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy độc giả có ID: " + docGia.getMaDocGia()));

    docGia.setPassword(existingDocGia.getPassword());
    docGia.setEnabled(existingDocGia.isEnabled());

    docGiaRepository.save(docGia);

    taiKhoanRepository.findById(docGia.getMaDocGia()).ifPresent(taiKhoan -> {
        taiKhoan.setUsername(docGia.getTenDocGia());
        taiKhoan.setEmail(docGia.getEmail());
        taiKhoan.setEnabled(docGia.isEnabled());
        taiKhoanRepository.save(taiKhoan);
    });

    redirectAttributes.addFlashAttribute("successMessage", "Cập nhật độc giả và tài khoản thành công!");
    return "redirect:/admin/docgia";
}

    // 5️⃣ Xóa độc giả (và tùy chọn xóa luôn tài khoản nếu muốn)
    @GetMapping("/xoa-docgia")
    public String deleteDocGia(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        if (!docGiaRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy độc giả để xóa.");
            return "redirect:/admin/docgia";
        }

        // Xóa độc giả
        docGiaRepository.deleteById(id);

        // Tùy chọn: Xóa tài khoản luôn nếu cần
        taiKhoanRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("successMessage", "Xóa độc giả và tài khoản thành công!");
        return "redirect:/admin/docgia";
    }

    
}
