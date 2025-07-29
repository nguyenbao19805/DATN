package com.poly.thuviendatn.Model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;



@Entity
@Table(name = "LichSuNap")
@Data
@NoArgsConstructor
public class LichSuNap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // ID riêng cho từng lần nạp

    @ManyToOne
    @JoinColumn(name = "maTaiKhoan", nullable = false)
    private TaiKhoan taiKhoan;
    

    @Temporal(TemporalType.DATE)
    private Date ngayNap;

    private Double soTien;
}


