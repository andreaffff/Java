package com.example.java.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.HashSet;


@Service
public class FileService implements com.example.java.service.FileService {


    @Override
    public HashSet<String> getFileWithQuery(MultipartFile file, String id) throws IOException {
        HashSet<String> linesWithId = new HashSet<>();
        HashSet<String> sqlQueries = new HashSet<>();

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

        return sqlQueries;

    }

    @Override
    public HashSet<String> getFileWithCobol(MultipartFile file) throws IOException {
        HashSet<String> coobol = new HashSet<>();

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
