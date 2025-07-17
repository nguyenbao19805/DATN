package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.TrangSachHinhAnh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrangSachHinhAnhRepository extends JpaRepository<TrangSachHinhAnh, Long> {

    // Ví dụ: tìm tất cả ảnh theo mã trang, sắp xếp theo thứ tự
    List<TrangSachHinhAnh> findByTrangSach_MaTrangOrderByThuTuAsc(Integer maTrang);
}
