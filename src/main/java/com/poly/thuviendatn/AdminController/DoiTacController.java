package com.poly.thuviendatn.AdminController;

import com.poly.thuviendatn.Model.NhaXuatBan;
import com.poly.thuviendatn.Repository.NhaXuatBanRepository;
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
@RequestMapping("/admin/doitac")
public class DoiTacController {

    private final NhaXuatBanRepository nhaXuatBanRepository;

    @Autowired
    public DoiTacController(NhaXuatBanRepository nhaXuatBanRepository) {
        this.nhaXuatBanRepository = nhaXuatBanRepository;
    }

    @GetMapping
    public String showDoiTacList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NhaXuatBan> doiTacPage;
        if (keyword != null && !keyword.isEmpty()) {
            doiTacPage = nhaXuatBanRepository.findByTenNXBContainingIgnoreCase(keyword, pageable);
        } else {
            doiTacPage = nhaXuatBanRepository.findAll(pageable);
        }
        model.addAttribute("doiTacPage", doiTacPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeSection", "doitac");
        return "admin/doitac/doitac";
    }

    @GetMapping("/them")
    public String showAddDoiTacForm(Model model) {
        model.addAttribute("doiTac", new NhaXuatBan());
        model.addAttribute("activeSection", "doitac");
        return "admin/doitac/themdoitac";
    }

    @PostMapping("/them")
    public String addDoiTac(
            @Valid @ModelAttribute("doiTac") NhaXuatBan nhaXuatBan,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("activeSection", "doitac");
            return "admin/doitac/themdoitac";
        }
        nhaXuatBanRepository.save(nhaXuatBan);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm đối tác thành công!");
        return "redirect:/admin/doitac";
    }

    @GetMapping("/sua")
    public String showEditDoiTacForm(@RequestParam("id") Integer id, Model model) {
        NhaXuatBan nhaXuatBan = nhaXuatBanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đối tác không tồn tại: " + id));
        model.addAttribute("doiTac", nhaXuatBan);
        model.addAttribute("activeSection", "doitac");
        return "admin/doitac/themdoitac";
    }

    @GetMapping("/xoa")
    public String deleteDoiTac(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        nhaXuatBanRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa đối tác thành công!");
        return "redirect:/admin/doitac";
    }
}