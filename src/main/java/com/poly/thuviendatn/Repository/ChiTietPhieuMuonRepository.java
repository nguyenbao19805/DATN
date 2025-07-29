package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.ChiTietPhieuMuon;
import com.poly.thuviendatn.Model.PhieuMuon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface ChiTietPhieuMuonRepository extends JpaRepository<ChiTietPhieuMuon, Integer> {

    void deleteByPhieuMuon(PhieuMuon phieuMuon);
long countByPhieuMuon_MaPhieu(Integer maPhieu);

    @EntityGraph(attributePaths = {"sach"})
    List<ChiTietPhieuMuon> findByPhieuMuon_MaPhieu(Integer maPhieu);
}
