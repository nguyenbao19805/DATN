package com.poly.thuviendatn.AdminController;

import com.poly.thuviendatn.Model.KeSach;
import com.poly.thuviendatn.Repository.KeSachRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/kesach")
public class KeSachController {

    private final KeSachRepository keSachRepository;

    @Autowired
    public KeSachController(KeSachRepository keSachRepository) {
        this.keSachRepository = keSachRepository;
    }

    @GetMapping
    public String showKeSachList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<KeSach> keSachPage;
        if (keyword != null && !keyword.isEmpty()) {
            keSachPage = keSachRepository.findByTenKeSachContainingIgnoreCaseOrViTriContainingIgnoreCase(keyword, pageable);
        } else {
            keSachPage = keSachRepository.findAll(pageable);
        }
        model.addAttribute("keSachPage", keSachPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeSection", "kesach");
        return "admin/kesach/kesach";
    }

    @GetMapping("/them")
    public String showAddKeSachForm(Model model) {
        model.addAttribute("keSach", new KeSach());
        model.addAttribute("activeSection", "kesach");
        return "admin/kesach/themkesach";
    }

    @PostMapping("/them")
    public String addKeSach(
            @Valid @ModelAttribute("keSach") KeSach keSach,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("activeSection", "kesach");
            return "admin/kesach/themkesach";
        }
        keSachRepository.save(keSach);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm kệ sách thành công!");
        return "redirect:/admin/kesach";
    }

    @GetMapping("/sua-kesach")
    public String showEditKeSachForm(@RequestParam("id") Long id, Model model) {
        KeSach keSach = keSachRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Kệ sách không tồn tại: " + id));
        model.addAttribute("keSach", keSach);
        model.addAttribute("activeSection", "kesach");
        return "admin/kesach/themkesach";
    }

    @PostMapping("/edit")
    public String updateKeSach(
            @RequestParam("id") Long id,
            @Valid @ModelAttribute("keSach") KeSach keSach,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("activeSection", "kesach");
            return "admin/kesach/themkesach";
        }
        KeSach existingKeSach = keSachRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Kệ sách không tồn tại: " + id));
        existingKeSach.setTenKeSach(keSach.getTenKeSach());
        existingKeSach.setViTri(keSach.getViTri());
        existingKeSach.setSucChua(keSach.getSucChua());
        existingKeSach.setMoTa(keSach.getMoTa());
        keSachRepository.save(existingKeSach);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật kệ sách thành công!");
        return "redirect:/admin/kesach";
    }

    @GetMapping("/xoa-kesach")
    public String deleteKeSach(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        keSachRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa kệ sách thành công!");
        return "redirect:/admin/kesach";
    }
}