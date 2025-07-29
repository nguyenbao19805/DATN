package com.poly.thuviendatn.Repository;

import com.poly.thuviendatn.Model.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
    Optional<TaiKhoan> findByUsername(String username);
    Optional<TaiKhoan> findByEmail(String email);
    Optional<TaiKhoan> findByUsernameIgnoringCase(String username);
    Optional<TaiKhoan> findByEmailIgnoringCase(String email);

    /**
     * Finds TaiKhoan entities where the username or email contains the keyword (case-insensitive).
     * @param username The keyword to search in usernames.
     * @param email The keyword to search in emails.
     * @param pageable Pagination information.
     * @return A paginated list of TaiKhoan entities matching the criteria.
     */
    Page<TaiKhoan> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email, Pageable pageable);
    List<TaiKhoan> findByQuyenMaQuyen(Integer maQuyen);
    Optional<TaiKhoan> findByMaTaiKhoan(Integer maTaiKhoan);

}