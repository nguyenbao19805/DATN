package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "PhieuMuon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuMuon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maPhieu;

    @ManyToOne
    @JoinColumn(name = "maDocGia")
    private DocGia docGia;

    @ManyToOne
    @JoinColumn(name = "maNV") // References TaiKhoan
    private TaiKhoan taiKhoan;

    @Column
    private LocalDate ngayMuon;

    @Column
    private LocalDate ngayHetHan;

    @Column
    private LocalDate ngayTra;

    @Column
    private String trangThai;

    @Column
    private BigDecimal tienPhat;

    @OneToMany(mappedBy = "phieuMuon")
    private List<ChiTietPhieuMuon> chiTietPhieuMuons;
}