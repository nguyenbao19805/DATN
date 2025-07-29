package com.poly.thuviendatn.AdminController;

import com.poly.thuviendatn.Dto.DanhMucDTO;
import com.poly.thuviendatn.Model.DanhMuc;
import com.poly.thuviendatn.Model.KeSach;
import com.poly.thuviendatn.Model.LoaiSach;
import com.poly.thuviendatn.Model.Sach;
import com.poly.thuviendatn.Model.TrangSach;
import com.poly.thuviendatn.Model.TrangSachHinhAnh;
import com.poly.thuviendatn.Model.ViTriKeSach;
import com.poly.thuviendatn.Repository.*;
import com.poly.thuviendatn.Service.SachDienTuService;
import com.poly.thuviendatn.Service.SachService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// Định nghĩa static nested class KeSachDTO
class KeSachDTO {
    private Long maKeSach;
    private String tenKeSach;
    private Integer remainingCapacity;

    public KeSachDTO(Long maKeSach, String tenKeSach, Integer remainingCapacity) {
        this.maKeSach = maKeSach;
        this.tenKeSach = tenKeSach;
        this.remainingCapacity = remainingCapacity;
    }

    public Long getMaKeSach() { return maKeSach; }
    public String getTenKeSach() { return tenKeSach; }
    public Integer getRemainingCapacity() { return remainingCapacity; }

    public void setMaKeSach(Long maKeSach) { this.maKeSach = maKeSach; }
    public void setTenKeSach(String tenKeSach) { this.tenKeSach = tenKeSach; }
    public void setRemainingCapacity(Integer remainingCapacity) { this.remainingCapacity = remainingCapacity; }
}

@Controller
@RequestMapping("/admin/quanlysach")
public class QuanLySachController {

    private static final Logger logger = LoggerFactory.getLogger(QuanLySachController.class);

    @Autowired
    private TrangSachRepository trangSachRepository;

    @Autowired
    private TrangSachHinhAnhRepository trangSachHinhAnhRepository;

    @Autowired
    private LoaiSachRepository loaiSachRepository;

    @Autowired
    private TacGiaRepository tacGiaRepository;

    @Autowired
    private NhaXuatBanRepository nhaXuatBanRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private SachService sachService;

    @Autowired
    private SachRepository sachRepository;

    @Autowired
    private SachDienTuService sachDienTuService;

    @Autowired
    private KeSachRepository keSachRepository;

    @Autowired
    private ViTriKeSachRepository viTriKeSachRepository;

    // API trả danh mục theo mã loại sách
    @GetMapping("/danhmuc-by-loaisach")
    @ResponseBody
    public List<DanhMucDTO> getDanhMucsByLoaiSach(@RequestParam("maLoaiSach") Integer maLoaiSach) {
        return danhMucRepository.findByLoaiSach_MaLoaiSach(maLoaiSach)
                .stream()
                .map(dm -> new DanhMucDTO(dm.getMaDanhMuc(), dm.getTenDanhMuc()))
                .toList();
    }

