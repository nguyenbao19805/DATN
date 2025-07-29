package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.ViTriKeSach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViTriKeSachRepository extends JpaRepository<ViTriKeSach, Integer> {
    boolean existsBySachMaSachAndKeSachMaKeSach(Integer maSach, Long maKeSach);

    Optional<ViTriKeSach> findBySachMaSachAndKeSachMaKeSach(Integer maSach, Long maKeSach);

    boolean existsBySachMaSach(Integer maSach);

    @Query("SELECT SUM(v.soLuong) FROM ViTriKeSach v WHERE v.keSach.maKeSach = :maKeSach")
    Integer sumSoLuongByKeSach(Long maKeSach);

    @Query("SELECT SUM(v.soLuong) FROM ViTriKeSach v WHERE v.sach.maSach = :maSach")
    Integer sumSoLuongBySach(Integer maSach);

    List<ViTriKeSach> findByKeSachMaKeSach(Long maKeSach);
}
