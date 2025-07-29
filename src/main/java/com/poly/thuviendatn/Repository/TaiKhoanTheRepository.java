package com.poly.thuviendatn.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.thuviendatn.Model.TaiKhoanThe;

@Repository
public interface TaiKhoanTheRepository extends JpaRepository<TaiKhoanThe, Integer> {
}

