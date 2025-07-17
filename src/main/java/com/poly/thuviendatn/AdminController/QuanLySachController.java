package com.poly.thuviendatn.AdminController;
import java.util.Optional;

import com.poly.thuviendatn.Dto.DanhMucDTO;
import com.poly.thuviendatn.Model.DanhMuc;
import com.poly.thuviendatn.Model.Sach;
import com.poly.thuviendatn.Model.TrangSach;
import com.poly.thuviendatn.Model.TrangSachHinhAnh;
import com.poly.thuviendatn.Repository.*;
import com.poly.thuviendatn.Service.SachDienTuService;
import com.poly.thuviendatn.Service.SachService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin/quanlysach")
public class QuanLySachController {

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

    // API trả danh mục theo mã loại sách
    @GetMapping("/danhmuc-by-loaisach")
    @ResponseBody
    public List<DanhMucDTO> getDanhMucsByLoaiSach(@RequestParam("maLoaiSach") Integer maLoaiSach) {
        return danhMucRepository.findByLoaiSach_MaLoaiSach(maLoaiSach)
                .stream()
                .map(dm -> new DanhMucDTO(dm.getMaDanhMuc(), dm.getTenDanhMuc()))
                .toList();
    }

    // GET: form thêm sách giấy
    @GetMapping("/themsachgiay")
    public String showAddSachgiayForm(Model model) {
        model.addAttribute("sach", new Sach());
        model.addAttribute("loaiSachs", loaiSachRepository.findAll());
        model.addAttribute("tacGias", tacGiaRepository.findAll());
        model.addAttribute("nhaXuatBans", nhaXuatBanRepository.findAll());
        model.addAttribute("danhMucs", danhMucRepository.findByLoaiSach_MaLoaiSach(10));
        model.addAttribute("selectedLoaiSach", 10); // sách giấy
        model.addAttribute("activeSection", "quanlysach");
        return "Admin/Quanlysach/themsachgiay";
    }

    // GET: form thêm sách điện tử

   @GetMapping("/suasachgiay/{id}")
    public String showEditSachGiayForm(@PathVariable("id") Integer id, Model model) {
        Sach sach = sachRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Mã sách không hợp lệ: " + id));

        model.addAttribute("sach", sach);
        model.addAttribute("loaiSachs", loaiSachRepository.findAll());
        model.addAttribute("tacGias", tacGiaRepository.findAll());
        model.addAttribute("nhaXuatBans", nhaXuatBanRepository.findAll());

        Integer maLoaiSach = 10; // mặc định
        if (sach.getDanhMuc() != null && sach.getDanhMuc().getLoaiSach() != null) {
            maLoaiSach = sach.getDanhMuc().getLoaiSach().getMaLoaiSach();
        }

        List<DanhMuc> danhMucs = danhMucRepository.findByLoaiSach_MaLoaiSach(maLoaiSach);

        // Nếu danh mục của sách không nằm trong danh sách đã lọc (do logic sai trước đó), thêm thủ công vào
        if (sach.getDanhMuc() != null && !danhMucs.contains(sach.getDanhMuc())) {
            danhMucs.add(sach.getDanhMuc());
        }

        model.addAttribute("danhMucs", danhMucs);
        model.addAttribute("selectedLoaiSach", maLoaiSach);
        model.addAttribute("activeSection", "quanlysach");

        return "Admin/Quanlysach/themsachgiay";
    }


    @PostMapping("/themsachgiay")
    public String themOrSuaSachGiay(
            @Valid @ModelAttribute("sach") Sach sach,
            BindingResult result,
            @RequestParam("coverImage") MultipartFile coverImage,
            @RequestParam(value = "trangSachImages", required = false) List<MultipartFile> trangSachImages,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "Admin/Quanlysach/themsachgiay";
        }

        try {
            sach.setHinhThuc(true); // true = sách giấy
            sachService.saveSachWithPages(sach, coverImage, trangSachImages);

            if (sach.getMaSach() == null) {
                redirectAttributes.addFlashAttribute("successMessage", "Thêm sách giấy thành công!");
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sách giấy thành công!");
            }

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi lưu ảnh: " + e.getMessage());
        }
        

        return "redirect:/admin/quanlysach";
    }

    @GetMapping("/xoa-sach")
    public String xoaSach(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
    try {
        sachRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa sách thành công!");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa sách: " + e.getMessage());
    }

    return "redirect:/admin/quanlysach";
}

    
@GetMapping("/themsachdientu")
public String showAddSachdientuForm(Model model) {
    model.addAttribute("sach", new Sach());
    model.addAttribute("loaiSachs", loaiSachRepository.findAll());
    model.addAttribute("tacGias", tacGiaRepository.findAll());
    model.addAttribute("nhaXuatBans", nhaXuatBanRepository.findAll());
    model.addAttribute("danhMucs", danhMucRepository.findByLoaiSach_MaLoaiSach(11)); // Sách điện tử
    model.addAttribute("selectedLoaiSach", 11);
    model.addAttribute("activeSection", "quanlysach");
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

    Integer maLoaiSach = 11; // mặc định sách điện tử
    if (sach.getDanhMuc() != null && sach.getDanhMuc().getLoaiSach() != null) {
        maLoaiSach = sach.getDanhMuc().getLoaiSach().getMaLoaiSach();
    }

    List<DanhMuc> danhMucs = danhMucRepository.findByLoaiSach_MaLoaiSach(maLoaiSach);
    if (sach.getDanhMuc() != null && !danhMucs.contains(sach.getDanhMuc())) {
        danhMucs.add(sach.getDanhMuc());
    }

    // ✅ Lấy ảnh trang sách nếu có
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
        return "Admin/Quanlysach/themsachdientu";
    }

    try {
        sach.setHinhThuc(false);
        sach.setSoLuong(1);

        if (sach.getMaSach() == null) {
            // ➕ Thêm mới sách điện tử
            sachDienTuService.saveSachDienTu(sach, coverImage, trangSachImages);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm sách điện tử thành công!");
        } else {
            // ✏️ Cập nhật sách điện tử (giữ ảnh cũ, giữ trang cũ, xử lý ảnh trang động)
            sachDienTuService.updateSachDienTu(sach, coverImage, trangSachImages);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sách điện tử thành công!");
        }

    } catch (IOException e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi lưu sách điện tử: " + e.getMessage());
    }

    return "redirect:/admin/quanlysach";
}

}
