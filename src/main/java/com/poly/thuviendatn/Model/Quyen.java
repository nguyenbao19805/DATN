package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Quyen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maQuyen;

    @Column(length = 50)
    private String tenQuyen; // Ví dụ: ROLE_ADMIN, ROLE_STAFF

    @OneToMany(mappedBy = "quyen")
    private List<TaiKhoan> taiKhoans;
}