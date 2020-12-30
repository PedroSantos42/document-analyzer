package com.pedrosantos.challenge.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity(name = "USER_DOCUMENTS")
public class UserDocument implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String title;
	private String location;
	private String user;

	@OneToMany(mappedBy = "document")
	private List<WordMatch> matches;

	@Column(name = "created_at")
	@JsonProperty("created_at")
	private Date createdAt;
}
