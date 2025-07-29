package com.poly.thuviendatn.Dto;

import com.poly.thuviendatn.Model.DanhMuc;
import com.poly.thuviendatn.Model.NhaXuatBan;
import com.poly.thuviendatn.Model.TacGia;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class SachDTO {
    private String tenSach;
    private DanhMuc danhMuc;
    private NhaXuatBan nhaXuatBan;
    private TacGia tacGia;
    private Integer namXB;
    private Integer soLuong;
    private MultipartFile hinhAnh;
    private String moTa;
    private List<TrangSachDTO> trangSachs;
}