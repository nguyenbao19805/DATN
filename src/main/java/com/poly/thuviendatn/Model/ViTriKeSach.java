package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ViTriKeSach")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViTriKeSach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maViTri")
    private Integer maViTri;

    @ManyToOne
    @JoinColumn(name = "maKeSach", nullable = false)
    private KeSach keSach;

    @ManyToOne
    @JoinColumn(name = "maSach", nullable = false)
    private Sach sach;

    @Column(name = "viTriChiTiet", length = 50)
    private String viTriChiTiet; // e.g., "Row 3, Slot 5"

    @Column(name = "soLuong")
    private Integer soLuong; // Number of copies at this position

    @Column(name = "ghiChu", length = 255)
    private String ghiChu; // Optional notes
}