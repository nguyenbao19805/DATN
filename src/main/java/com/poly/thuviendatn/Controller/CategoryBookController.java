// Source code is decompiled from a .class file using FernFlower decompiler.
package com.poly.thuviendatn.Controller;

import com.poly.thuviendatn.Service.SachService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CategoryBookController {
   private final SachService SachService;

   public CategoryBookController(SachService SachService) {
      this.SachService = SachService;
   }

   @GetMapping({"/bookpaper"})
   public String sachgiay(Model model) {
      model.addAttribute("books", this.SachService.getAllBooks());
      return "Category/sachgiay";
   }

   @GetMapping({"/ebook"})
   public String sachDienTu(Model model) {
      return "Category/sachdientu";
   }

   @GetMapping({"/audiobook"})
   public String sachNoihNoi(Model model) {
      return "Category/sachnoi";
   }

   @GetMapping({"/lecture"})
   public String baiGiangDienTu(Model model) {
      return "Category/baigiangdientu";
   }

   @GetMapping({"/video"})
   public String sachVideo(Model model) {
      return "Category/sachvideo";
   }

   @GetMapping({"/album"})
   public String sachAnh(Model model) {
      return "Category/albumanh";
   }

   @GetMapping({"/skills"})
   public String sachKyNang(Model model) {
      return "Category/sachkynang";
   }

   @GetMapping({"/journal"})
   public String baoChi(Model model) {
      return "Category/baochi";
   }
}
