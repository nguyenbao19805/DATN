package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.Sach;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SachRepository extends JpaRepository<Sach, Integer> {
    @Query("SELECT s FROM Sach s WHERE s.tenSach LIKE %:keyword%")
    Page<Sach> findByKeyword(String keyword, Pageable pageable);

    Page<Sach> findByTenSachContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("SELECT s FROM Sach s WHERE s.danhMuc.loaiSach.maLoaiSach = :maCategory")
    List<Sach> findByDanhMucLoaiSachMaCategory(Integer maCategory);

}