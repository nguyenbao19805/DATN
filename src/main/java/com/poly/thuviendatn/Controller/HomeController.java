package com.poly.thuviendatn.Controller;

import com.poly.thuviendatn.Model.LoaiSach;
import com.poly.thuviendatn.Model.Sach;
import com.poly.thuviendatn.Model.TaiKhoan;
import com.poly.thuviendatn.Repository.LoaiSachRepository;
import com.poly.thuviendatn.Repository.SachRepository;
import com.poly.thuviendatn.Repository.TaiKhoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private final SachRepository sachRepository;
    private final LoaiSachRepository loaiSachRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    @Autowired
    public HomeController(SachRepository sachRepository,
                          LoaiSachRepository loaiSachRepository,
                          TaiKhoanRepository taiKhoanRepository) {
        this.sachRepository = sachRepository;
        this.loaiSachRepository = loaiSachRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @GetMapping("/")
    public String hienThiTrangChu(Model model) {
        Pageable pageable = PageRequest.of(0, 8);
        List<Sach> danhSachSach = sachRepository.findTop8ByOrderByMaSachDesc(pageable);

        // Validate hinhAnh paths
//        danhSachSach.forEach(sach -> {
//            if (sach.getHinhAnh() != null && !sach.getHinhAnh().isEmpty()) {
//                logger.info("hinhAnh for book {}: {}", sach.getMaSach(), sach.getHinhAnh());
//                // Ensure path is relative and clean
//                String processedPath = sach.getHinhAnh().replace("\\", "/");
//                if (!processedPath.startsWith("Image/Anhbia/")) {
//                    logger.warn("Invalid hinhAnh path for book {}: {}", sach.getMaSach(), processedPath);
//                    sach.setHinhAnh(null); // Fallback to default
//                } else {
//                    sach.setHinhAnh(processedPath);
//                }
//            } else {
//                logger.warn("hinhAnh is null or empty for book {}", sach.getMaSach());
//                sach.setHinhAnh(null);
//            }
//        });

        model.addAttribute("sachList", danhSachSach);
        logger.info("Number of books sent to template: {}", danhSachSach.size());
        return "public/home";
    }

    @GetMapping("/books")
    public String booksByCategory(@RequestParam("categoryId") Integer categoryId, Model model) {
        Optional<LoaiSach> loaiSach = loaiSachRepository.findById(categoryId);
        List<Sach> sachList = sachRepository.findByDanhMucLoaiSachMaCategory(categoryId);

        sachList.forEach(sach -> {
            if (sach.getHinhAnh() != null && !sach.getHinhAnh().isEmpty()) {
                String processedPath = sach.getHinhAnh().replace("\\", "/");
                sach.setHinhAnh(processedPath.startsWith("Image/Anhbia/") ? processedPath : null);
            } else {
                sach.setHinhAnh(null);
            }
        });

        model.addAttribute("loaiSach", loaiSach.orElse(null));
        model.addAttribute("sachList", sachList);
        return "public/sachtheodanhmuc";
    }

    @GetMapping("/aboutus")
    public String showGioiThieu(Model model) {
        List<Sach> sachList = sachRepository.findAll();

        sachList.forEach(sach -> {
            if (sach.getHinhAnh() != null && !sach.getHinhAnh().isEmpty()) {
                String processedPath = sach.getHinhAnh().replace("\\", "/");
                sach.setHinhAnh(processedPath.startsWith("Image/Anhbia/") ? processedPath : null);
            } else {
                sach.setHinhAnh(null);
            }
        });

        model.addAttribute("sachList", sachList);
        return "layout/gioithieu";
    }
}