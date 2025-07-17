package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "KeSach")
public class KeSach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maKeSach")
    private Long maKeSach;

    @Column(name = "tenKeSach", nullable = false, length = 100)
    private String tenKeSach;

    @Column(name = "viTri", length = 255)
    private String viTri;

    @Column(name = "sucChua")
    private Integer sucChua;

    @Column(name = "moTa", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @Column(name = "ngayTao", updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngayCapNhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getMaKeSach() { return maKeSach; }
    public void setMaKeSach(Long maKeSach) { this.maKeSach = maKeSach; }
    public String getTenKeSach() { return tenKeSach; }
    public void setTenKeSach(String tenKeSach) { this.tenKeSach = tenKeSach; }
    public String getViTri() { return viTri; }
    public void setViTri(String viTri) { this.viTri = viTri; }
    public Integer getSucChua() { return sucChua; }
    public void setSucChua(Integer sucChua) { this.sucChua = sucChua; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
    public LocalDateTime getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(LocalDateTime ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
}