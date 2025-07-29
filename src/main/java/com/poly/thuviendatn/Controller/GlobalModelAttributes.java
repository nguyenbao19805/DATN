package com.poly.thuviendatn.Controller;

import com.poly.thuviendatn.Repository.LoaiSachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final LoaiSachRepository loaiSachRepository;

    @Autowired
    public GlobalModelAttributes(LoaiSachRepository loaiSachRepository) {
        this.loaiSachRepository = loaiSachRepository;
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        model.addAttribute("loaiSachList", loaiSachRepository.findAll());
    }
}