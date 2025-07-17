package com.poly.thuviendatn.Service;
import com.poly.thuviendatn.Model.TrangSachHinhAnh;
import com.poly.thuviendatn.Repository.TrangSachHinhAnhRepository;

import com.poly.thuviendatn.Model.DanhGia;
import com.poly.thuviendatn.Model.LoaiSach;
import com.poly.thuviendatn.Model.Sach;
import com.poly.thuviendatn.Model.TrangSach;
import com.poly.thuviendatn.Repository.DanhGiaRepository;
import com.poly.thuviendatn.Repository.LoaiSachRepository;
import com.poly.thuviendatn.Repository.SachRepository;
import com.poly.thuviendatn.Repository.TrangSachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class SachService implements ISachService {

    private final SachRepository sachRepository;
    private final DanhGiaRepository danhGiaRepository;
    private final LoaiSachRepository loaiSachRepository;
    private final TrangSachRepository trangSachRepository;

private final TrangSachHinhAnhRepository trangSachHinhAnhRepository;

@Autowired
public SachService(SachRepository sachRepository,
                   DanhGiaRepository danhGiaRepository,
                   LoaiSachRepository loaiSachRepository,
                   TrangSachRepository trangSachRepository,
                   TrangSachHinhAnhRepository trangSachHinhAnhRepository) {
    this.sachRepository = sachRepository;
    this.danhGiaRepository = danhGiaRepository;
    this.loaiSachRepository = loaiSachRepository;
    this.trangSachRepository = trangSachRepository;
    this.trangSachHinhAnhRepository = trangSachHinhAnhRepository;
}


    

    @Override
    public Page<Sach> findAllBooks(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword != null && !keyword.isEmpty()) {
            return sachRepository.findByTenSachContainingIgnoreCase(keyword, pageable);
        }
        return sachRepository.findAll(pageable);
    }

    @Override
    public List<Sach> getAllBooks() {
        return sachRepository.findAll();
    }

    @Override
    public List<LoaiSach> getAllLoaiSach() {
        return loaiSachRepository.findAll();
    }

    @Override
    public List<Sach> getBooksByLoaiSachId(Integer maCategory) {
        return sachRepository.findByDanhMucLoaiSachMaCategory(maCategory);
    }

    @Override
    public Sach findById(Integer id) {
        return sachRepository.findById(id).orElse(null);
    }

    @Override
    public void saveDanhGia(DanhGia danhGia) {
        danhGiaRepository.save(danhGia);
    }

    @Override
    public List<DanhGia> getDanhGiasBySachId(Integer maSach) {
        return danhGiaRepository.findBySachMaSach(maSach);
    }

    @Override
    public void saveSachWithPages(Sach sach, MultipartFile hinhAnhFile, List<MultipartFile> trangSachImages) throws IOException {
        // ✅ Xử lý ảnh bìa
        if (hinhAnhFile != null && !hinhAnhFile.isEmpty()) {
            String imagePath = saveImageToStaticFolder(hinhAnhFile);
            sach.setHinhAnh(imagePath);
        }

        // ✅ Lưu sách trước
        Sach savedSach = sachRepository.save(sach);

        // ✅ Lưu trang sách (nếu có)
        if (trangSachImages != null && !trangSachImages.isEmpty()) {
            if (trangSachImages != null && !trangSachImages.isEmpty()) {
    TrangSach trangSach = new TrangSach();
    trangSach.setSach(savedSach);
    trangSach = trangSachRepository.save(trangSach); // Lưu để có MaTrang

    int index = 1;
    for (MultipartFile file : trangSachImages) {
        if (file != null && !file.isEmpty()) {
            String path = saveImageToStaticFolder(file);

            TrangSachHinhAnh hinhAnh = new TrangSachHinhAnh();
            hinhAnh.setTrangSach(trangSach);
            hinhAnh.setHinhAnh(path);
            hinhAnh.setThuTu(index++); // Giữ thứ tự ảnh
            trangSachHinhAnhRepository.save(hinhAnh);
        }
    }
}

        }
    }

    // ✅ Lưu ảnh vào thư mục static và trả về đường dẫn
    private String saveImageToStaticFolder(MultipartFile file) throws IOException {
        String uploadDir = "src/main/resources/static/Image/Anhbia/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = UUID.randomUUID() + "_" + Objects.requireNonNull(file.getOriginalFilename());
        Path filePath = uploadPath.resolve(filename);
        Files.write(filePath, file.getBytes());

        // Trả về path hiển thị (trong th:src)
        return "/Image/Anhbia/" + filename;
    }
}
