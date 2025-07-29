package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SachYeuThich")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SachYeuThich {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maYeuThich")
    private Integer maYeuThich;

    @ManyToOne
    @JoinColumn(name = "maDocGia", nullable = false)
    private DocGia docGia;

    @ManyToOne
    @JoinColumn(name = "maSach", nullable = false)
    private Sach sach;

    @Column(name = "ngayThem")
    private LocalDateTime ngayThem; // Date added to favorites

    @Column(name = "ghiChu", length = 255)
    private String ghiChu; // Optional notes
}