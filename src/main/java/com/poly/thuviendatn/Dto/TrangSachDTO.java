package com.poly.thuviendatn.Dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class TrangSachDTO {
    private Integer soTrang;
    private List<MultipartFile> hinhAnhs;
}