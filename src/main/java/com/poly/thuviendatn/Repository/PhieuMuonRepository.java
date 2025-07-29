package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.PhieuMuon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PhieuMuonRepository extends JpaRepository<PhieuMuon, Integer> {
    @Query("SELECT p FROM PhieuMuon p WHERE LOWER(p.docGia.tenDocGia) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.trangThai) LIKE LOWER(CONCAT('%', :keyword, '%'))")
Page<PhieuMuon> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

}