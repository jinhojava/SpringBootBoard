package com.study.bootboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Board {

    @Id//primary키 의미
    @GeneratedValue(strategy = GenerationType.IDENTITY)//기본키값 자동생성
    private Integer id;
    private String title;
    private String content;
    private String filename;
    private String filepath;

    public boolean hasImage() {
        return this.filepath != null && !this.filepath.isEmpty();
    }
}

