package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ThongBao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongBao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maThongBao")
    private Integer maThongBao;

    @ManyToOne
    @JoinColumn(name = "maTaiKhoan", nullable = false)
    private TaiKhoan taiKhoan;

    @Column(name = "tieuDe", length = 100, nullable = false)
    private String tieuDe; // Notification title

    @Column(name = "noiDung", length = 1000, nullable = false)
    private String noiDung; // Notification content

    @Column(name = "ngayGui")
    private LocalDateTime ngayGui; // Date sent

    @Column(name = "daDoc")
    private Boolean daDoc = false; // Read status
}