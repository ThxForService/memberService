package com.thxforservice.member.entities;

import com.thxforservice.member.constants.Status;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Student extends Member {
    @Column(unique = true, nullable = false)
    private Long studentNo;

    @Column(length=10)
    private String grade; // 학년

    @Column(length=50)
    private String department; // 학과

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="professor")
    private Employee professor;

    @Enumerated(EnumType.STRING)
    @Column(length=20)
    private Status status;
}
