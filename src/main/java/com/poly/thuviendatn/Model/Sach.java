package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Sach")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maSach;

    @Column(columnDefinition = "NVARCHAR(155)")
    private String tenSach;

    @ManyToOne
    @JoinColumn(name = "maDanhMuc", nullable = false)
    private DanhMuc danhMuc;

    @ManyToOne
    @JoinColumn(name = "maTacGia")
    private TacGia tacGia;

    @ManyToOne
    @JoinColumn(name = "maNXB")
    private NhaXuatBan nhaXuatBan;

    @Column
    private Integer namXB;

    @Column
    private Integer soLuong;

    @Column(length = 255)
    private String hinhAnh;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @Column(nullable = false)
    private boolean hinhThuc = true;

    @OneToMany(mappedBy = "sach")
    private List<TrangSach> trangSachs;

    @OneToMany(mappedBy = "sach")
    private List<DanhGia> danhGias;

    @OneToMany(mappedBy = "sach")
    private List<LichSuDoc> lichSuDocs;

    @OneToMany(mappedBy = "sach")
    private List<ChiTietPhieuMuon> chiTietPhieuMuons;
}