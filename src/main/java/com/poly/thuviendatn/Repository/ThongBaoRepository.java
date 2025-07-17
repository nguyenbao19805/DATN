package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.TaiKhoan;
import com.poly.thuviendatn.Model.ThongBao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, Integer> {
    Page<ThongBao> findByTieuDeContainingIgnoreCaseOrTaiKhoanUsernameContainingIgnoreCase(String tieuDe, String username, Pageable pageable);
    List<ThongBao> findByTaiKhoan(TaiKhoan taiKhoan);
}