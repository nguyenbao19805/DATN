package com.poly.thuviendatn.AdminController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/danhgia")
public class DanhGiaController {

    @GetMapping
    public String showDanhGiaList(Model model) {
        model.addAttribute("activeSection", "danhgia");
        return "admin/danhgia/danhgia";
    }
}