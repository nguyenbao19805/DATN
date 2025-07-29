package com.poly.thuviendatn.Controller;

import com.poly.thuviendatn.Model.DanhMuc;
import com.poly.thuviendatn.Model.LoaiSach;
import com.poly.thuviendatn.Model.NhaXuatBan;
import com.poly.thuviendatn.Model.Sach;
import com.poly.thuviendatn.Model.TacGia;
import com.poly.thuviendatn.Repository.DanhMucRepository;
import com.poly.thuviendatn.Repository.LoaiSachRepository;
import com.poly.thuviendatn.Repository.NhaXuatBanRepository;
import com.poly.thuviendatn.Repository.SachRepository;
import com.poly.thuviendatn.Repository.TacGiaRepository;
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
@RequestMapping("/all-books") // Changed from /toanbosach to match template
public class AllBookController {

    @Autowired
    private SachRepository sachRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

    @Autowired
    private LoaiSachRepository loaiSachRepository;

    @Autowired
    private TacGiaRepository tacGiaRepository;

    @Autowired
    private NhaXuatBanRepository nhaXuatBanRepository;

    @GetMapping
    public String listAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Integer maDanhMuc,
            @RequestParam(required = false) Integer maLoaiSach,
            @RequestParam(required = false) Integer maTacGia,
            @RequestParam(required = false) Integer maNXB,
            Model model) {

        // Create Pageable object for pagination
        Pageable pageable = PageRequest.of(page, size);

        // Fetch filtered and paginated books
        Page<Sach> sachPageData = sachRepository.findBooks(null, maDanhMuc, maLoaiSach, maTacGia, maNXB, pageable);

        // Add attributes to the model
        model.addAttribute("sachs", sachPageData.getContent());
        model.addAttribute("danhMucs", danhMucRepository.findAll());
        model.addAttribute("loaiSachs", loaiSachRepository.findAll());
        model.addAttribute("tacGias", tacGiaRepository.findAll());
        model.addAttribute("nhaXuatBans", nhaXuatBanRepository.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("hasMore", sachPageData.hasNext());

        // Return the view template
        return "Public/AllBook";
    }
}