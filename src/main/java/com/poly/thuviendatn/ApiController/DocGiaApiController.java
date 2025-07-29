package com.poly.thuviendatn.ApiController;

import com.poly.thuviendatn.Model.DocGia;
import com.poly.thuviendatn.Repository.DocGiaRepository;
import com.poly.thuviendatn.Service.DocGiaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/docgia")
public class DocGiaApiController {

    @Autowired
    private DocGiaRepository docGiaRepository;

    @GetMapping("/{maDocGia}")
    public ResponseEntity<?> getDocGiaByMa(@PathVariable Integer maDocGia) {
        return docGiaRepository.findById(maDocGia)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}

