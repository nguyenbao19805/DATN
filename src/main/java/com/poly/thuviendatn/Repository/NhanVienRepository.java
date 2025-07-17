package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.DocGia;
import com.poly.thuviendatn.Model.NhanVien;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
    Page<NhanVien> findByTenNhanVienContainingIgnoreCase(String tenNhanVien, Pageable pageable);
    Page<NhanVien> findByTenNhanVienContainingIgnoreCaseOrCccdContainingIgnoreCase(String tenNhanVien, String cccd, Pageable pageable);
    Optional<NhanVien> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByCccd(String cccd);
    Optional<NhanVien> findByCccd(String cccd);


}