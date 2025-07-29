package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "DanhMuc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDanhMuc;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String tenDanhMuc;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "maLoaiSach", nullable = false)
    private LoaiSach loaiSach;

    @OneToMany(mappedBy = "danhMuc")
    private List<Sach> sachs;
    
}