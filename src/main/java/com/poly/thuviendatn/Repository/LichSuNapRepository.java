package com.poly.thuviendatn.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.thuviendatn.Model.LichSuNap;

@Repository
public interface LichSuNapRepository extends JpaRepository<LichSuNap, Integer> {
        List<LichSuNap> findByTaiKhoan_MaTaiKhoan(Integer maTaiKhoan);
        
}
