package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.TrangSach;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrangSachRepository extends JpaRepository<TrangSach, Integer> {
List<TrangSach> findAllBySachMaSach(Integer maSach);
        Optional<TrangSach> findBySachMaSach(Integer maSach);

}