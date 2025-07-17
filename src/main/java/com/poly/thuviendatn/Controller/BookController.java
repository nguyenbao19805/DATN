package com.poly.thuviendatn.Controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.thuviendatn.Model.DanhGia;
import com.poly.thuviendatn.Model.Sach;
import com.poly.thuviendatn.Service.SachService;

@Controller
@RequestMapping("/sach")
public class BookController {

    private final SachService sachService;

    @Autowired
    public BookController(SachService sachService) {
        this.sachService = sachService;
    }

    @GetMapping("/gioithieu")
    public String showBookIntro() {
        return "Layout/bookintro"; // Thymeleaf sẽ tìm tệp gioithieusach.html trong thư mục templates/public/
    }

    @GetMapping("/chitiet/{id}")
    public String showBookDetail(@PathVariable("id") Integer id, Model model) {
        Sach sach = sachService.findById(id);
        if (sach == null) {
            return "error/404"; // Trang báo lỗi không tìm thấy sách
        }
        model.addAttribute("sach", sach);
        model.addAttribute("danhGias", sachService.getDanhGiasBySachId(id));
        model.addAttribute("newDanhGia", new DanhGia());
        return "public/chitietsach"; // Đường dẫn file Thymeleaf: templates/public/chitietsach.html
    }

    @PostMapping("/chitiet/{id}/danhgia")
    public String submitDanhGia(@PathVariable("id") Integer id, @ModelAttribute("newDanhGia") DanhGia danhGia, Model model) {
        Sach sach = sachService.findById(id);
        if (sach == null) {
            return "error/404";
        }
        danhGia.setSach(sach);
        danhGia.setNgayDanhGia(LocalDateTime.now());
        sachService.saveDanhGia(danhGia);
        return "redirect:/sach/chitiet/" + id;
    }

        @GetMapping("/sach/sachchitiet/{id}")
         public String readBook( Model model) {
        return "public/readbook2"; // Maps to realbook2.html
    }
}