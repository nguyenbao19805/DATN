package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TaiKhoanThe")
public class TaiKhoanThe {
    @Id
    @Column(name = "maTaiKhoan")
    private Integer maTaiKhoan;

    @OneToOne
    @MapsId
    @JoinColumn(name = "maTaiKhoan")
    private TaiKhoan taiKhoan;

    @Column
    private Double SoDu;
}


