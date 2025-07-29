package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.KeSach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface KeSachRepository extends JpaRepository<KeSach, Long> {
    @Query("SELECT k FROM KeSach k WHERE LOWER(k.tenKeSach) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(k.viTri) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<KeSach> findByTenKeSachContainingIgnoreCaseOrViTriContainingIgnoreCase(String keyword, Pageable pageable);
}