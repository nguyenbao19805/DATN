package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "NhaXuatBan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhaXuatBan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maNXB;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String tenNXB;

    @Column(columnDefinition = "NVARCHAR(200)")
    private String diaChi;

    @Column(length = 15)
    private String sdt;

    @OneToMany(mappedBy = "nhaXuatBan")
    @JsonIgnore
    private List<Sach> sachs;
}