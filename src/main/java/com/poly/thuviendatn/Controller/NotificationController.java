package com.poly.thuviendatn.Controller;

import com.poly.thuviendatn.Model.DocGia;
import com.poly.thuviendatn.Model.TaiKhoan;
import com.poly.thuviendatn.Model.ThongBao;
import com.poly.thuviendatn.Repository.DocGiaRepository;
import com.poly.thuviendatn.Repository.TaiKhoanRepository;
import com.poly.thuviendatn.Repository.ThongBaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class NotificationController {

    @Autowired
    private ThongBaoRepository thongBaoRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private DocGiaRepository docGiaRepository;

    @GetMapping("/notification")
    public String viewNotifications(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Get email from authentication

        Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByEmail(email);
        if (taiKhoanOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy tài khoản");
            return "Layout/notification";
        }
        TaiKhoan taiKhoan = taiKhoanOpt.get();

        // Fetch notifications for the TaiKhoan
        List<ThongBao> notifications = thongBaoRepository.findByTaiKhoan(taiKhoan);

        // Try to find DocGia by email for greeting
        Optional<DocGia> docGiaOpt = docGiaRepository.findByEmail(email);
        String displayName = docGiaOpt.map(DocGia::getTenDocGia).orElse(taiKhoan.getUsername());
        model.addAttribute("tenDocGia", displayName);

        model.addAttribute("notifications", notifications);

        return "Layout/notification";
    }

    @PostMapping("/notification/mark-as-read")
    public String markAsRead(@RequestParam("notificationId") Integer maThongBao) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByEmail(email);
        if (taiKhoanOpt.isEmpty()) {
            return "redirect:/notification?error=User+not+found";
        }
        TaiKhoan taiKhoan = taiKhoanOpt.get();

        Optional<ThongBao> thongBaoOpt = thongBaoRepository.findById(maThongBao);
        if (thongBaoOpt.isPresent() && thongBaoOpt.get().getTaiKhoan().getMaTaiKhoan().equals(taiKhoan.getMaTaiKhoan())) {
            ThongBao thongBao = thongBaoOpt.get();
            thongBao.setDaDoc(true);
            thongBaoRepository.save(thongBao);
        }

        return "redirect:/notification";
    }

    @PostMapping("/notification/mark-all-as-read")
    public String markAllAsRead() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByEmail(email);
        if (taiKhoanOpt.isEmpty()) {
            return "redirect:/notification?error=User+not+found";
        }
        TaiKhoan taiKhoan = taiKhoanOpt.get();

        List<ThongBao> notifications = thongBaoRepository.findByTaiKhoan(taiKhoan);
        for (ThongBao thongBao : notifications) {
            if (!thongBao.getDaDoc()) {
                thongBao.setDaDoc(true);
                thongBaoRepository.save(thongBao);
            }
        }

        return "redirect:/notification";
    }
}