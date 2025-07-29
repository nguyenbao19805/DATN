package com.poly.thuviendatn.Service;

import com.poly.thuviendatn.Model.BanQuyen;
import com.poly.thuviendatn.Model.Sach;
import com.poly.thuviendatn.Repository.BanQuyenRepository;
import com.poly.thuviendatn.Repository.SachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BanQuyenService {

    private final BanQuyenRepository banQuyenRepository;
    private final SachRepository sachRepository;

    @Autowired
    public BanQuyenService(BanQuyenRepository banQuyenRepository, SachRepository sachRepository) {
        this.banQuyenRepository = banQuyenRepository;
        this.sachRepository = sachRepository;
    }

    public List<BanQuyen> getAllBanQuyen() {
        return banQuyenRepository.findAll();
    }

    public Page<BanQuyen> getAllBanQuyen(Pageable pageable) {
        return banQuyenRepository.findAll(pageable);
    }

    public void saveBanQuyen(BanQuyen banQuyen) {
        Sach sach = sachRepository.findById(banQuyen.getSach().getMaSach())
                .orElseThrow(() -> new RuntimeException("Sách không tồn tại"));
        banQuyen.setSach(sach);
        banQuyenRepository.save(banQuyen);
    }
}