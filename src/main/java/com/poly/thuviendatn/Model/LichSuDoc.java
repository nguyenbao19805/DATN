package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "LichSuDoc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichSuDoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maLichSu")
    private Integer maLichSu;

    @ManyToOne
    @JoinColumn(name = "maDocGia", nullable = false)
    private DocGia docGia;

    @ManyToOne
    @JoinColumn(name = "maSach", nullable = false)
    private Sach sach;

    @Column(name = "ngayBatDau")
    private LocalDateTime ngayBatDau; // Start reading date

    @Column(name = "ngayKetThuc")
    private LocalDateTime ngayKetThuc; // End reading date

    @Column(name = "trangThai", length = 50)
    private String trangThai; // e.g., "Đang đọc", "Hoàn thành"

    @Column(name = "ghiChu", length = 255)
    private String ghiChu; // Optional notes
}