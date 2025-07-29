package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.ChiTietPhieuMuon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiTietPhieuMuonRepository extends JpaRepository<ChiTietPhieuMuon, Integer> {
}