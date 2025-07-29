package com.poly.thuviendatn.ApiController;

import com.poly.thuviendatn.Model.DocGia;
import com.poly.thuviendatn.Repository.DocGiaRepository;
import com.poly.thuviendatn.Repository.SachRepository;
import com.poly.thuviendatn.Service.DocGiaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sach")
public class SachApiController {

    @Autowired
    private SachRepository sachRepository;

    @GetMapping("/{maSach}")
    public ResponseEntity<?> getTenSachByMa(@PathVariable Integer maSach) {
        return sachRepository.findById(maSach)
                .map(sach -> ResponseEntity.ok().body(sach))
                .orElse(ResponseEntity.notFound().build());
    }
}
