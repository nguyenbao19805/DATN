package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.BanQuyen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BanQuyenRepository extends JpaRepository<BanQuyen, Long> {
    boolean existsBySach_MaSach(Long maSach);

    // Optional method to find by ID (already provided by JpaRepository)
    // BanQuyen findById(Long id); // Uncomment if you want to explicitly declare it

    // Custom method to find by ID with a direct entity return (throws exception if not found)
    BanQuyen findByMaBanQuyen(Long id);
}