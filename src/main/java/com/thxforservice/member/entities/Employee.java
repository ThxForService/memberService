package com.thxforservice.member.entities;

import com.thxforservice.member.constants.Status;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Employee extends Member {
    @Column(unique = true, nullable = false)
    private Long empNo;

    @Lob
    private String introduction; // 소개

    @Column(length=50)
    private String subject; // 담당분야

    @Column(scale=2)
    private Double rating;

    @Enumerated(EnumType.STRING)
    @Column(length=10)
    private Status status;
}
