package com.poly.thuviendatn.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BorrowedbookController {

    @GetMapping("/borrowedbook")
    public String showBorrowedBookPage(Model model) {
        return "Public/sachdangmuon";  // tương ứng file: templates/Public/borrowedbook.html
    }

    @GetMapping("/followedbook")
    public String showgfollowedBookPage(Model model) {
        return "Public/sachdangtheodoi";  // tương ứng file: templates/Public/borrowedbook.html
    }
}
