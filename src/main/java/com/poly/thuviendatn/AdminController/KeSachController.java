package com.poly.thuviendatn.AdminController;

import com.poly.thuviendatn.Model.KeSach;
import com.poly.thuviendatn.Model.ViTriKeSach;
import com.poly.thuviendatn.Model.Sach;
import com.poly.thuviendatn.Repository.KeSachRepository;
import com.poly.thuviendatn.Repository.ViTriKeSachRepository;
import com.poly.thuviendatn.Repository.SachRepository;
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
    private final ViTriKeSachRepository viTriKeSachRepository;
    private final SachRepository sachRepository;

    @Autowired
    public KeSachController(KeSachRepository keSachRepository, ViTriKeSachRepository viTriKeSachRepository, SachRepository sachRepository) {
        this.keSachRepository = keSachRepository;
        this.viTriKeSachRepository = viTriKeSachRepository;
        this.sachRepository = sachRepository;
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
        keSachPage.getContent().forEach(keSach -> {
            Integer currentBooks = viTriKeSachRepository.sumSoLuongByKeSach(keSach.getMaKeSach());
            keSach.setCurrentBooks(currentBooks != null ? currentBooks : 0);
            keSach.setViTriKeSachs(viTriKeSachRepository.findByKeSachMaKeSach(keSach.getMaKeSach()));
        });
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
        try {
            keSachRepository.save(keSach);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm kệ sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm kệ sách: " + e.getMessage());
            model.addAttribute("activeSection", "kesach");
            return "admin/kesach/themkesach";
        }
        return "redirect:/admin/kesach";
    }

    @GetMapping("/edit")
    public String showEditKeSachForm(
            @RequestParam("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            KeSach keSach = keSachRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Kệ sách không tồn tại: " + id));
            model.addAttribute("keSach", keSach);
            model.addAttribute("activeSection", "kesach");
            return "admin/kesach/themkesach";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tải kệ sách: " + e.getMessage());
            return "redirect:/admin/kesach";
        }
    }

    @PostMapping("/edit")
    public String updateKeSachEdit(
            @RequestParam("id") Long id,
            @Valid @ModelAttribute("keSach") KeSach keSach,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("activeSection", "kesach");
            return "admin/kesach/themkesach";
        }
        try {
            KeSach existingKeSach = keSachRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Kệ sách không tồn tại: " + id));
            existingKeSach.setTenKeSach(keSach.getTenKeSach());
            existingKeSach.setViTri(keSach.getViTri());
            existingKeSach.setSucChua(keSach.getSucChua());
            existingKeSach.setMoTa(keSach.getMoTa());

            Integer currentBooks = viTriKeSachRepository.sumSoLuongByKeSach(id);
            currentBooks = currentBooks != null ? currentBooks : 0;
            if (currentBooks > keSach.getSucChua()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Sức chứa mới (" + keSach.getSucChua() + ") nhỏ hơn số sách hiện tại (" + currentBooks + ")!");
                return "redirect:/admin/kesach";
            }

            keSachRepository.save(existingKeSach);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật kệ sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật kệ sách: " + e.getMessage());
            model.addAttribute("activeSection", "kesach");
            return "admin/kesach/themkesach";
        }
        return "redirect:/admin/kesach";
    }

    @GetMapping("/sua-kesach")
    public String showEditKeSachFormLegacy(
            @RequestParam("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        return showEditKeSachForm(id, model, redirectAttributes);
    }

    @PostMapping("/sua-kesach")
    public String updateKeSach(
            @RequestParam("id") Long id,
            @Valid @ModelAttribute("keSach") KeSach keSach,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        return updateKeSachEdit(id, keSach, result, model, redirectAttributes);
    }

    @GetMapping("/xoa-kesach")
    public String xoaKeSach(
            @RequestParam("id") Long keSachId,
            RedirectAttributes redirectAttributes) {
        try {
            KeSach keSach = keSachRepository.findById(keSachId)
                    .orElseThrow(() -> new IllegalArgumentException("Kệ sách không tồn tại: " + keSachId));
            
            Integer currentBooks = viTriKeSachRepository.sumSoLuongByKeSach(keSachId);
            if (currentBooks != null && currentBooks > 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa kệ sách vì vẫn còn " + currentBooks + " sách trên kệ. Vui lòng xóa sách trước!");
                return "redirect:/admin/kesach";
            }

            keSachRepository.deleteById(keSachId);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa kệ sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa kệ sách: " + e.getMessage());
        }
        return "redirect:/admin/kesach";
    }

    @GetMapping("/xoa-vitri")
    public String deleteViTriKeSach(
            @RequestParam("id") Integer viTriId,
            @RequestParam("keSachId") Long keSachId,
            RedirectAttributes redirectAttributes) {
        try {
            ViTriKeSach viTri = viTriKeSachRepository.findById(viTriId)
                    .orElseThrow(() -> new IllegalArgumentException("Vị trí kệ sách không tồn tại: " + viTriId));
            
            viTriKeSachRepository.deleteById(viTriId);

            Integer updatedBooksOnKe = viTriKeSachRepository.sumSoLuongByKeSach(keSachId);
            KeSach keSach = keSachRepository.findById(keSachId)
                    .orElseThrow(() -> new IllegalArgumentException("Kệ sách không tồn tại: " + keSachId));
            keSach.setCurrentBooks(updatedBooksOnKe != null ? updatedBooksOnKe : 0);
            keSachRepository.save(keSach);

            Integer maSach = viTri.getSach().getMaSach();
            Integer updatedBooksOnShelf = viTriKeSachRepository.sumSoLuongBySach(maSach);
            Sach sach = sachRepository.findById(maSach)
                    .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại: " + maSach));
            sach.setCurrentBooks(updatedBooksOnShelf != null ? updatedBooksOnShelf : 0);
            sach.setOnShelf(updatedBooksOnShelf != null && updatedBooksOnShelf > 0);
            sachRepository.save(sach);

            redirectAttributes.addFlashAttribute("successMessage", "Xóa sách khỏi kệ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa sách khỏi kệ: " + e.getMessage());
        }
        return "redirect:/admin/kesach";
    }

    @PostMapping("/remove-from-shelf")
    public String removeFromShelf(
            @RequestParam("viTriId") Integer viTriId,
            @RequestParam("keSachId") Long keSachId,
            @RequestParam("soLuongXoa") Integer soLuongXoa,
            RedirectAttributes redirectAttributes) {
        try {
            ViTriKeSach viTri = viTriKeSachRepository.findById(viTriId)
                    .orElseThrow(() -> new IllegalArgumentException("Vị trí kệ sách không tồn tại: " + viTriId));

            if (soLuongXoa <= 0 || soLuongXoa > viTri.getSoLuong()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Số lượng xóa không hợp lệ! Vui lòng chọn số từ 1 đến " + viTri.getSoLuong());
                return "redirect:/admin/kesach";
            }

            Integer newSoLuong = viTri.getSoLuong() - soLuongXoa;
            if (newSoLuong == 0) {
                viTriKeSachRepository.deleteById(viTriId);
            } else {
                viTri.setSoLuong(newSoLuong);
                viTriKeSachRepository.save(viTri);
            }

            Integer updatedBooksOnKe = viTriKeSachRepository.sumSoLuongByKeSach(keSachId);
            KeSach keSach = keSachRepository.findById(keSachId)
                    .orElseThrow(() -> new IllegalArgumentException("Kệ sách không tồn tại: " + keSachId));
            keSach.setCurrentBooks(updatedBooksOnKe != null ? updatedBooksOnKe : 0);
            keSachRepository.save(keSach);

            Sach sach = viTri.getSach();
            Integer updatedBooksOnShelf = viTriKeSachRepository.sumSoLuongBySach(sach.getMaSach());
            sach.setCurrentBooks(updatedBooksOnShelf != null ? updatedBooksOnShelf : 0);
            sach.setOnShelf(updatedBooksOnShelf != null && updatedBooksOnShelf > 0);
            sachRepository.save(sach);

            redirectAttributes.addFlashAttribute("successMessage", "Xóa " + soLuongXoa + " sách khỏi kệ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa sách khỏi kệ: " + e.getMessage());
        }
        return "redirect:/admin/kesach";
    }
}