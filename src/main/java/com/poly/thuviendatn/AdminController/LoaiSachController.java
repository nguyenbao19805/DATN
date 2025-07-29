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

import com.poly.thuviendatn.Model.LoaiSach;
import com.poly.thuviendatn.Repository.LoaiSachRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/quanlysach")
public class LoaiSachController {

    @Autowired
    private LoaiSachRepository loaiSachRepository;

    // Hiển thị danh sách loại sách
    @GetMapping("/loaisach")
    public String showLoaiSach(
            @RequestParam(defaultValue = "0") int loaiSachPage,
            @RequestParam(defaultValue = "10") int loaiSachSize,
            @RequestParam(required = false) String loaiSachKeyword,
            @RequestParam(defaultValue = "0") int sachPage,
            @RequestParam(defaultValue = "10") int sachSize,
            @RequestParam(required = false) String sachKeyword,
            @RequestParam(defaultValue = "0") int danhMucPage,
            @RequestParam(defaultValue = "10") int danhMucSize,
            @RequestParam(required = false) String danhMucKeyword,
            Model model) {
        try {
            Pageable pageable = PageRequest.of(loaiSachPage, loaiSachSize);
            Page<LoaiSach> loaiSachPageResult;
            if (loaiSachKeyword != null && !loaiSachKeyword.isEmpty()) {
                loaiSachPageResult = loaiSachRepository.findByTenLoaiSachContainingIgnoreCase(loaiSachKeyword, pageable);
            } else {
                loaiSachPageResult = loaiSachRepository.findAll(pageable);
            }
            model.addAttribute("loaiSachPage", loaiSachPageResult);
            model.addAttribute("loaiSachKeyword", loaiSachKeyword);
            model.addAttribute("sachPage", Page.empty());
            model.addAttribute("danhMucPage", Page.empty());
            model.addAttribute("sachKeyword", sachKeyword);
            model.addAttribute("danhMucKeyword", danhMucKeyword);
            model.addAttribute("activeTab", "loaisach");
            model.addAttribute("activeSection", "quanlysach");
            return "admin/quanlysach/quanlysach";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tải danh sách loại sách: " + e.getMessage());
            model.addAttribute("loaiSachPage", Page.empty());
            model.addAttribute("loaiSachKeyword", loaiSachKeyword);
            model.addAttribute("sachPage", Page.empty());
            model.addAttribute("danhMucPage", Page.empty());
            model.addAttribute("sachKeyword", sachKeyword);
            model.addAttribute("danhMucKeyword", danhMucKeyword);
            model.addAttribute("activeTab", "loaisach");
            model.addAttribute("activeSection", "quanlysach");
            return "admin/quanlysach/quanlysach";
        }
    }

    // Hiển thị form thêm loại sách
    @GetMapping("/themloaisach")
    public String showAddForm(Model model) {
        model.addAttribute("loaiSach", new LoaiSach());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeSection", "quanlysach");
        model.addAttribute("activeTab", "loaisach");
        return "admin/quanlysach/themloaisach";
    }

    // Xử lý thêm loại sách
    @PostMapping("/themloaisach")
    public String addLoaiSach(
            @Valid @ModelAttribute("loaiSach") LoaiSach loaiSach,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("activeSection", "quanlysach");
            model.addAttribute("activeTab", "loaisach");
            return "admin/quanlysach/themloaisach";
        }
        loaiSachRepository.save(loaiSach);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm loại sách thành công!");
        return "redirect:/admin/quanlysach?activeTab=loaisach";
    }

    // Hiển thị form sửa loại sách
    @GetMapping("/sualoaisach")
    public String showEditLoaiSachForm(@RequestParam("id") Integer id, Model model) {
        LoaiSach loaiSach = loaiSachRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loại sách không tồn tại: " + id));
        model.addAttribute("loaiSach", loaiSach);
        model.addAttribute("isEdit", true);
        model.addAttribute("activeSection", "quanlysach");
        model.addAttribute("activeTab", "loaisach");
        return "admin/quanlysach/themloaisach";
    }

    // Xử lý cập nhật loại sách
    @PostMapping("/sualoaisach")
    public String updateLoaiSach(
            @Valid @ModelAttribute("loaiSach") LoaiSach loaiSach,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("activeSection", "quanlysach");
            model.addAttribute("activeTab", "loaisach");
            return "admin/quanlysach/themloaisach";
        }
        loaiSachRepository.save(loaiSach);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật loại sách thành công!");
        return "redirect:/admin/quanlysach?activeTab=loaisach";
    }

    // Xóa loại sách
    @GetMapping("/xoaloaisach")
    public String deleteLoaiSach(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        if (!loaiSachRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy loại sách để xóa.");
            return "redirect:/admin/quanlysach?activeTab=loaisach";
        }
        try {
            loaiSachRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa loại sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa loại sách (có thể đang được sử dụng)!");
        }
        return "redirect:/admin/quanlysach?activeTab=loaisach";
    }
}
