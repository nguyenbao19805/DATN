package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "BanQuyen")
@Data
public class BanQuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaBanQuyen")
    private Long maBanQuyen;

    @NotBlank(message = "Tên bản quyền không được để trống")
    @Size(max = 255, message = "Tên bản quyền không được vượt quá 255 ký tự")
    @Column(name = "TenBanQuyen", nullable = false, length = 255)
    private String tenBanQuyen;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    @Column(name = "MoTa", length = 1000)
    private String moTa;

    @NotNull(message = "Ngày đăng ký không được để trống")
    @Column(name = "NgayDangKy", nullable = false)
    private LocalDate ngayDangKy;

    @NotNull(message = "Sách không được để trống")
    @OneToOne
    @JoinColumn(name = "MaSach", nullable = false)
    private Sach sach;
}