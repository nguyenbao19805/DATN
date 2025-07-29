// package com.poly.thuviendatn.Controller;

// import java.security.Principal;
// import jakarta.validation.Valid;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PostMapping;
// import com.poly.thuviendatn.Model.TaiKhoan;
// import com.poly.thuviendatn.Service.TaiKhoanService;

// @Controller
// public class EditprofileController {

//     @Autowired
//     private TaiKhoanService taiKhoanService;

//     @GetMapping("/profile")
//     public String showProfileForm(Model model, Principal principal) {
//         String username = principal.getName();
//         TaiKhoan taiKhoan = taiKhoanService.findByUsername(username);
//         model.addAttribute("taiKhoan", taiKhoan);
//         return "public/editprofile";
//     }

//     @PostMapping("/profile")
//     public String updateProfile(@Valid @ModelAttribute("taiKhoan") TaiKhoan taiKhoan, 
//                                BindingResult result, 
//                                Principal principal, 
//                                Model model) {
//         if (result.hasErrors()) {
//             return "public/editprofile";
//         }
//         try {
//             taiKhoanService.updateProfile(principal.getName(), taiKhoan.getEmail());
//             model.addAttribute("successMessage", "Cập nhật hồ sơ thành công!");
//             return "public/editprofile";
//         } catch (RuntimeException e) {
//             model.addAttribute("errorMessage", e.getMessage());
//             return "public/editprofile";
//         }
//     }
// }