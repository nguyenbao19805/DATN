package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.DanhMuc;
import com.poly.thuviendatn.Model.TacGia;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TacGiaRepository extends JpaRepository<TacGia, Integer> {
    Page<TacGia> findByTenTacGiaContainingIgnoreCase(String keyword, Pageable pageable);

}