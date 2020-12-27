package com.pedrosantos.challenge.providers.interfaces;

import java.util.List;

import com.pedrosantos.challenge.entities.WordMatch;

public interface DocumentAnalysisProvider {
	List<WordMatch> analyseDocument();
}
