package com.pedrosantos.challenge.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity(name = "WORD_MATCHES")
public class WordMatch {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String word;
	private Integer quantity;

	@ManyToOne
	@JoinColumn(name = "document_id")
	private UserDocument document;
}
