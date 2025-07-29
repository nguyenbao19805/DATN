package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.NhaXuatBan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhaXuatBanRepository extends JpaRepository<NhaXuatBan, Integer> {
    Page<NhaXuatBan> findByTenNXBContainingIgnoreCase(String tenNXB, Pageable pageable);
}