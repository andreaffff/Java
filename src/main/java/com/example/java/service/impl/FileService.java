package com.example.java.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import java.util.ArrayList;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
                    if (id == null || line.contains(id)) {
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

        return sqlQueries.stream().sorted().collect(Collectors.toList());

    }

    @Override
    public List<String> getFileWithCobol(MultipartFile file) throws IOException {
        List<String> coobol = new ArrayList<>();

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
        return coobol.stream().collect(Collectors.toList());
    }

    @Override
    public List<String> getResultWithDate(MultipartFile file) throws IOException {
        List<String> dates = new ArrayList<>();


        if (!isValidFileFormat(file)) {
            throw new IllegalArgumentException("The file format is incorrect. Allowed format: .log");
        }

        if (!file.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                Pattern pattern = Pattern.compile("\\b\\d{2}:\\d{2}:\\d{2},\\d{3}\\b"); // Regex pattern for date format
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String time = line.substring(0, 12);
                        dates.add(time);
                    }
                }
            }
        } else {
            throw new RuntimeException("The file cannot be empty");
        }

        return dates.stream().collect(Collectors.toList());
    }

    @Override
    public List<String> getResultWithId(MultipartFile file, String id) throws IOException {
        List<String> listWithId = new ArrayList<>();

        if (!isValidFileFormat(file)) {
            throw new IllegalArgumentException("The file format is incorrect. Allowed format: .log");
        }

        if (!file.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.contains(id)) {
                        listWithId.add(id);

                    }
                }
            }
        } else {
            throw new RuntimeException("The file cannot be empty");
        }

        return listWithId;
    }

    @Override
    public List<String> getResultWithCard(MultipartFile file, String id) throws IOException {
        List<String> listWithCard = new ArrayList<>();


        if (!isValidFileFormat(file)) {
            throw new IllegalArgumentException("The file format is incorrect. Allowed format: .log");
        }

        if (!file.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.contains(id)) {
                        listWithCard.add(id);

                    }
                }
            }
        } else {
            throw new RuntimeException("The file cannot be empty");
        }

        return listWithCard;
    }

    @Override
    public List<String> getResultWithSql(MultipartFile file) {
        List<String> sqlFragments = new ArrayList<>();

        if (!isValidFileFormat(file)) {
            throw new IllegalArgumentException("The file format is incorrect. Allowed format: .log");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Parsing final sqlString >")) {
                    if (sqlBuilder.length() > 0) {
                        sqlFragments.add(sqlBuilder.toString().trim());
                        sqlBuilder.setLength(0);
                    }
                    sqlBuilder.append(line.substring(line.indexOf("Parsing final sqlString >") + "Parsing final sqlString >".length()).trim()).append("\n");
                } else if (!line.trim().isEmpty() && sqlBuilder.length() > 0) {

                    sqlBuilder.append(line.trim()).append("\n");
                }
            }

            if (sqlBuilder.length() > 0) {
                sqlFragments.add(sqlBuilder.toString().trim());
            }
        } catch (Exception e) {

            throw new RuntimeException("Error reading log file", e);
        }

        return sqlFragments.stream().collect(Collectors.toList());
    }

    @Override
    public List<String> getResultWithRegex(MultipartFile file) throws IOException {
        List<String> listWithoutInfo = new ArrayList<>();

        if (!isValidFileFormat(file)) {
            throw new IllegalArgumentException("The file format is incorrect. Allowed format: .log");
        }

        if (!file.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    String filteredLine = line.replaceAll("ainer : |tainer :|INFO\\s+db\\.DbConnector\\s+-\\s+|Parsing final sqlString >|DEBUG\\s+db\\.DbConnector\\s+-\\s+|INFO\\s+ejb\\.BaseSessionBean\\s+-\\s+|SQL to execute: ", "").trim();

                    if (!filteredLine.isEmpty()) {
                        filteredLine = filteredLine.substring(1, filteredLine.length() - 2);
                        filteredLine = filteredLine.replaceAll("^(.{13})(.{4})(.{11})", "$1&$2&$3&");
                        listWithoutInfo.add(filteredLine);
                    }
                }
            }
        } else {
            throw new RuntimeException("The file cannot be empty");
        }

        return listWithoutInfo;
    }

    @Override
    public Object convertToCsv(MultipartFile file) {
        if (file.isEmpty()) {
            return "Please select a file to upload";
        }

        try {
            File convertedFile = convertMultiPartToFile(file);
            File desktopDirectory = new File(System.getProperty("user.home"), "Desktop/ConvertedFiles");
            if (!desktopDirectory.exists()) {
                desktopDirectory.mkdirs();
            }
            File csvFile = convertFileToCSV(convertedFile, desktopDirectory);

            return "The file is converted to CSV format and saved to the desktop: " + csvFile.getAbsolutePath();
        } catch (IOException e) {
            return "An error occurred while converting the file: " + e.getMessage();
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    private File convertFileToCSV(File file, File directory) throws IOException {
        String uniqueFileName = UUID.randomUUID() + ".csv";
        File csvFile = new File(directory, uniqueFileName);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile));
             BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {

                bw.write(line);
                bw.newLine();
            }
        }
        return csvFile;
    }

    @Override
    public List<String> getResultWithStatement(MultipartFile file) throws IOException {
        List<String> listStatement = new ArrayList<>();

        if (!isValidFileFormat(file)) {
            throw new IllegalArgumentException("The file format is incorrect. Allowed format: .log");
        }

        if (!file.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    String filteredLine = line.replaceAll("ainer : |tainer :|INFO\\s+db\\.DbConnector\\s+-\\s+|SQL to execute:", "").trim();

                    if (!filteredLine.isEmpty()) {
                        filteredLine = filteredLine.substring(1, filteredLine.length() - 2);
                        filteredLine = filteredLine.replaceAll("^(.{13})(.{4})(.{11})", "$1&$2&$3&");
                        listStatement.add(filteredLine);
                    }
                }
            }
        } else {
            throw new RuntimeException("The file cannot be empty");
        }

        return listStatement;
    }

    @Override
    public List<String> getListWithStatement(MultipartFile file) throws IOException {
        List<String> listStatement = new ArrayList<>();

        if (!isValidFileFormat(file)) {
            throw new IllegalArgumentException("The file format is incorrect. Allowed format: .log");
        }
        if (!file.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Statement")) {
                        listStatement.add(line);
                    }
                }
            }
        } else throw new RuntimeException("The file cannot be empty");
        return listStatement.stream().collect(Collectors.toList());
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
