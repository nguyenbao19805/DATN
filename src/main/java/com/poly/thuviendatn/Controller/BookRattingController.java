// Source code is decompiled from a .class file using FernFlower decompiler.
package com.poly.thuviendatn.Controller;

import com.poly.thuviendatn.Service.SachService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookRattingController {
   private final SachService SachService;

   public BookRattingController(SachService SachService) {
      this.SachService = SachService;
   }

   @GetMapping({"/bookratting"})
   public String xepHangSach(Model model) {
      model.addAttribute("books", this.SachService.getAllBooks());
      return "Public/bookratting";
   }
}
