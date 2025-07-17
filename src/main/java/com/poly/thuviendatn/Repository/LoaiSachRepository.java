package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.DocGia;
import com.poly.thuviendatn.Model.LoaiSach;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoaiSachRepository extends JpaRepository<LoaiSach, Integer> {
    Page<LoaiSach> findByTenLoaiSachContainingIgnoreCase(String keyword, Pageable pageable);
    List<LoaiSach> findByMaLoaiSach(Integer maLoaiSach);

}
