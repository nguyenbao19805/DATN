package com.poly.thuviendatn.Service;
import com.poly.thuviendatn.Model.TrangSachHinhAnh;
import com.poly.thuviendatn.Repository.TrangSachHinhAnhRepository;

import com.poly.thuviendatn.Model.Sach;
import com.poly.thuviendatn.Model.TrangSach;
import com.poly.thuviendatn.Repository.SachRepository;
import com.poly.thuviendatn.Repository.TrangSachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class SachDienTuService {

    private final SachRepository sachRepository;
    private final TrangSachRepository trangSachRepository;
    private final TrangSachHinhAnhRepository trangSachHinhAnhRepository;

    private final String COVER_IMAGE_DIR = "src/main/resources/static/Image/Anhbia/";
    private final String PAGE_IMAGE_DIR_BASE = "src/main/resources/static/Image/SachDienTu/";

    @Autowired
    public SachDienTuService(SachRepository sachRepository,
                             TrangSachRepository trangSachRepository,
                             TrangSachHinhAnhRepository trangSachHinhAnhRepository) {
        this.sachRepository = sachRepository;
        this.trangSachRepository = trangSachRepository;
        this.trangSachHinhAnhRepository = trangSachHinhAnhRepository;
    }

    public void saveSachDienTu(Sach sach, MultipartFile coverImage, List<MultipartFile> pageImages) throws IOException {
        sach.setHinhThuc(false);
        sach.setSoLuong(1);

        if (coverImage != null && !coverImage.isEmpty()) {
            String coverPath = saveFile(coverImage, COVER_IMAGE_DIR);
            sach.setHinhAnh("/Image/Anhbia/" + coverPath);
        }

        Sach saved = sachRepository.save(sach);

        if (pageImages != null && !pageImages.isEmpty()) {
            String sachDir = PAGE_IMAGE_DIR_BASE + saved.getMaSach();
            Path sachPath = Paths.get(sachDir);
            if (!Files.exists(sachPath)) Files.createDirectories(sachPath);

            TrangSach trangSach = new TrangSach();
            trangSach.setSach(saved);
            trangSach = trangSachRepository.save(trangSach);

            int index = 1;
            for (MultipartFile file : pageImages) {
                if (file != null && !file.isEmpty()) {
                    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    Path filePath = sachPath.resolve(fileName);
                    Files.write(filePath, file.getBytes());

                    TrangSachHinhAnh hinhAnh = new TrangSachHinhAnh();
                    hinhAnh.setTrangSach(trangSach);
                    hinhAnh.setHinhAnh("/Image/SachDienTu/" + saved.getMaSach() + "/" + fileName);
                    hinhAnh.setThuTu(index++);
                    trangSachHinhAnhRepository.save(hinhAnh);
                }
            }
        }
    }

    public void updateSachDienTu(Sach sach, MultipartFile coverImage, List<MultipartFile> pageImages) throws IOException {
        Sach existing = sachRepository.findById(sach.getMaSach()).orElseThrow();

        // Cập nhật ảnh bìa nếu có
        if (coverImage != null && !coverImage.isEmpty()) {
            String coverPath = saveFile(coverImage, COVER_IMAGE_DIR);
            existing.setHinhAnh("/Image/Anhbia/" + coverPath);
        }

        // Cập nhật thông tin cơ bản
        existing.setTenSach(sach.getTenSach());
        existing.setDanhMuc(sach.getDanhMuc());
        existing.setTacGia(sach.getTacGia());
        existing.setNhaXuatBan(sach.getNhaXuatBan());
        existing.setMoTa(sach.getMoTa());
        existing.setNamXB(sach.getNamXB());
        existing.setHinhThuc(false);
        existing.setSoLuong(1);

        Sach saved = sachRepository.save(existing);

        // Lấy trang sách cũ
        TrangSach trangSach = trangSachRepository.findBySachMaSach(saved.getMaSach())
            .orElseGet(() -> {
                TrangSach ts = new TrangSach();
                ts.setSach(saved);
                return trangSachRepository.save(ts);
            });

        List<TrangSachHinhAnh> oldImages = trangSach.getHinhAnhs();
        int oldSize = oldImages.size();
        int newSize = (pageImages != null) ? pageImages.size() : 0;

        if (newSize > 0) {
            String sachDir = PAGE_IMAGE_DIR_BASE + saved.getMaSach();
            Path sachPath = Paths.get(sachDir);
            if (!Files.exists(sachPath)) Files.createDirectories(sachPath);

            for (int i = 0; i < newSize; i++) {
                MultipartFile file = pageImages.get(i);
                if (file == null || file.isEmpty()) continue;

                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = sachPath.resolve(fileName);
                Files.write(filePath, file.getBytes());

                String imagePath = "/Image/SachDienTu/" + saved.getMaSach() + "/" + fileName;

                if (i < oldSize) {
                    TrangSachHinhAnh oldImg = oldImages.get(i);
                    oldImg.setHinhAnh(imagePath);
                    oldImg.setThuTu(i + 1);
                    trangSachHinhAnhRepository.save(oldImg);
                } else {
                    TrangSachHinhAnh newImg = new TrangSachHinhAnh();
                    newImg.setTrangSach(trangSach);
                    newImg.setHinhAnh(imagePath);
                    newImg.setThuTu(i + 1);
                    trangSachHinhAnhRepository.save(newImg);
                }
            }

            // Xóa ảnh dư nếu có
            if (newSize < oldSize) {
                for (int i = newSize; i < oldSize; i++) {
                    trangSachHinhAnhRepository.delete(oldImages.get(i));
                }
            }
        }
    }

    private String saveFile(MultipartFile file, String dirPath) throws IOException {
        Path dir = Paths.get(dirPath);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = dir.resolve(filename);
        Files.write(filePath, file.getBytes());
        return filename;
    }
}
