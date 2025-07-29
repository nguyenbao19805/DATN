package com.poly.thuviendatn.Model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TaiKhoan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoan {
    @Id
    @Column(name = "maTaiKhoan")
    private Integer maTaiKhoan;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "maQuyen")
    private Quyen quyen;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @OneToOne(mappedBy = "taiKhoan")
    private TaiKhoanThe taiKhoanThe;

    @OneToMany(mappedBy = "taiKhoan", cascade = CascadeType.ALL)
    private List<LichSuNap> lichSuNaps = new ArrayList<>();

}