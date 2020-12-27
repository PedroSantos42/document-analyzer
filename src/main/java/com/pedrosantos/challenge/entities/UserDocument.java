package com.pedrosantos.challenge.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity(name = "USER_DOCUMENTS")
public class UserDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String user;

    @OneToMany(mappedBy = "document")
    private List<WordMatch> matches;

    @Column(name = "created_at")
    private Date createdAt;
}
