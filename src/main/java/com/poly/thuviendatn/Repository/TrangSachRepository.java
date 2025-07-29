package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.TrangSach;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrangSachRepository extends JpaRepository<TrangSach, Integer> {
        Optional<TrangSach> findBySachMaSach(Integer maSach);

}