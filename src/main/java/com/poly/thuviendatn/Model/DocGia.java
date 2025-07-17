package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "DocGia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDocGia;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String tenDocGia;

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

    @OneToMany(mappedBy = "docGia")
    private List<PhieuMuon> phieuMuons;

    @OneToMany(mappedBy = "docGia")
    private List<DanhGia> danhGias;

    @OneToMany(mappedBy = "docGia")
    private List<LichSuDoc> lichSuDocs;
}
