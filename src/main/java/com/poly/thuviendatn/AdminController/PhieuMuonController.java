package com.poly.thuviendatn.AdminController;

import com.poly.thuviendatn.Model.*;
import com.poly.thuviendatn.Repository.*;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/nhapmuon")
public class PhieuMuonController {

    private final TaiKhoanRepository taiKhoanRepository;
    private final SachRepository sachRepository;
    private final DocGiaRepository docGiaRepository;
    private final PhieuMuonRepository phieuMuonRepository;
    private final ChiTietPhieuMuonRepository chiTietPhieuMuonRepository;

    @Autowired
    public PhieuMuonController(
            TaiKhoanRepository taiKhoanRepository,
            SachRepository sachRepository,
            DocGiaRepository docGiaRepository,
            PhieuMuonRepository phieuMuonRepository,
            ChiTietPhieuMuonRepository chiTietPhieuMuonRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.sachRepository = sachRepository;
        this.docGiaRepository = docGiaRepository;
        this.phieuMuonRepository = phieuMuonRepository;
        this.chiTietPhieuMuonRepository = chiTietPhieuMuonRepository;
    }

    @GetMapping
    public String showPhieuMuonManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PhieuMuon> phieuMuonPage;
        if (keyword != null && !keyword.isEmpty()) {
            phieuMuonPage = phieuMuonRepository.searchByKeyword(keyword, pageable);
        } else {
            phieuMuonPage = phieuMuonRepository.findAll(pageable);
        }
        model.addAttribute("phieuMuonPage", phieuMuonPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeSection", "nhapmuon");
        return "admin/nhapmuon/nhapmuon";
    }

    @GetMapping("/them")
    public String showAddPhieuMuonForm(Model model) {
        PhieuMuon phieuMuon = new PhieuMuon();
        phieuMuon.setChiTietPhieuMuons(new ArrayList<>());
        model.addAttribute("phieuMuon", phieuMuon);
        model.addAttribute("docGias", docGiaRepository.findAll());
        model.addAttribute("taiKhoans", taiKhoanRepository.findByQuyenMaQuyen(2));
        model.addAttribute("sachs", sachRepository.findAll());
        model.addAttribute("activeSection", "nhapmuon");
        return "admin/nhapmuon/themphieumuon";
    }

    @PostMapping("/them")
    public String addPhieuMuon(
            @Valid @ModelAttribute("phieuMuon") PhieuMuon phieuMuon,
            BindingResult result,
            @RequestParam("sachIds") List<Integer> sachIds,
            @RequestParam("ghiChus") List<String> ghiChus,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("docGias", docGiaRepository.findAll());
            model.addAttribute("taiKhoans", taiKhoanRepository.findByQuyenMaQuyen(2));
            model.addAttribute("sachs", sachRepository.findAll());
            return "admin/nhapmuon/themphieumuon";
        }
        if (phieuMuon.getDocGia() == null || phieuMuon.getDocGia().getMaDocGia() == null) {
            result.rejectValue("docGia", "error.docGia", "Vui lòng chọn độc giả!");
            model.addAttribute("docGias", docGiaRepository.findAll());
            model.addAttribute("taiKhoans", taiKhoanRepository.findByQuyenMaQuyen(2));
            model.addAttribute("sachs", sachRepository.findAll());
            return "admin/nhapmuon/themphieumuon";
        }
        if (phieuMuon.getTaiKhoan() == null || phieuMuon.getTaiKhoan().getMaTaiKhoan() == null) {
            result.rejectValue("taiKhoan", "error.taiKhoan", "Vui lòng chọn nhân viên!");
            model.addAttribute("docGias", docGiaRepository.findAll());
            model.addAttribute("taiKhoans", taiKhoanRepository.findByQuyenMaQuyen(2));
            model.addAttribute("sachs", sachRepository.findAll());
            return "admin/nhapmuon/themphieumuon";
        }
        if (sachIds == null || sachIds.isEmpty()) {
            result.rejectValue("chiTietPhieuMuons", "error.chiTietPhieuMuons", "Vui lòng chọn ít nhất một cuốn sách!");
            model.addAttribute("docGias", docGiaRepository.findAll());
            model.addAttribute("taiKhoans", taiKhoanRepository.findByQuyenMaQuyen(2));
            model.addAttribute("sachs", sachRepository.findAll());
            return "admin/nhapmuon/themphieumuon";
        }
        phieuMuon.setNgayMuon(LocalDate.now());
        if (phieuMuon.getNgayHetHan() == null) {
            phieuMuon.setNgayHetHan(LocalDate.now().plusDays(7));
        }
        phieuMuon.setTrangThai("Chưa trả");
        phieuMuon.setTienPhat(BigDecimal.ZERO);
        // Add book details
        List<ChiTietPhieuMuon> chiTietPhieuMuons = new ArrayList<>();
        for (int i = 0; i < sachIds.size(); i++) {
    final int index = i; // Thêm dòng này để khắc phục lỗi
    Sach sach = sachRepository.findById(sachIds.get(index))
            .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại: " + sachIds.get(index)));
    
    ChiTietPhieuMuon chiTiet = new ChiTietPhieuMuon();
    chiTiet.setPhieuMuon(phieuMuon);
    chiTiet.setSach(sach);
    chiTiet.setGhiChu(index < ghiChus.size() && !ghiChus.get(index).isEmpty() ? ghiChus.get(index) : null);
    chiTietPhieuMuons.add(chiTiet);
}
        phieuMuon.setChiTietPhieuMuons(chiTietPhieuMuons);
        try {
            phieuMuonRepository.save(phieuMuon);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm phiếu mượn thành công!");
            return "redirect:/admin/nhapmuon";
        } catch (Exception e) {
            model.addAttribute("docGias", docGiaRepository.findAll());
            model.addAttribute("taiKhoans", taiKhoanRepository.findByQuyenMaQuyen(2));
            model.addAttribute("sachs", sachRepository.findAll());
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm phiếu mượn: " + e.getMessage());
            return "admin/nhapmuon/themphieumuon";
        }
    }

    @GetMapping("/sua")
    public String showEditPhieuMuonForm(@RequestParam("id") Integer id, Model model) {
        PhieuMuon phieuMuon = phieuMuonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phiếu mượn không tồn tại: " + id));
        model.addAttribute("phieuMuon", phieuMuon);
        model.addAttribute("docGias", docGiaRepository.findAll());
        model.addAttribute("taiKhoans", taiKhoanRepository.findByQuyenMaQuyen(2));
        model.addAttribute("sachs", sachRepository.findAll());
        model.addAttribute("activeSection", "nhapmuon");
        return "admin/nhapmuon/themphieumuon";
    }

    @GetMapping("/xoa")
    public String deletePhieuMuon(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        phieuMuonRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa phiếu mượn thành công!");
        return "redirect:/admin/nhapmuon";
    }
}