package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "NhanVien")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maNV;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String tenNhanVien;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date namSinh;

    @Column(columnDefinition = "BIT")
    private Boolean gioiTinh;

    @Column(columnDefinition = "NVARCHAR(200)")
    private String diaChi;

    @Column(length = 12)
    private String cccd;

    @Column(length = 15)
    private String sdt;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled = true;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date ngayBatDau;

    @Column(precision = 19, scale = 4)
    private BigDecimal luong;


}