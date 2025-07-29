package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.Sach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SachRepository extends JpaRepository<Sach, Integer> {

    @Query("SELECT s FROM Sach s WHERE s.tenSach LIKE %:keyword%")
    Page<Sach> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Sach> findByTenSachContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("SELECT s FROM Sach s WHERE s.danhMuc.loaiSach.maLoaiSach = :maCategory")
    List<Sach> findByDanhMucLoaiSachMaCategory(@Param("maCategory") Integer maCategory);

    @Query("SELECT s FROM Sach s ORDER BY s.maSach DESC")
    List<Sach> findTop8ByOrderByMaSachDesc(Pageable pageable);

    @Query("SELECT s FROM Sach s " +
           "WHERE (:search IS NULL OR s.tenSach LIKE %:search% OR s.moTa LIKE %:search%) " +
           "AND (:maDanhMuc IS NULL OR s.danhMuc.maDanhMuc = :maDanhMuc) " +
           "AND (:maLoaiSach IS NULL OR s.danhMuc.loaiSach.maLoaiSach = :maLoaiSach) " +
           "AND (:maTacGia IS NULL OR s.tacGia.maTacGia = :maTacGia) " +
           "AND (:maNXB IS NULL OR s.nhaXuatBan.maNXB = :maNXB)")
    Page<Sach> findBooks(
            @Param("search") String search,
            @Param("maDanhMuc") Integer maDanhMuc,
            @Param("maLoaiSach") Integer maLoaiSach,
            @Param("maTacGia") Integer maTacGia,
            @Param("maNXB") Integer maNXB,
            Pageable pageable);
}