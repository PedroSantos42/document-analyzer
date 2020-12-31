package com.pedrosantos.challenge.dtos;

import java.io.Serializable;

import com.pedrosantos.challenge.entities.UserDocument;

import lombok.Data;

@Data
public class ListUserDocumentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String user;

    private String title;

    private String resourceUrl;

    public ListUserDocumentDTO(UserDocument obj) {
        this.user = obj.getUser();
        this.title = obj.getTitle();
        this.resourceUrl = obj.getLocation();
    }
}
