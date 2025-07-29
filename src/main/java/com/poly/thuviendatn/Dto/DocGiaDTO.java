package com.poly.thuviendatn.Dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocGiaDTO {

    private Integer maDocGia; // Dùng khi cập nhật, có thể null khi thêm mới

    @NotBlank(message = "Tên độc giả không được để trống")
    @Size(max = 100, message = "Tên không được vượt quá 100 ký tự")
    private String tenDocGia;

    @NotNull(message = "Ngày sinh không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date namSinh;

    @NotNull(message = "Giới tính không được để trống")
    private Boolean gioiTinh;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 200, message = "Địa chỉ không được vượt quá 200 ký tự")
    private String diaChi;

    @NotBlank(message = "CCCD không được để trống")
    @Pattern(regexp = "^\\d{12}$", message = "CCCD phải đúng 12 chữ số")
    private String cccd;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải đúng định dạng và đủ 10 chữ số")
    private String sdt;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Pattern(
    regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
    message = "Mật khẩu phải có ít nhất 8 ký tự, bao gồm cả chữ và số"
    )
    private String password;

    private boolean enabled = true;
}
