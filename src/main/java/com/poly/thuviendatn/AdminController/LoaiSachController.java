package com.poly.thuviendatn.AdminController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.thuviendatn.Model.DanhMuc;
import com.poly.thuviendatn.Model.DocGia;
import com.poly.thuviendatn.Model.LoaiSach;
import com.poly.thuviendatn.Model.Sach;
import com.poly.thuviendatn.Repository.DanhMucRepository;
import com.poly.thuviendatn.Repository.LoaiSachRepository;
import com.poly.thuviendatn.Repository.NhaXuatBanRepository;
import com.poly.thuviendatn.Repository.SachRepository;
import com.poly.thuviendatn.Repository.TacGiaRepository;
import com.poly.thuviendatn.Service.SachService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/quanlysach")
public class LoaiSachController {
    @Autowired
    private SachRepository sachRepository;
    
    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private TacGiaRepository tacGiaRepository;

    @Autowired
    private NhaXuatBanRepository nhaXuatBanRepository;

    @Autowired
    private LoaiSachRepository loaiSachRepository;

    @Autowired
    private SachService sachService;

  
   @GetMapping
public String showQuanLySach(
        @RequestParam(defaultValue = "0") int sachPage,
        @RequestParam(defaultValue = "10") int sachSize,
        @RequestParam(required = false) String sachKeyword,

        @RequestParam(defaultValue = "0") int loaiSachPage,
        @RequestParam(defaultValue = "10") int loaiSachSize,
        @RequestParam(required = false) String loaiSachKeyword,

        @RequestParam(defaultValue = "0") int danhMucPage,
        @RequestParam(defaultValue = "10") int danhMucSize,
        @RequestParam(required = false) String danhMucKeyword,

        @RequestParam(defaultValue = "sach") String activeTab,

        Model model
) {
    // ==== Danh sách sách ====
    Page<Sach> sachPageResult = sachService.findAllBooks(sachPage, sachSize, sachKeyword);
    model.addAttribute("sachPage", sachPageResult);
    model.addAttribute("sachKeyword", sachKeyword);

    // ==== Danh sách loại sách ====
    Pageable loaiSachPageable = PageRequest.of(loaiSachPage, loaiSachSize);
    Page<LoaiSach> loaiSachPageResult = loaiSachRepository
        .findByTenLoaiSachContainingIgnoreCase(loaiSachKeyword != null ? loaiSachKeyword : "", loaiSachPageable);
    model.addAttribute("loaiSachPage", loaiSachPageResult);
    model.addAttribute("loaiSachKeyword", loaiSachKeyword);

    // ==== Danh sách danh mục ====
    Pageable danhMucPageable = PageRequest.of(danhMucPage, danhMucSize);
    Page<DanhMuc> danhMucPageResult = danhMucRepository
        .findByTenDanhMucContainingIgnoreCase(danhMucKeyword != null ? danhMucKeyword : "", danhMucPageable);
    model.addAttribute("danhMucPage", danhMucPageResult);
    model.addAttribute("danhMucKeyword", danhMucKeyword);

    // ==== Tab hiện tại ====
    model.addAttribute("activeTab", activeTab);
    model.addAttribute("activeSection", "quanlysach");

    return "admin/quanlysach/quanlysach";
}



    @GetMapping("/themloaisach")
public String showAddForm(Model model) {
    model.addAttribute("loaiSach", new LoaiSach());
    model.addAttribute("isEdit", false); // Thêm
    model.addAttribute("activeSection", "quanlysach");
    return "admin/quanlysach/themloaisach";
}

    @PostMapping("/themloaisach")
    public String addLoaiSach(
            @Valid @ModelAttribute("loaiSach") LoaiSach loaiSach,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/quanlysach/themloaisach";
        }
        loaiSachRepository.save(loaiSach);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm loại sách thành công!");
        return "redirect:/admin/quanlysach";
    }

    @GetMapping("/sualoaisach")
    public String showEditLoaiSachForm(@RequestParam("id") Integer id, Model model) {
    LoaiSach loaiSach = loaiSachRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Loại sách không tồn tại: " + id));
    model.addAttribute("loaiSach", loaiSach);
    model.addAttribute("isEdit", true); // Sửa
    model.addAttribute("activeSection", "quanlysach");
    return "admin/quanlysach/themloaisach";
}

    @PostMapping("/sualoaisach")
    public String updateLoaiSach(
            @Valid @ModelAttribute("loaiSach") LoaiSach loaiSach,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("activeSection", "quanlysach");
            return "admin/quanlysach/themloaisach";
        }

        loaiSachRepository.save(loaiSach);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật loại sách thành công!");
        return "redirect:/admin/quanlysach";
    }

    @GetMapping("/xoaloaisach")
    public String deleteLoaiSach(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        if (!loaiSachRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy độc giả để xóa.");
            return "redirect:/admin/docgia";
        }

        // Xóa độc giả
        loaiSachRepository.deleteById(id);


        redirectAttributes.addFlashAttribute("successMessage", "Xóa loại sáchthành công!");
        return "redirect:/admin/quanlysach";
    }

}
