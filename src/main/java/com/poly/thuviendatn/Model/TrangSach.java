package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "TrangSach")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrangSach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maTrang;

    @ManyToOne
    @JoinColumn(name = "MaSach", nullable = false)
    private Sach sach;

    // Xóa bỏ soTrang và noiDung vì không cần nữa

    // Danh sách ảnh của trang sách
   @OneToMany(mappedBy = "trangSach", cascade = CascadeType.ALL, orphanRemoval = true)
private List<TrangSachHinhAnh> hinhAnhs;

}