    // GET: Hiển thị danh sách sách, loại sách, hoặc danh mục
    @GetMapping
    public String showQuanLySach(
            @RequestParam(defaultValue = "0") int sachPage,
            @RequestParam(defaultValue = "5") int sachSize,
            @RequestParam(required = false) String sachKeyword,
            @RequestParam(defaultValue = "true") boolean hinhThuc,
            @RequestParam(defaultValue = "sach") String activeTab,
            @RequestParam(defaultValue = "0") int loaiSachPage,
            @RequestParam(defaultValue = "10") int loaiSachSize,
            @RequestParam(required = false) String loaiSachKeyword,
            @RequestParam(defaultValue = "0") int danhMucPage,
            @RequestParam(defaultValue = "10") int danhMucSize,
            @RequestParam(required = false) String danhMucKeyword,
            Model model) {
        try {
            // Xử lý tab Sách
            Page<Sach> sachPageObj;
            Pageable sachPageable = PageRequest.of(sachPage, sachSize);
            if (sachKeyword != null && !sachKeyword.isEmpty()) {
                sachPageObj = sachRepository.findByTenSachContainingIgnoreCase(sachKeyword, sachPageable);
            } else {
                sachPageObj = sachRepository.findAll(sachPageable);
            }
            List<Sach> sachListWithStatus = sachPageObj.getContent().stream().map(sach -> {
                Integer booksOnShelf = viTriKeSachRepository.sumSoLuongBySach(sach.getMaSach());
                sach.setOnShelf(booksOnShelf != null && booksOnShelf > 0);
                sach.setCurrentBooks(booksOnShelf != null ? booksOnShelf : 0);
                return sach;
            }).toList();

            // Xử lý tab Loại sách
            Pageable loaiSachPageable = PageRequest.of(loaiSachPage, loaiSachSize);
            Page<LoaiSach> loaiSachPageResult;
            if (loaiSachKeyword != null && !loaiSachKeyword.isEmpty()) {
                loaiSachPageResult = loaiSachRepository.findByTenLoaiSachContainingIgnoreCase(loaiSachKeyword, loaiSachPageable);
            } else {
                loaiSachPageResult = loaiSachRepository.findAll(loaiSachPageable);
            }

            // Xử lý tab Danh mục
            Pageable danhMucPageable = PageRequest.of(danhMucPage, danhMucSize);
            Page<DanhMuc> danhMucPageResult;
            if (danhMucKeyword != null && !danhMucKeyword.isEmpty()) {
                danhMucPageResult = danhMucRepository.findByTenDanhMucContainingIgnoreCase(danhMucKeyword, danhMucPageable);
            } else {
                danhMucPageResult = danhMucRepository.findAll(danhMucPageable);
            }

            // Lấy danh sách kệ sách
            List<KeSach> keSachList = keSachRepository.findAll().stream().map(keSach -> {
                Integer currentBooks = viTriKeSachRepository.sumSoLuongByKeSach(keSach.getMaKeSach());
                keSach.setCurrentBooks(currentBooks != null ? currentBooks : 0);
                return keSach;
            }).toList();

            model.addAttribute("sachPage", sachPageObj);
            model.addAttribute("currentPage", sachPage);
            model.addAttribute("pageSize", sachSize);
            model.addAttribute("sachKeyword", sachKeyword);
            model.addAttribute("hinhThuc", hinhThuc);
            model.addAttribute("activeTab", activeTab);
            model.addAttribute("loaiSachPage", loaiSachPageResult);
            model.addAttribute("loaiSachKeyword", loaiSachKeyword);
            model.addAttribute("danhMucPage", danhMucPageResult);
            model.addAttribute("danhMucKeyword", danhMucKeyword);
            model.addAttribute("keSachList", keSachList);
            model.addAttribute("activeSection", "quanlysach");
            return "Admin/Quanlysach/quanlysach";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tải dữ liệu: " + e.getMessage());
            model.addAttribute("sachPage", Page.empty());
            model.addAttribute("currentPage", sachPage);
            model.addAttribute("pageSize", sachSize);
            model.addAttribute("sachKeyword", sachKeyword);
            model.addAttribute("hinhThuc", hinhThuc);
            model.addAttribute("activeTab", activeTab);
            model.addAttribute("loaiSachPage", Page.empty());
            model.addAttribute("loaiSachKeyword", loaiSachKeyword);
            model.addAttribute("danhMucPage", Page.empty());
            model.addAttribute("danhMucKeyword", danhMucKeyword);
            List<KeSach> keSachList = keSachRepository.findAll().stream().map(keSach -> {
                Integer currentBooks = viTriKeSachRepository.sumSoLuongByKeSach(keSach.getMaKeSach());
                keSach.setCurrentBooks(currentBooks != null ? currentBooks : 0);
                return keSach;
            }).toList();
            model.addAttribute("keSachList", keSachList);
            model.addAttribute("activeSection", "quanlysach");
            return "Admin/Quanlysach/quanlysach";
        }
    }

