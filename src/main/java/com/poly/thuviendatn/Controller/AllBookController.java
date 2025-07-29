package com.poly.thuviendatn.Controller;

import com.poly.thuviendatn.Model.Sach;
import com.poly.thuviendatn.Repository.SachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/toanbosach")
public class AllBookController {

    @Autowired
    private SachRepository sachRepository;

    @GetMapping
    public String listAllBooks(
            @RequestParam(defaultValue = "0") int sachPage,
            @RequestParam(defaultValue = "10") int sachSize,
            @RequestParam(required = false) String sachKeyword,
            Model model) {

        // Create Pageable object for pagination
        Pageable pageable = PageRequest.of(sachPage, sachSize);

        // Fetch books based on search keyword or all books
        Page<Sach> sachPageData;
        if (sachKeyword != null && !sachKeyword.isEmpty()) {
            sachPageData = sachRepository.findByTenSachContainingIgnoreCase(sachKeyword, pageable);
        } else {
            sachPageData = sachRepository.findAll(pageable);
        }

        // Add attributes to the model
        model.addAttribute("sachPage", sachPageData);
        model.addAttribute("sachKeyword", sachKeyword);

        // Return the view template
        return "Public/AllBook";
    }
}