package com.example.java.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;


public interface FileService {

    List<String> getFileWithQuery(MultipartFile file, String id) throws IOException;

    List<String> getFileWithCobol(MultipartFile file) throws IOException;

    List<String> getResultWithDate(MultipartFile file) throws IOException;

    List<String> getResultWithId(MultipartFile file, String id) throws IOException;

    List<String> getResultWithCard(MultipartFile file, String id) throws IOException;

    List<String> getResultWithSql(MultipartFile file) throws IOException;

    List<String> getResultWithRegex(MultipartFile file) throws IOException;

    Object convertToCsvForQuery(MultipartFile file);

    List<String> getResultWithStatement(MultipartFile file) throws IOException;

    List<String> getListWithStatement(MultipartFile file) throws IOException;

    Path wroteCsvFile(MultipartFile file) throws IOException;

	Object generateXlsxTable(MultipartFile file, String id) throws IOException;	
	
}