    // API trả sức chứa còn lại của kệ sách
    @GetMapping("/shelf-capacity")
    @ResponseBody
    public KeSachDTO getShelfCapacity(@RequestParam("maKeSach") Long maKeSach) {
        KeSach keSach = keSachRepository.findById(maKeSach)
                .orElseThrow(() -> new IllegalArgumentException("Kệ sách không tồn tại: " + maKeSach));
        Integer currentBooks = viTriKeSachRepository.sumSoLuongByKeSach(maKeSach);
        currentBooks = currentBooks != null ? currentBooks : 0;
        int remainingCapacity = keSach.getSucChua() - currentBooks;
        return new KeSachDTO(keSach.getMaKeSach(), keSach.getTenKeSach(), remainingCapacity);
    }

    // POST: Thêm sách lên nhiều kệ
    @PostMapping("/add-to-shelf")
    public String addToShelf(
            @RequestParam(value = "maSach", required = false) Integer maSach,
            @RequestParam(value = "maKeSach", required = false) List<Long> maKeSachList,
            @RequestParam(value = "viTriChiTiet", required = false) List<String> viTriChiTietList,
            @RequestParam(value = "soLuong", required = false) List<Integer> soLuongList,
            @RequestParam(value = "ghiChu", required = false) String ghiChu,
            @RequestParam(defaultValue = "sach") String activeTab,
            @RequestParam(defaultValue = "true") boolean hinhThuc,
            RedirectAttributes redirectAttributes) {
        if (maSach == null || maSach <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tham số maSach là bắt buộc và phải là số nguyên dương hợp lệ!");
            return "redirect:/admin/quanlysach?activeTab=" + activeTab + "&hinhThuc=" + hinhThuc;
        }

        if (maKeSachList == null || maKeSachList.isEmpty() || viTriChiTietList == null || soLuongList == null ||
                maKeSachList.size() != viTriChiTietList.size() || maKeSachList.size() != soLuongList.size()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Dữ liệu kệ sách, vị trí chi tiết hoặc số lượng không hợp lệ!");
            return "redirect:/admin/quanlysach?activeTab=" + activeTab + "&hinhThuc=" + hinhThuc;
        }

        try {
            Sach sach = sachRepository.findById(maSach)
                    .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại: " + maSach));
            if (!sach.isHinhThuc()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Chỉ sách giấy có thể được thêm lên kệ!");
                return "redirect:/admin/quanlysach?activeTab=" + activeTab + "&hinhThuc=" + hinhThuc;
            }

            Integer currentBooksOnShelf = viTriKeSachRepository.sumSoLuongBySach(maSach);
            currentBooksOnShelf = currentBooksOnShelf != null ? currentBooksOnShelf : 0;

            int totalBooksToAdd = soLuongList.stream().mapToInt(Integer::intValue).sum();

            if (currentBooksOnShelf + totalBooksToAdd > sach.getSoLuong()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                    "Tổng số lượng sách thêm lên kệ (" + (currentBooksOnShelf + totalBooksToAdd) +
                    ") vượt quá số lượng sách có sẵn (" + sach.getSoLuong() + ")!");
                return "redirect:/admin/quanlysach?activeTab=" + activeTab + "&hinhThuc=" + hinhThuc;
            }

            for (int i = 0; i < maKeSachList.size(); i++) {
                Long maKeSach = maKeSachList.get(i);
                String viTriChiTiet = viTriChiTietList.get(i);
                Integer soLuong = soLuongList.get(i);

                KeSach keSach = keSachRepository.findById(maKeSach)
                        .orElseThrow(() -> new IllegalArgumentException("Kệ sách không tồn tại: " + maKeSach));

                Integer currentBooksOnKe = viTriKeSachRepository.sumSoLuongByKeSach(maKeSach);
                currentBooksOnKe = currentBooksOnKe != null ? currentBooksOnKe : 0;
                if (currentBooksOnKe + soLuong > keSach.getSucChua()) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                        "Kệ sách " + keSach.getTenKeSach() + " đã đầy! Không thể thêm " + soLuong + " sách.");
                    continue;
                }

