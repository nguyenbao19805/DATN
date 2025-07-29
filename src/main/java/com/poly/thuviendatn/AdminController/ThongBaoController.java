package com.poly.thuviendatn.AdminController;

import com.poly.thuviendatn.Model.ThongBao;
import com.poly.thuviendatn.Model.TaiKhoan;
import com.poly.thuviendatn.Repository.ThongBaoRepository;
import com.poly.thuviendatn.Repository.TaiKhoanRepository;
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

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/thongbao")
public class ThongBaoController {

    private final ThongBaoRepository thongBaoRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    @Autowired
    public ThongBaoController(ThongBaoRepository thongBaoRepository, TaiKhoanRepository taiKhoanRepository) {
        this.thongBaoRepository = thongBaoRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @GetMapping
    public String showThongBaoList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ThongBao> thongBaoPage;
        if (keyword != null && !keyword.isEmpty()) {
            thongBaoPage = thongBaoRepository.findByTieuDeContainingIgnoreCaseOrTaiKhoanUsernameContainingIgnoreCase(keyword, keyword, pageable);
        } else {
            thongBaoPage = thongBaoRepository.findAll(pageable);
        }
        model.addAttribute("thongBaoPage", thongBaoPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeSection", "thongbao");
        return "admin/thongbao/thongbao";
    }

    @GetMapping("/them")
    public String showAddThongBaoForm(Model model) {
        model.addAttribute("thongBao", new ThongBao());
        model.addAttribute("taiKhoans", taiKhoanRepository.findAll());
        model.addAttribute("activeSection", "thongbao");
        return "admin/thongbao/themthongbao";
    }

    @PostMapping("/them")
    public String addThongBao(
            @Valid @ModelAttribute("thongBao") ThongBao thongBao,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("taiKhoans", taiKhoanRepository.findAll());
            model.addAttribute("activeSection", "thongbao");
            return "admin/thongbao/themthongbao";
        }
        if (thongBao.getTaiKhoan() == null || thongBao.getTaiKhoan().getMaTaiKhoan() == null) {
            result.rejectValue("taiKhoan", "error.taiKhoan", "Vui lòng chọn người nhận!");
            model.addAttribute("taiKhoans", taiKhoanRepository.findAll());
            return "admin/thongbao/themthongbao";
        }
        thongBao.setNgayGui(LocalDateTime.now());
        thongBao.setDaDoc(false);
        thongBaoRepository.save(thongBao);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm thông báo thành công!");
        return "redirect:/admin/thongbao";
    }

    @GetMapping("/sua")
    public String showEditThongBaoForm(@RequestParam("id") Integer id, Model model) {
        ThongBao thongBao = thongBaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Thông báo không tồn tại: " + id));
        model.addAttribute("thongBao", thongBao);
        model.addAttribute("taiKhoans", taiKhoanRepository.findAll());
        model.addAttribute("activeSection", "thongbao");
        return "admin/thongbao/themthongbao";
    }

    @GetMapping("/xoa")
    public String deleteThongBao(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        thongBaoRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa thông báo thành công!");
        return "redirect:/admin/thongbao";
    }
}