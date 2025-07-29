package com.poly.thuviendatn.AdminController;

import com.poly.thuviendatn.Model.*;
import com.poly.thuviendatn.Repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/admin/nhapmuon")
public class PhieuMuonController {
@Autowired
private TaiKhoanRepository taiKhoanRepository;
    private final SachRepository sachRepository;
    private final DocGiaRepository docGiaRepository;
    private final PhieuMuonRepository phieuMuonRepository;
    private final ChiTietPhieuMuonRepository chiTietPhieuMuonRepository;

    @Autowired
    private TaiKhoanTheRepository taiKhoanTheRepository;
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
        RedirectAttributes redirectAttributes,
        @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {

    if (result.hasErrors()) {
        model.addAttribute("docGias", docGiaRepository.findAll());
        model.addAttribute("taiKhoans", taiKhoanRepository.findAll());
        model.addAttribute("sachs", sachRepository.findAll());
        return "admin/nhapmuon/themphieumuon";
    }

    // Lấy tài khoản nhân viên đang đăng nhập
    Integer maTaiKhoan = Integer.parseInt(currentUser.getUsername());
    TaiKhoan taiKhoan = taiKhoanRepository.findById(maTaiKhoan)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản nhân viên: " + maTaiKhoan));
    phieuMuon.setTaiKhoan(taiKhoan);

    // Kiểm tra độc giả
    if (phieuMuon.getDocGia() == null || phieuMuon.getDocGia().getMaDocGia() == null) {
        result.rejectValue("docGia", "error.docGia", "Vui lòng chọn độc giả!");
        model.addAttribute("docGias", docGiaRepository.findAll());
        model.addAttribute("taiKhoans", taiKhoanRepository.findAll());
        model.addAttribute("sachs", sachRepository.findAll());
        return "admin/nhapmuon/themphieumuon";
    }

    Integer maDocGia = phieuMuon.getDocGia().getMaDocGia();
    TaiKhoanThe taiKhoanThe = taiKhoanTheRepository.findById(maDocGia)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản thẻ của mã độc giả: " + maDocGia));

    // Thiết lập mặc định
    if (phieuMuon.getNgayMuon() == null)
        phieuMuon.setNgayMuon(LocalDate.now());

    phieuMuon.setTrangThai("Chưa trả");
    phieuMuon.setTienPhat(null);
    int soLuong = sachIds.size();
    phieuMuon.setSoLuong(soLuong);

    // Tính số ngày mượn và thanh toán
    LocalDate batDau = phieuMuon.getNgayMuon();
    LocalDate ketThuc = phieuMuon.getNgayTra() != null
            ? phieuMuon.getNgayTra()
            : (phieuMuon.getNgayHetHan() != null ? phieuMuon.getNgayHetHan() : batDau.plusDays(7));
    if (phieuMuon.getNgayHetHan() == null)
        phieuMuon.setNgayHetHan(ketThuc);

    long soNgay = java.time.temporal.ChronoUnit.DAYS.between(batDau, ketThuc);
    double thanhToan = (soLuong + 3) * soNgay * 1000;
    phieuMuon.setThanhToan(thanhToan);

    // Trừ số dư
    double soDu = taiKhoanThe.getSoDu() != null ? taiKhoanThe.getSoDu() : 0.0;
    if (soDu < thanhToan) {
        result.rejectValue("taiKhoan", "error.soDu", "Tài khoản không đủ số dư để mượn sách!");
        model.addAttribute("docGias", docGiaRepository.findAll());
        model.addAttribute("taiKhoans", taiKhoanRepository.findAll());
        model.addAttribute("sachs", sachRepository.findAll());
        return "admin/nhapmuon/themphieumuon";
    }

    taiKhoanThe.setSoDu(soDu - thanhToan);
    taiKhoanTheRepository.save(taiKhoanThe);

    // Tạo chi tiết phiếu mượn và cập nhật sách
    List<ChiTietPhieuMuon> chiTietList = new ArrayList<>();
    for (int i = 0; i < sachIds.size(); i++) {
        Integer maSach = sachIds.get(i);
        Sach sach = sachRepository.findById(maSach)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách: " + maSach));

        if (sach.getSoLuong() == null || sach.getSoLuong() <= 0) {
            result.rejectValue("chiTietPhieuMuons", "error.sach", "Sách mã " + maSach + " đã hết!");
            model.addAttribute("docGias", docGiaRepository.findAll());
            model.addAttribute("taiKhoans", taiKhoanRepository.findAll());
            model.addAttribute("sachs", sachRepository.findAll());
            return "admin/nhapmuon/themphieumuon";
        }

        sach.setSoLuong(sach.getSoLuong() - 1);
        sachRepository.save(sach);

        ChiTietPhieuMuon ct = new ChiTietPhieuMuon();
        ct.setPhieuMuon(phieuMuon);
        ct.setSach(sach);
        ct.setGhiChu(i < ghiChus.size() ? ghiChus.get(i) : null);
        chiTietList.add(ct);
    }

    phieuMuon.setChiTietPhieuMuons(chiTietList);

    try {
        phieuMuonRepository.save(phieuMuon); // Cascade = true sẽ lưu luôn ctpm
        redirectAttributes.addFlashAttribute("successMessage", "Thêm phiếu mượn thành công!");
        return "redirect:/admin/nhapmuon";
    } catch (Exception e) {
        model.addAttribute("errorMessage", "Lỗi khi lưu phiếu mượn: " + e.getMessage());
        model.addAttribute("docGias", docGiaRepository.findAll());
        model.addAttribute("taiKhoans", taiKhoanRepository.findAll());
        model.addAttribute("sachs", sachRepository.findAll());
        return "admin/nhapmuon/themphieumuon";
    }
}

@GetMapping("/sua")
public String showUpdateForm(@RequestParam("id") Integer id, Model model) {
    PhieuMuon phieuMuon = phieuMuonRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn"));

    model.addAttribute("phieuMuon", phieuMuon);
    model.addAttribute("docGias", docGiaRepository.findAll());
    model.addAttribute("sachs", sachRepository.findAll());

    return "admin/nhapmuon/suaphieumuon"; // Đây là trang form chi tiết bạn cần tạo
}


@PostMapping("/sua")
public String updatePhieuMuon(
        @Valid @ModelAttribute("phieuMuon") PhieuMuon phieuMuon,
        BindingResult result,
        @RequestParam("sachIds") List<Integer> sachIds,
        @RequestParam("ghiChus") List<String> ghiChus,
        Model model,
        RedirectAttributes redirectAttributes,
        @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {

    if (result.hasErrors()) {
        model.addAttribute("docGias", docGiaRepository.findAll());
        model.addAttribute("taiKhoans", taiKhoanRepository.findByQuyenMaQuyen(2));
        model.addAttribute("sachs", sachRepository.findAll());
        return "admin/nhapmuon/suaphieumuon"; // hoặc "admin/nhapmuon/suaphieumuon" nếu bạn tách form
    }

    // Tìm phiếu mượn cũ
    PhieuMuon existingPhieuMuon = phieuMuonRepository.findById(phieuMuon.getMaPhieu())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn để sửa."));

    // Cập nhật thông tin cơ bản
    existingPhieuMuon.setNgayMuon(phieuMuon.getNgayMuon());
    existingPhieuMuon.setNgayHetHan(phieuMuon.getNgayHetHan());
    existingPhieuMuon.setNgayTra(phieuMuon.getNgayTra());
    existingPhieuMuon.setTrangThai(phieuMuon.getTrangThai());
    existingPhieuMuon.setDocGia(phieuMuon.getDocGia());

    // Gắn tài khoản người sửa
    Integer maTaiKhoan = Integer.parseInt(currentUser.getUsername());
    TaiKhoan taiKhoan = taiKhoanRepository.findById(maTaiKhoan)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản nhân viên: " + maTaiKhoan));
    existingPhieuMuon.setTaiKhoan(taiKhoan);

    // Xóa chi tiết cũ trước khi thêm mới
    chiTietPhieuMuonRepository.deleteByPhieuMuon(existingPhieuMuon);

    // Thêm lại chi tiết phiếu mượn
    List<ChiTietPhieuMuon> newChiTietList = new ArrayList<>();
    for (int i = 0; i < sachIds.size(); i++) {
        Integer maSach = sachIds.get(i);
        Sach sach = sachRepository.findById(maSach)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách mã: " + maSach));

        ChiTietPhieuMuon ct = new ChiTietPhieuMuon();
        ct.setPhieuMuon(existingPhieuMuon);
        ct.setSach(sach);
        ct.setGhiChu(i < ghiChus.size() ? ghiChus.get(i) : null);
        newChiTietList.add(ct);
    }

    existingPhieuMuon.setChiTietPhieuMuons(newChiTietList);
    existingPhieuMuon.setSoLuong(newChiTietList.size());

    // Tính lại thanh toán (nếu cần)
    LocalDate batDau = existingPhieuMuon.getNgayMuon();
    LocalDate ketThuc = existingPhieuMuon.getNgayTra() != null
            ? existingPhieuMuon.getNgayTra()
            : (existingPhieuMuon.getNgayHetHan() != null ? existingPhieuMuon.getNgayHetHan() : batDau.plusDays(7));

    if (existingPhieuMuon.getNgayHetHan() == null)
        existingPhieuMuon.setNgayHetHan(ketThuc);

    long soNgay = java.time.temporal.ChronoUnit.DAYS.between(batDau, ketThuc);
    double thanhToan = (newChiTietList.size() + 3) * soNgay * 1000;
    existingPhieuMuon.setThanhToan(thanhToan);

    try {
        phieuMuonRepository.save(existingPhieuMuon); // Cascade sẽ lưu ctpm
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật phiếu mượn thành công!");
        return "redirect:/admin/nhapmuon";
    } catch (Exception e) {
        model.addAttribute("errorMessage", "Lỗi khi cập nhật phiếu mượn: " + e.getMessage());
        model.addAttribute("docGias", docGiaRepository.findAll());
        model.addAttribute("taiKhoans", taiKhoanRepository.findByQuyenMaQuyen(2));
        model.addAttribute("sachs", sachRepository.findAll());
        return "admin/nhapmuon/suaphieumuon"; // hoặc form mới
    }
}


    @GetMapping("/xoa")
    public String deletePhieuMuon(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        phieuMuonRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa phiếu mượn thành công!");
        return "redirect:/admin/nhapmuon";
    }

   @GetMapping("/trasach")
public String hienThiTraSach(@RequestParam("maPhieu") Integer maPhieu, Model model) {
    Optional<PhieuMuon> optionalPhieu = phieuMuonRepository.findById(maPhieu);

    if (optionalPhieu.isEmpty()) {
        return "redirect:/admin/nhapmuon";
    }

    PhieuMuon phieu = optionalPhieu.get();
    model.addAttribute("phieu", phieu);

    List<ChiTietPhieuMuon> chiTietList = chiTietPhieuMuonRepository.findByPhieuMuon_MaPhieu(maPhieu);
    model.addAttribute("chiTietList", chiTietList);

    return "admin/nhapmuon/trasach";  // đảm bảo file này tồn tại ở: templates/admin/nhapmuon/trasach.html
}

    @PostMapping("/xacnhan-tra")
    public String xacNhanTraSach(@RequestParam("maPhieuMuon") Integer maPhieuMuon,
                                RedirectAttributes redirectAttributes) {

        Optional<PhieuMuon> optionalPhieuMuon = phieuMuonRepository.findById(maPhieuMuon);

        if (optionalPhieuMuon.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy phiếu mượn.");
            return "redirect:/admin/nhapmuon";
        }

        PhieuMuon phieuMuon = optionalPhieuMuon.get();

        LocalDate ngayTra = LocalDate.now();
        phieuMuon.setNgayTra(ngayTra);

        LocalDate hanTra = phieuMuon.getNgayHetHan();
        long soLuong = chiTietPhieuMuonRepository.countByPhieuMuon_MaPhieu(maPhieuMuon);

        if (ngayTra.isAfter(hanTra)) {
            phieuMuon.setTrangThai("Trễ hạn");

            long soNgayTre = ChronoUnit.DAYS.between(hanTra, ngayTra);
            double tienPhat = (soLuong + 2) * soNgayTre * 1000;
            phieuMuon.setTienPhat(tienPhat);

            // Lấy maDocGia từ phiếu mượn (giả định maDocGia == maTaiKhoan)
            DocGia docGia = phieuMuon.getDocGia();
            if (docGia == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy độc giả.");
                return "redirect:/admin/nhapmuon";
            }

            Integer maTaiKhoan = docGia.getMaDocGia(); // Do maDocGia == maTaiKhoan
            TaiKhoanThe taiKhoanThe = taiKhoanTheRepository.findById(maTaiKhoan).orElse(null);

            if (taiKhoanThe == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tài khoản thẻ không tồn tại.");
                return "redirect:/admin/nhapmuon";
            }

            double soDu = taiKhoanThe.getSoDu() != null ? taiKhoanThe.getSoDu() : 0.0;

            if (soDu < tienPhat) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tài khoản không đủ số dư để thanh toán tiền phạt!");
                return "redirect:/admin/nhapmuon";
            }

            // Trừ tiền
            taiKhoanThe.setSoDu(soDu - tienPhat);
            taiKhoanTheRepository.save(taiKhoanThe);

        } else {
            phieuMuon.setTrangThai("Đã trả");
            phieuMuon.setTienPhat(0.0);
        }

        // Cập nhật số lượng sách
        for (ChiTietPhieuMuon ct : phieuMuon.getChiTietPhieuMuons()) {
            Sach sach = ct.getSach();
            if (sach.getSoLuong() == null) {
                sach.setSoLuong(1);
            } else {
                sach.setSoLuong(sach.getSoLuong() + 1);
            }
            sachRepository.save(sach);
        }

        // Lưu phiếu mượn
        phieuMuonRepository.save(phieuMuon);
        redirectAttributes.addFlashAttribute("successMessage", "Xác nhận trả sách thành công.");

        return "redirect:/admin/nhapmuon";
    }








    

}