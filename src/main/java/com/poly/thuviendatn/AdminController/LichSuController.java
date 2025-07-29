package com.poly.thuviendatn.AdminController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/lichsu")
public class LichSuController {

    @GetMapping
    public String showLichSuList(Model model) {
        model.addAttribute("activeSection", "lichsu");
        return "admin/lichsu/lichsu";
    }
}