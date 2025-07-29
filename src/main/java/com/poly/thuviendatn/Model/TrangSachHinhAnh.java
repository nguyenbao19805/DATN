package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TrangSach_HinhAnh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrangSachHinhAnh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "HinhAnh", length = 255, nullable = false)
    private String hinhAnh;

    @Column(name = "ThuTu")
    private Integer thuTu;

    @ManyToOne
    @JoinColumn(name = "MaTrang", nullable = false)
    private TrangSach trangSach;
}
