package com.example.java.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface FileService {

    List<String> getFileWithQuery(MultipartFile file, String id) throws IOException;

    List<String> getFileWithCobol(MultipartFile file) throws IOException;

    List<String> getResultWithDate(MultipartFile file) throws IOException;

    List<String> getResultWithId(MultipartFile file, String id) throws IOException;

    List<String> getResultWithCard(MultipartFile file, String id) throws IOException;

    List<String> getResultWithSql(MultipartFile file) throws IOException;

    List<String> getResultWithRegex(MultipartFile file) throws IOException;

}