                Optional<ViTriKeSach> existingViTri = viTriKeSachRepository.findBySachMaSachAndKeSachMaKeSach(maSach, maKeSach);
                if (existingViTri.isPresent()) {
                    ViTriKeSach viTriKeSach = existingViTri.get();
                    viTriKeSach.setSoLuong(viTriKeSach.getSoLuong() + soLuong);
                    viTriKeSach.setViTriChiTiet(viTriChiTiet);
                    viTriKeSach.setGhiChu(ghiChu);
                    viTriKeSachRepository.save(viTriKeSach);
                } else {
                    ViTriKeSach viTriKeSach = new ViTriKeSach();
                    viTriKeSach.setSach(sach);
                    viTriKeSach.setKeSach(keSach);
                    viTriKeSach.setViTriChiTiet(viTriChiTiet);
                    viTriKeSach.setSoLuong(soLuong);
                    viTriKeSach.setGhiChu(ghiChu);
                    viTriKeSachRepository.save(viTriKeSach);
                }
            }

            Integer updatedBooksOnShelf = viTriKeSachRepository.sumSoLuongBySach(maSach);
            updatedBooksOnShelf = updatedBooksOnShelf != null ? updatedBooksOnShelf : 0;
            sach.setOnShelf(updatedBooksOnShelf > 0);
            sach.setCurrentBooks(updatedBooksOnShelf);
            sachRepository.save(sach);

            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sách lên kệ thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm sách lên kệ: " + e.getMessage());
        }
        return "redirect:/admin/quanlysach?activeTab=" + activeTab + "&hinhThuc=" + hinhThuc;
    }

    @GetMapping("/themsachgiay")
    public String showAddSachgiayForm(Model model) {
        model.addAttribute("sach", new Sach());
        model.addAttribute("loaiSachs", loaiSachRepository.findAll());
        model.addAttribute("tacGias", tacGiaRepository.findAll());
        model.addAttribute("nhaXuatBans", nhaXuatBanRepository.findAll());
        model.addAttribute("danhMucs", danhMucRepository.findByLoaiSach_MaLoaiSach(10));
        model.addAttribute("selectedLoaiSach", 10);
        model.addAttribute("activeSection", "quanlysach");
        model.addAttribute("activeTab", "sach");
        model.addAttribute("hinhThuc", true);
        return "Admin/Quanlysach/themsachgiay";
    }

    @GetMapping("/suasachgiay/{id}")
    public String showEditSachGiayForm(@PathVariable("id") Integer id, Model model) {
        Sach sach = sachRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mã sách không hợp lệ: " + id));
        model.addAttribute("sach", sach);
        model.addAttribute("loaiSachs", loaiSachRepository.findAll());
        model.addAttribute("tacGias", tacGiaRepository.findAll());
        model.addAttribute("nhaXuatBans", nhaXuatBanRepository.findAll());
        Integer maLoaiSach = sach.getDanhMuc() != null && sach.getDanhMuc().getLoaiSach() != null
                ? sach.getDanhMuc().getLoaiSach().getMaLoaiSach()
                : 10;
        List<DanhMuc> danhMucs = danhMucRepository.findByLoaiSach_MaLoaiSach(maLoaiSach);
        if (sach.getDanhMuc() != null && !danhMucs.contains(sach.getDanhMuc())) {
            danhMucs.add(sach.getDanhMuc());
        }
        model.addAttribute("danhMucs", danhMucs);
        model.addAttribute("selectedLoaiSach", maLoaiSach);
        model.addAttribute("currentCoverImage", sach.getHinhAnh());
        model.addAttribute("activeSection", "quanlysach");
        model.addAttribute("activeTab", "sach");
        model.addAttribute("hinhThuc", true);
        return "Admin/Quanlysach/themsachgiay";
    }

    @PostMapping("/themsachgiay")
    public String themOrSuaSachGiay(
            @Valid @ModelAttribute("sach") Sach sach,
            BindingResult result,
            @RequestParam("coverImage") MultipartFile coverImage,
            @RequestParam(value = "trangSachImages", required = false) List<MultipartFile> trangSachImages,
            @RequestParam(value = "danhMuc.maDanhMuc", required = false) Integer maDanhMuc,
            Model model,
            RedirectAttributes redirectAttributes) {
        logger.info("Processing themOrSuaSachGiay with sach: {}", sach);

        // Khởi tạo danhMuc nếu maDanhMuc được cung cấp
        if (maDanhMuc != null) {
            DanhMuc danhMuc = danhMucRepository.findById(maDanhMuc)
                    .orElseThrow(() -> new IllegalArgumentException("Danh mục không hợp lệ: " + maDanhMuc));
            sach.setDanhMuc(danhMuc);
        }

        if (result.hasErrors() || sach.getDanhMuc() == null) {
            logger.warn("Validation errors or danhMuc is null: {}", result.getAllErrors());
            // Thêm các thuộc tính để hiển thị lại form
            model.addAttribute("sach", sach);
            model.addAttribute("loaiSachs", loaiSachRepository.findAll());
            model.addAttribute("tacGias", tacGiaRepository.findAll());
            model.addAttribute("nhaXuatBans", nhaXuatBanRepository.findAll());
            Integer maLoaiSach = (sach.getDanhMuc() != null && sach.getDanhMuc().getLoaiSach() != null)
                    ? sach.getDanhMuc().getLoaiSach().getMaLoaiSach()
                    : 10;
            List<DanhMuc> danhMucs = danhMucRepository.findByLoaiSach_MaLoaiSach(maLoaiSach);
            if (sach.getDanhMuc() != null && !danhMucs.contains(sach.getDanhMuc())) {
                danhMucs.add(sach.getDanhMuc());
            }
            model.addAttribute("danhMucs", danhMucs);
            model.addAttribute("selectedLoaiSach", maLoaiSach);
            model.addAttribute("currentCoverImage", sach.getHinhAnh());
            model.addAttribute("activeSection", "quanlysach");
            model.addAttribute("activeTab", "sach");
            model.addAttribute("hinhThuc", true);
            model.addAttribute("errorMessage", sach.getDanhMuc() == null ? "Danh mục không được để trống." : "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại các trường bắt buộc.");
            return "Admin/Quanlysach/themsachgiay";
        }

        try {
            sach.setHinhThuc(true);
            sachService.saveSachWithPages(sach, coverImage, trangSachImages);
            redirectAttributes.addFlashAttribute("successMessage",
                sach.getMaSach() == null ? "Thêm sách giấy thành công!" : "Cập nhật sách giấy thành công!");
            return "redirect:/admin/quanlysach?activeTab=sach&hinhThuc=true";
        } catch (IOException e) {
            logger.error("IOException when saving book: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi lưu ảnh: " + e.getMessage());
            return "redirect:/admin/quanlysach?activeTab=sach&hinhThuc=true";
        } catch (IllegalArgumentException e) {
            logger.error("IllegalArgumentException when saving book: ", e);
            model.addAttribute("sach", sach);
            model.addAttribute("loaiSachs", loaiSachRepository.findAll());
            model.addAttribute("tacGias", tacGiaRepository.findAll());
            model.addAttribute("nhaXuatBans", nhaXuatBanRepository.findAll());
            Integer maLoaiSach = (sach.getDanhMuc() != null && sach.getDanhMuc().getLoaiSach() != null)
                    ? sach.getDanhMuc().getLoaiSach().getMaLoaiSach()
                    : 10;
            List<DanhMuc> danhMucs = danhMucRepository.findByLoaiSach_MaLoaiSach(maLoaiSach);
            if (sach.getDanhMuc() != null && !danhMucs.contains(sach.getDanhMuc())) {
                danhMucs.add(sach.getDanhMuc());
            }
            model.addAttribute("danhMucs", danhMucs);
            model.addAttribute("selectedLoaiSach", maLoaiSach);
            model.addAttribute("currentCoverImage", sach.getHinhAnh());
            model.addAttribute("activeSection", "quanlysach");
            model.addAttribute("activeTab", "sach");
            model.addAttribute("hinhThuc", true);
            model.addAttribute("errorMessage", e.getMessage());
            return "Admin/Quanlysach/themsachgiay";
        }
    }

    @GetMapping("/xoa-sach")
    public String xoaSach(@RequestParam("id") Integer id,
                         @RequestParam(defaultValue = "sach") String activeTab,
                         @RequestParam(defaultValue = "true") boolean hinhThuc,
                         RedirectAttributes redirectAttributes) {
        try {
            sachRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa sách: " + e.getMessage());
        }
        return "redirect:/admin/quanlysach?activeTab=" + activeTab + "&hinhThuc=" + hinhThuc;
    }

    @GetMapping("/themsachdientu")
    public String showAddSachdientuForm(Model model) {
        model.addAttribute("sach", new Sach());
        model.addAttribute("loaiSachs", loaiSachRepository.findAll());
        model.addAttribute("tacGias", tacGiaRepository.findAll());
        model.addAttribute("nhaXuatBans", nhaXuatBanRepository.findAll());
        model.addAttribute("danhMucs", danhMucRepository.findByLoaiSach_MaLoaiSach(11));
        model.addAttribute("selectedLoaiSach", 11);
        model.addAttribute("activeSection", "quanlysach");
        model.addAttribute("activeTab", "sach");
        model.addAttribute("hinhThuc", false);
        return "Admin/Quanlysach/themsachdientu";
    }

    @GetMapping("/suasachdientu/{id}")
    public String showEditSachdientuForm(@PathVariable("id") Integer id, Model model) {
        Sach sach = sachRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mã sách không hợp lệ: " + id));
        model.addAttribute("sach", sach);
        model.addAttribute("loaiSachs", loaiSachRepository.findAll());
        model.addAttribute("tacGias", tacGiaRepository.findAll());
        model.addAttribute("nhaXuatBans", nhaXuatBanRepository.findAll());
        Integer maLoaiSach = 11;
        if (sach.getDanhMuc() != null && sach.getDanhMuc().getLoaiSach() != null) {
            maLoaiSach = sach.getDanhMuc().getLoaiSach().getMaLoaiSach();
        }
        List<DanhMuc> danhMucs = danhMucRepository.findByLoaiSach_MaLoaiSach(maLoaiSach);
        if (sach.getDanhMuc() != null && !danhMucs.contains(sach.getDanhMuc())) {
            danhMucs.add(sach.getDanhMuc());
        }
        Optional<TrangSach> trangSachOpt = trangSachRepository.findBySachMaSach(sach.getMaSach());
        if (trangSachOpt.isPresent()) {
            List<TrangSachHinhAnh> hinhAnhs = trangSachOpt.get().getHinhAnhs()
                    .stream()
                    .sorted(Comparator.comparingInt(TrangSachHinhAnh::getThuTu))
                    .toList();
            model.addAttribute("hinhAnhs", hinhAnhs);
        }
        model.addAttribute("danhMucs", danhMucs);
        model.addAttribute("selectedLoaiSach", maLoaiSach);
        model.addAttribute("activeSection", "quanlysach");
        model.addAttribute("activeTab", "sach");
        model.addAttribute("hinhThuc", false);
        return "Admin/Quanlysach/themsachdientu";
    }

    @PostMapping("/themsachdientu")
    public String themOrCapNhatSachDienTu(
            @Valid @ModelAttribute("sach") Sach sach,
            BindingResult result,
            @RequestParam("coverImage") MultipartFile coverImage,
            @RequestParam(value = "trangSachImages", required = false) List<MultipartFile> trangSachImages,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
            return "redirect:/admin/quanlysach?activeTab=sach&hinhThuc=false";
        }
        try {
            sach.setHinhThuc(false);
            sach.setSoLuong(1);
            if (sach.getMaSach() == null) {
                sachDienTuService.saveSachDienTu(sach, coverImage, trangSachImages);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm sách điện tử thành công!");
            } else {
                sachDienTuService.updateSachDienTu(sach, coverImage, trangSachImages);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sách điện tử thành công!");
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi lưu sách điện tử: " + e.getMessage());
        }
        return "redirect:/admin/quanlysach?activeTab=sach&hinhThuc=false";
    }
}