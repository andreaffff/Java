package com.example.java.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;

import java.util.List;


@Service
public class FileService implements com.example.java.service.FileService {


    @Override
    public List<String> getFileWithQuery(MultipartFile file, String id) throws IOException {
        List<String> linesWithId = new ArrayList<>();
        List<String> sqlQueries = new ArrayList<>();

        // check about the format of the file
        if (!isValidFileFormat(file)) {
            throw new IllegalArgumentException("The file format is incorrect. Allowed format: .log");
        }
        if (!file.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(id)) {
                        linesWithId.add(line);
                    }
                }
            }
        } else throw new RuntimeException("The file cannot be empty");
        // Iterate through the list of rows with the given id
        for (String line : linesWithId) {
            if (line.contains("Parsing final sqlString")) {
                sqlQueries.add(line);
            }
        }

        return sqlQueries.stream().sorted().toList();

    }

    @Override
    public List<String> getFileWithCobol(MultipartFile file) throws IOException {
        List<String> coobol = new ArrayList<>();

        // check about the format of the file
        if (!isValidFileFormat(file)) {
            throw new IllegalArgumentException("The file format is incorrect. Allowed format: .log");
        }
        if (!file.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Object(1)")) {
                        coobol.add(line);
                    }
                }
            }
        } else throw new RuntimeException("The file cannot be empty");
        return coobol;
    }


    public static boolean isValidFileFormat(MultipartFile file) {
        if (file == null) {
            return false;
        }
        // Checks if the file has a .log extension
        String fileName = file.getOriginalFilename();
        return fileName != null && fileName.toLowerCase().endsWith(".log");
    }

}
