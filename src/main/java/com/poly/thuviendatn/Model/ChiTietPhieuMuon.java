package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ChiTietPhieuMuon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhieuMuon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maCTPM;

    @ManyToOne
    @JoinColumn(name = "MaPhieuMuon")
    private PhieuMuon phieuMuon;

    @ManyToOne
    @JoinColumn(name = "MaSach")
    private Sach sach;

    @Temporal(TemporalType.DATE)
    private Date ngayTra;

    @Column(length = 255)
    private String ghiChu;

}