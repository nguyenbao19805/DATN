// Source code is decompiled from a .class file using FernFlower decompiler.
package com.poly.thuviendatn.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForumController {
   public ForumController() {
   }

   @GetMapping({"forumhome"})
   public String dienDan(Model model) {
      return "Public/forumhome";
   }

   @GetMapping({"categorypost"})
   public String thaoLuanSach(Model model) {
      return "Public/categorypost";
   }

   @GetMapping({"fagbook"})
   public String hoiDapMuonSach(Model model) {
      return "Public/fagbook";
   }

   @GetMapping({"notifition"})
   public String thongBaoThuVien(Model model) {
      return "Public/notifition";
   }
}
