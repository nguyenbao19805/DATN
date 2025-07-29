package com.poly.thuviendatn.Controller;

import com.poly.thuviendatn.Model.LoaiSach;
import com.poly.thuviendatn.Model.Sach;
import com.poly.thuviendatn.Model.TaiKhoan;
import com.poly.thuviendatn.Repository.LoaiSachRepository;
import com.poly.thuviendatn.Repository.SachRepository;
import com.poly.thuviendatn.Repository.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

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
    public String home(Model model, Authentication authentication) {
        List<Sach> sachList = sachRepository.findAll();

        sachList.forEach(sach -> {
            if (sach.getHinhAnh() != null) {
                sach.setHinhAnh(sach.getHinhAnh().replace("D:\\DATN\\src\\main\\resources\\static\\", ""));
            }
        });

        model.addAttribute("sachList", sachList);

        // ✅ Hiển thị thông tin người dùng đăng nhập
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                Integer maTaiKhoan = Integer.parseInt(authentication.getName());
                Optional<TaiKhoan> optTaiKhoan = taiKhoanRepository.findByMaTaiKhoan(maTaiKhoan);
                if (optTaiKhoan.isPresent()) {
                    TaiKhoan taiKhoan = optTaiKhoan.get();
                    model.addAttribute("username", taiKhoan.getUsername());
                }
            } catch (NumberFormatException ignored) {
                // Trong trường hợp authentication.getName() không phải số (không cần xử lý)
            }
        }

        return "public/home";
    }

    @GetMapping("/books")
    public String booksByCategory(@RequestParam("categoryId") Integer categoryId, Model model) {
        Optional<LoaiSach> loaiSach = loaiSachRepository.findById(categoryId);
        List<Sach> sachList = sachRepository.findByDanhMucLoaiSachMaCategory(categoryId);

        sachList.forEach(sach -> {
            if (sach.getHinhAnh() != null) {
                sach.setHinhAnh(sach.getHinhAnh().replace("D:\\DATN\\src\\main\\resources\\static\\", ""));
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
            if (sach.getHinhAnh() != null) {
                sach.setHinhAnh(sach.getHinhAnh().replace("D:\\DATN\\src\\main\\resources\\static\\", ""));
            }
        });

        model.addAttribute("sachList", sachList);
        return "layout/gioithieu";
    }
}
