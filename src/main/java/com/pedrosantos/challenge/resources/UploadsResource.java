package com.pedrosantos.challenge.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/uploads")
public class UploadsResource {

	@GetMapping
	public ResponseEntity<String> index() {

		return ResponseEntity.ok().body("Hello from uploads!");
	}

	// acessar service para análise de documentos
	// criar UserDocument
	// criar Matches
	//
	//

}
