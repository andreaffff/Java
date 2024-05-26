package com.example.java.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;


public interface FileService {

    HashSet<String> getFileWithQuery(MultipartFile file, String id) throws IOException;

    HashSet<String> getFileWithCobol(MultipartFile file) throws IOException;
}
