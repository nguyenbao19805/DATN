package com.poly.thuviendatn.AdminController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.thuviendatn.Service.DocGiaService;

@Controller
@RequestMapping("/admin/docgia")
public class NapTienController {

    @Autowired
    private DocGiaService docGiaService;

    @PostMapping
    public String napTien(@RequestParam("maTaiKhoan") Integer maTaiKhoan,
                          @RequestParam("soTien") Double soTien,
                          RedirectAttributes redirectAttributes) {
        try {
            docGiaService.napTien(maTaiKhoan, soTien);
            redirectAttributes.addFlashAttribute("successMessage", "Nạp tiền thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/docgia"; // hoặc trang bạn muốn quay về
    }
}
