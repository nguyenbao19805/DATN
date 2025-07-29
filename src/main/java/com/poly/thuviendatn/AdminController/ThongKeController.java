package com.poly.thuviendatn.AdminController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/thongke")
public class ThongKeController {

    @GetMapping
    public String showThongKeList(Model model) {
        model.addAttribute("activeSection", "thongke");
        return "admin/thongke/thongke";
    }
}