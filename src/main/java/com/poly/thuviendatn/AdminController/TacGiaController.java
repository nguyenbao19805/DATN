package com.poly.thuviendatn.AdminController;

import com.poly.thuviendatn.Model.TacGia;
import com.poly.thuviendatn.Repository.TacGiaRepository;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/tacgia")
public class TacGiaController {

    @Autowired
    private TacGiaRepository tacGiaRepository;

    // 📌 Hiển thị danh sách tác giả + tìm kiếm + phân trang
    @GetMapping
    public String listTacGia(Model model,
                             @RequestParam(defaultValue = "0") int page,
@RequestParam(defaultValue = "10") int size,

                             @RequestParam(required = false) String keyword) {

        Page<TacGia> tacGiaPage;

        if (keyword != null && !keyword.isEmpty()) {
            tacGiaPage = tacGiaRepository.findByTenTacGiaContainingIgnoreCase( keyword, PageRequest.of(page, size));
        } else {
            tacGiaPage = tacGiaRepository.findAll(PageRequest.of(page, size));
        }

        model.addAttribute("tacGiaPage", tacGiaPage);
        model.addAttribute("tacGiaKeyword", keyword);
        model.addAttribute("activeSection", "tacgia");
        return "admin/Tacgia/tacgia";
    }

    // 📌 Hiển thị form thêm tác giả
    @GetMapping("/them")
    public String showAddForm(Model model) {
        model.addAttribute("tacGia", new TacGia());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeSection", "tacgia");
        return "admin/Tacgia/themtacgia";
    }

    // 📌 Xử lý thêm tác giả
    @PostMapping("/them")
    public String addTacGia(@Valid @ModelAttribute("tacGia") TacGia tacGia,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "admin/Tacgia/themtacgia";
        }
        tacGiaRepository.save(tacGia);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm tác giả thành công!");
        return "redirect:/admin/tacgia";
    }

    // 📌 Hiển thị form sửa tác giả
    @GetMapping("/sua-tacgia")
    public String showEditForm(@RequestParam("id") Integer id, Model model) {
        TacGia tacGia = tacGiaRepository.findById(id).orElse(null);
        if (tacGia == null) {
            return "redirect:/admin/tacgia";
        }
        model.addAttribute("tacGia", tacGia);
        model.addAttribute("isEdit", true);
        model.addAttribute("activeSection", "tacgia");
        return "admin/Tacgia/themtacgia";
    }

    // 📌 Xử lý cập nhật tác giả
    @PostMapping("/sua")
    public String updateTacGia(@Valid @ModelAttribute("tacGia") TacGia tacGia,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "admin/Tacgia/themtacgia";
        }
        tacGiaRepository.save(tacGia);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật tác giả thành công!");
        return "redirect:/admin/tacgia";
    }

    // 📌 Xử lý xóa tác giả
    @GetMapping("/xoa-tacgia")
    public String deleteTacGia(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        if (tacGiaRepository.existsById(id)) {
            tacGiaRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa tác giả thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tác giả cần xóa.");
        }
        return "redirect:/admin/tacgia";
    }
}
