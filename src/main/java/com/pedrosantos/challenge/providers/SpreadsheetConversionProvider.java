package com.pedrosantos.challenge.providers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pedrosantos.challenge.entities.WordMatch;

@Service
public class SpreadsheetConversionProvider {

	@Value("${storage.folder}")
	private String diskFolder;

	public String create(List<WordMatch> matches, String fileName) throws IOException {

		// creating Spread Sheet
		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("word_matches");

		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 4000);

		Row header = sheet.createRow(0);

		CellStyle headerStyle = workbook.createCellStyle();

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Calibri");
		font.setFontHeightInPoints((short) 16);
		font.setBold(true);
		headerStyle.setFont(font);

		Cell headerCell = header.createCell(0);
		headerCell.setCellValue("Word");
		headerCell.setCellStyle(headerStyle);

		headerCell = header.createCell(1);
		headerCell.setCellValue("Occurrency");
		headerCell.setCellStyle(headerStyle);

		// creating cells
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);

		int columnIndex = 2;

		for (WordMatch match : matches) {
			Row row = sheet.createRow(columnIndex);
			Cell cell = row.createCell(0);
			cell.setCellValue(match.getWord());
			cell.setCellStyle(style);

			cell = row.createCell(1);
			cell.setCellValue(match.getQuantity());
			cell.setCellStyle(style);

			columnIndex++;
		}

		// exporting xlsx file
		File currDir = new File(diskFolder);
		String path = currDir.getAbsolutePath();
		String fileLocation = String.format("%s/%s", path, fileName);

		FileOutputStream outputStream;
		outputStream = new FileOutputStream(fileLocation);
		workbook.write(outputStream);
		workbook.close();

		return fileLocation;
	}
}
