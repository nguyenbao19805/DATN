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

    // Hiển thị danh sách danh mục
    @GetMapping("/danhmuc")
    public String showDanhMuc(
            @RequestParam(defaultValue = "0") int danhMucPage,
            @RequestParam(defaultValue = "10") int danhMucSize,
            @RequestParam(required = false) String danhMucKeyword,
            @RequestParam(defaultValue = "0") int sachPage,
            @RequestParam(defaultValue = "10") int sachSize,
            @RequestParam(required = false) String sachKeyword,
            @RequestParam(defaultValue = "0") int loaiSachPage,
            @RequestParam(defaultValue = "10") int loaiSachSize,
            @RequestParam(required = false) String loaiSachKeyword,
            Model model) {
        try {
            Pageable pageable = PageRequest.of(danhMucPage, danhMucSize);
            Page<DanhMuc> danhMucPageResult;
            if (danhMucKeyword != null && !danhMucKeyword.isEmpty()) {
                danhMucPageResult = danhMucRepository.findByTenDanhMucContainingIgnoreCase(danhMucKeyword, pageable);
            } else {
                danhMucPageResult = danhMucRepository.findAll(pageable);
            }
            model.addAttribute("danhMucPage", danhMucPageResult);
            model.addAttribute("danhMucKeyword", danhMucKeyword);
            model.addAttribute("sachPage", Page.empty());
            model.addAttribute("loaiSachPage", Page.empty());
            model.addAttribute("sachKeyword", sachKeyword);
            model.addAttribute("loaiSachKeyword", loaiSachKeyword);
            model.addAttribute("activeTab", "danhmuc");
            model.addAttribute("activeSection", "quanlysach");
            return "admin/quanlysach/quanlysach";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tải danh sách danh mục: " + e.getMessage());
            model.addAttribute("danhMucPage", Page.empty());
            model.addAttribute("danhMucKeyword", danhMucKeyword);
            model.addAttribute("sachPage", Page.empty());
            model.addAttribute("loaiSachPage", Page.empty());
            model.addAttribute("sachKeyword", sachKeyword);
            model.addAttribute("loaiSachKeyword", loaiSachKeyword);
            model.addAttribute("activeTab", "danhmuc");
            model.addAttribute("activeSection", "quanlysach");
            return "admin/quanlysach/quanlysach";
        }
    }

    // Hiển thị form thêm danh mục
    @GetMapping("/themdanhmuc")
    public String showAddForm(Model model) {
        model.addAttribute("danhMuc", new DanhMuc());
        model.addAttribute("isEdit", false);
        model.addAttribute("dsLoaiSach", loaiSachRepository.findAll());
        model.addAttribute("activeSection", "quanlysach");
        model.addAttribute("activeTab", "danhmuc");
        return "admin/quanlysach/themdanhmuc";
    }

    // Xử lý thêm danh mục
    @PostMapping("/themdanhmuc")
    public String addDanhMuc(
            @Valid @ModelAttribute("danhMuc") DanhMuc danhMuc,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("dsLoaiSach", loaiSachRepository.findAll());
            model.addAttribute("activeSection", "quanlysach");
            model.addAttribute("activeTab", "danhmuc");
            return "admin/quanlysach/themdanhmuc";
        }
        danhMucRepository.save(danhMuc);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm danh mục thành công!");
        return "redirect:/admin/quanlysach?activeTab=danhmuc";
    }

    // Hiển thị form sửa danh mục
    @GetMapping("/sua-danhmuc")
    public String showEditForm(@RequestParam("id") Integer id, Model model) {
        DanhMuc danhMuc = danhMucRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục có ID: " + id));
        model.addAttribute("danhMuc", danhMuc);
        model.addAttribute("isEdit", true);
        model.addAttribute("dsLoaiSach", loaiSachRepository.findAll());
        model.addAttribute("activeSection", "quanlysach");
        model.addAttribute("activeTab", "danhmuc");
        return "admin/quanlysach/themdanhmuc";
    }

    // Xử lý cập nhật danh mục
    @PostMapping("/sua-danhmuc")
    public String updateDanhMuc(
            @Valid @ModelAttribute("danhMuc") DanhMuc danhMuc,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("dsLoaiSach", loaiSachRepository.findAll());
            model.addAttribute("activeSection", "quanlysach");
            model.addAttribute("activeTab", "danhmuc");
            return "admin/quanlysach/themdanhmuc";
        }
        danhMucRepository.save(danhMuc);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
        return "redirect:/admin/quanlysach?activeTab=danhmuc";
    }

    // Xóa danh mục
    @GetMapping("/xoa-danhmuc")
    public String deleteDanhMuc(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            danhMucRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa danh mục (có thể đang được sử dụng)!");
        }
        return "redirect:/admin/quanlysach?activeTab=danhmuc";
    }
}
