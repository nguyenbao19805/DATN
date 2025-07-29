package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.PhieuMuon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhieuMuonRepository extends JpaRepository<PhieuMuon, Integer> {

    // Tìm kiếm theo tên độc giả hoặc trạng thái
    @org.springframework.data.jpa.repository.Query("SELECT p FROM PhieuMuon p WHERE LOWER(p.docGia.tenDocGia) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.trangThai) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<PhieuMuon> searchByKeyword(@org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);

    // Lọc phiếu mượn theo trạng thái cụ thể
    Page<PhieuMuon> findByTrangThaiIgnoreCase(String trangThai, Pageable pageable);

    // Lọc phiếu mượn theo trạng thái và từ khóa (ví dụ: tìm kiếm trong tab "Đã trả")
    @org.springframework.data.jpa.repository.Query("SELECT p FROM PhieuMuon p WHERE LOWER(p.trangThai) = LOWER(:status) AND LOWER(p.docGia.tenDocGia) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<PhieuMuon> findByTrangThaiAndKeyword(@org.springframework.data.repository.query.Param("status") String status,
                                              @org.springframework.data.repository.query.Param("keyword") String keyword,
                                              Pageable pageable);
}
