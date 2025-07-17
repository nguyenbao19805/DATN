package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "DanhGia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DanhGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maDanhGia;

    private Integer soSao;

    private String noiDung;

    @ManyToOne
    @JoinColumn(name = "maDocGia")
    private DocGia docGia;

    @Column(name = "ngayDanhGia")
    private LocalDateTime ngayDanhGia;

    @ManyToOne
    @JoinColumn(name = "maSach")
    private Sach sach;
}