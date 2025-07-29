package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayMuon;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayHetHan;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayTra;

    @Column
    private Integer soLuong;

    @Column
    private Double thanhToan;

    @Column(columnDefinition = "NVARCHAR(200)")
    private String trangThai;

    @Column
    private Double tienPhat;

    @OneToMany(mappedBy = "phieuMuon", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ChiTietPhieuMuon> chiTietPhieuMuons;
}