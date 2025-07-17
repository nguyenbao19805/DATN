package com.poly.thuviendatn.AdminController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.thuviendatn.Model.DanhMuc;
import com.poly.thuviendatn.Repository.DanhMucRepository;
import com.poly.thuviendatn.Repository.LoaiSachRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/quanlysach")
public class DanhMucController {

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private LoaiSachRepository loaiSachRepository;

    // ================= HIỂN THỊ FORM THÊM =================
    @GetMapping("/themdanhmuc")
    public String showAddForm(Model model) {
        model.addAttribute("danhMuc", new DanhMuc());
        model.addAttribute("isEdit", false);
        model.addAttribute("dsLoaiSach", loaiSachRepository.findAll());
        model.addAttribute("activeSection", "quanlysach");
        return "admin/quanlysach/themdanhmuc";
    }

    // ================= XỬ LÝ THÊM =================
    @PostMapping("/themdanhmuc")
    public String addDanhMuc(@Valid @ModelAttribute("danhMuc") DanhMuc danhMuc,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("dsLoaiSach", loaiSachRepository.findAll());
            model.addAttribute("activeSection", "quanlysach");
            return "admin/quanlysach/themdanhmuc";
        }
        danhMucRepository.save(danhMuc);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm danh mục thành công!");
        return "redirect:/admin/quanlysach";
    }

    // ================= HIỂN THỊ FORM SỬA =================
    @GetMapping("/sua-danhmuc")
    public String showEditForm(@RequestParam("id") Integer id, Model model) {
        DanhMuc danhMuc = danhMucRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục có ID: " + id));
        model.addAttribute("danhMuc", danhMuc);
        model.addAttribute("isEdit", true);
        model.addAttribute("dsLoaiSach", loaiSachRepository.findAll());
        model.addAttribute("activeSection", "quanlysach");
        return "admin/quanlysach/themdanhmuc";
    }

    // ================= XỬ LÝ CẬP NHẬT =================
    @PostMapping("/sua-danhmuc")
    public String updateDanhMuc(@Valid @ModelAttribute("danhMuc") DanhMuc danhMuc,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("dsLoaiSach", loaiSachRepository.findAll());
            model.addAttribute("activeSection", "quanlysach");
            return "admin/quanlysach/themdanhmuc";
        }
        danhMucRepository.save(danhMuc);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
        return "redirect:/admin/quanlysach";
    }

    // ================= XÓA DANH MỤC =================
    @GetMapping("/xoa-danhmuc")
    public String deleteDanhMuc(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            danhMucRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa danh mục (có thể đang được sử dụng)!");
        }
        return "redirect:/admin/quanlysach";
    }
}
