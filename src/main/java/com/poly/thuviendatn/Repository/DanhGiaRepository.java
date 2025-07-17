package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.DanhGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhGiaRepository extends JpaRepository<DanhGia, Long> {
    List<DanhGia> findBySachMaSach(Integer maSach);
}