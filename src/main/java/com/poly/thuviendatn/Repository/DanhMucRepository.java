package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.DanhMuc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {
    Page<DanhMuc> findByTenDanhMucContainingIgnoreCase(String keyword, Pageable pageable);
    List<DanhMuc> findByLoaiSachMaLoaiSach(Integer maLoaiSach);
    List<DanhMuc> findByLoaiSach_MaLoaiSach(Integer maLoaiSach);

}