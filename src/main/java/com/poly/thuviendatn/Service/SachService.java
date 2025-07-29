package com.poly.thuviendatn.Service;

import com.poly.thuviendatn.Model.DanhGia;
import com.poly.thuviendatn.Model.LoaiSach;
import com.poly.thuviendatn.Model.Sach;
import com.poly.thuviendatn.Model.TrangSach;
import com.poly.thuviendatn.Model.TrangSachHinhAnh;
import com.poly.thuviendatn.Repository.DanhGiaRepository;
import com.poly.thuviendatn.Repository.LoaiSachRepository;
import com.poly.thuviendatn.Repository.SachRepository;
import com.poly.thuviendatn.Repository.TrangSachRepository;
import com.poly.thuviendatn.Repository.TrangSachHinhAnhRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        // Xử lý ảnh bìa
        String existingHinhAnh = null;
        if (sach.getMaSach() != null) {
            // Lấy sách hiện tại từ DB để giữ ảnh bìa nếu không có ảnh mới
            Sach existingSach = sachRepository.findById(sach.getMaSach())
                    .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại: " + sach.getMaSach()));
            existingHinhAnh = existingSach.getHinhAnh();
        }

        if (hinhAnhFile != null && !hinhAnhFile.isEmpty()) {
            // Lưu ảnh bìa mới nếu có
            String imagePath = saveImageToStaticFolder(hinhAnhFile);
            sach.setHinhAnh(imagePath);
        } else if (existingHinhAnh != null) {
            // Giữ nguyên ảnh bìa hiện tại nếu không có ảnh mới
            sach.setHinhAnh(existingHinhAnh);
        } else {
            // Đặt ảnh mặc định cho sách mới nếu không có ảnh bìa
            sach.setHinhAnh("/Image/default.png");
        }

        // Kiểm tra danh mục để đảm bảo không null
        if (sach.getDanhMuc() == null) {
            throw new IllegalArgumentException("Danh mục không được để trống");
        }

        // Lưu sách
        Sach savedSach = sachRepository.save(sach);

        // Xử lý các trang sách
        if (trangSachImages != null && !trangSachImages.isEmpty()) {
            TrangSach trangSach = new TrangSach();
            trangSach.setSach(savedSach);
            TrangSach savedTrangSach = trangSachRepository.save(trangSach);

            List<TrangSachHinhAnh> hinhAnhs = new ArrayList<>();
            int thuTu = 1;
            for (MultipartFile file : trangSachImages) {
                if (file != null && !file.isEmpty()) {
                    String path = saveImageToStaticFolder(file);
                    TrangSachHinhAnh hinhAnh = new TrangSachHinhAnh();
                    hinhAnh.setTrangSach(savedTrangSach);
                    hinhAnh.setHinhAnh(path);
                    hinhAnh.setThuTu(thuTu++);
                    hinhAnhs.add(hinhAnh);
                }
            }
            if (!hinhAnhs.isEmpty()) {
                trangSachHinhAnhRepository.saveAll(hinhAnhs);
            }
        }
    }

    private String saveImageToStaticFolder(MultipartFile file) throws IOException {
        String uploadDir = "src/main/resources/static/Image/Anhbia/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            originalFilename = "unnamed_file";
        }

        String filename = UUID.randomUUID() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(filename);
        Files.write(filePath, file.getBytes());

        return "/Image/Anhbia/" + filename;
    }
}
