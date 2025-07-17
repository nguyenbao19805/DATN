package com.poly.thuviendatn.Service;

import com.poly.thuviendatn.Model.DanhGia;
import com.poly.thuviendatn.Model.LoaiSach;
import com.poly.thuviendatn.Model.Sach;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ISachService {
    Page<Sach> findAllBooks(int page, int size, String keyword);
    List<Sach> getAllBooks();
    List<LoaiSach> getAllLoaiSach();
    List<Sach> getBooksByLoaiSachId(Integer maCategory);
    Sach findById(Integer id);
    void saveDanhGia(DanhGia danhGia);
    List<DanhGia> getDanhGiasBySachId(Integer maSach);
    void saveSachWithPages(Sach sach, MultipartFile coverImage, List<MultipartFile> pageImages) throws IOException;
}