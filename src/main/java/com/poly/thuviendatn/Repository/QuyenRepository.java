package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.Quyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuyenRepository extends JpaRepository<Quyen, Integer> {
    Optional<Quyen> findByMaQuyen(Integer maQuyen);
}