package com.poly.thuviendatn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "TacGia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TacGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maTacGia;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String tenTacGia;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date namSinh;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String queQuan;

    @OneToMany(mappedBy = "tacGia")
    private List<Sach> sachs;
}