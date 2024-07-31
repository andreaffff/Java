package com.example.java.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFTableColumn;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



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
                
                    if (line.length() > 12) { 
                        String partBefore = line.substring(0, 8);                       
                        String partAfter = line.substring(12);     
                        line = partBefore + partAfter;             
                    }
                    
                    String filteredLine = line.replaceAll("ainer : |tainer :|INFO\\s+db\\.DbConnector\\s+-\\s+|Parsing final sqlString >|DEBUG\\s+db\\.DbConnector\\s+-\\s+|INFO\\s+ejb\\.BaseSessionBean\\s+-\\s+|SQL to execute: ", "").trim();
                    
                    if (!filteredLine.isEmpty()) {
                        filteredLine = filteredLine.substring(0, filteredLine.length());
                        filteredLine = filteredLine.replaceAll("^(.{8})(.{5})(.{10})", "$1&$2&$3&");
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
    public Object convertToCsvForQuery(MultipartFile file) {
        if (file.isEmpty()) {
            return "Please select a file to upload";
        }
        if (!isValidFileFormat(file)) {
            throw new IllegalArgumentException("The file format is incorrect. Allowed format: .log");
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
        File convFile = new File(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    private File convertFileToCSV(File file, File directory) throws IOException {
        String csvFileName = file.getName().replaceFirst("[.][^.]+$", "") + ".csv";
        File csvFile = new File(directory, csvFileName);

        try (BufferedReader br = new BufferedReader(new FileReader(file));
             BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {

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
                        
                        filteredLine = filteredLine.substring(2, filteredLine.length() - 2);
                        filteredLine = filteredLine.replaceAll("^(.{13})(.4})(.{11})", "$1&$2&$3&");
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
    
    
    @Override
    public Object generateXlsxTable(MultipartFile file, String id) throws IOException {
        isValidFileFormat(file);

        String fileName = file.getOriginalFilename();
        String fileType = ".log";

        // execute query to get normal and statement query
        List<String> sql1 = getFileWithQuery(file, id);
        List<String> sql2 = getListWithStatement(file);

        // merge the results
        List<String> mergedOutput = new ArrayList<>(sql1);
        mergedOutput.addAll(sql2);

        // creting start file and save il in a directory called mergedFile located in the desktop
        Path logFilePath = createAndWriteLogFile("Desktop/mergedFile", fileName.concat(fileType), mergedOutput);

        //calling regex to prepare the csv final stucture
        List<String> sql3 = getResultWithRegex(new CustomMultipartFile(logFilePath.toFile()));

        // final file creation
        Path logToUplPath = createAndWriteLogFile("Desktop/fileToUpload", fileName, sql3);
        
        MultipartFile csvMultipartFile = new CustomMultipartFile(logToUplPath.toFile());

        // Convert the CSV to Excel and save it on the desktop
        Path excelFilePath = convertCsvToExcel(csvMultipartFile, "&");

        return "CSV and Excel files generated. Excel file saved at: " + excelFilePath.toString();
    }

    private Path createAndWriteLogFile(String dirPath, String fileName, List<String> content) throws IOException {
        Path filePath = Paths.get(System.getProperty("user.home"), dirPath, fileName);
        createDirectoryIfNotExists(filePath.getParent());
        Files.write(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("FIle created and wrote at: " + filePath);
        return filePath;
    }

    private void createDirectoryIfNotExists(Path dirPath) throws IOException {
        if (Files.notExists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }
    
    private Path convertCsvToExcel(MultipartFile csvFile, String delimiter) throws IOException {
    	 String fileName = csvFile.getOriginalFilename();
    	    String excelFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".xlsx";
    	    
    	    Path excelDirectoryPath = Paths.get(System.getProperty("user.home"), "Desktop/ExcelFiles");
    	    Path excelFilePath = excelDirectoryPath.resolve(excelFileName);

    	    createDirectoryIfNotExists(excelDirectoryPath);
    	    
    	    Workbook workbook = new XSSFWorkbook();
    	    Sheet sheet = workbook.createSheet("Data");

    	    // define header value
    	    String[] headers = {"time", "id", "type", "query"}; 

    	    try (BufferedReader br = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {
    	        String line;
    	        int rowNum = 0;

    	        //create header
    	        Row headerRow = sheet.createRow(rowNum++);
    	        for (int i = 0; i < headers.length; i++) {
    	            Cell cell = headerRow.createCell(i);
    	            cell.setCellValue(headers[i]);
    	            CellStyle style = workbook.createCellStyle();
    	            Font font = workbook.createFont();
    	            font.setBold(true);
    	            style.setFont(font);
    	            cell.setCellStyle(style);
    	        }

    	        // add row data
    	        while ((line = br.readLine()) != null) {
    	            String[] columns = line.split(delimiter);
    	            Row row = sheet.createRow(rowNum++);

    	            for (int i = 0; i < columns.length; i++) {
    	                Cell cell = row.createCell(i);
    	                cell.setCellValue(columns[i]);
    	            }
    	        }

    	        // Responsive column
    	        for (int i = 0; i < headers.length; i++) {
    	            sheet.autoSizeColumn(i);
    	        }

    	        // create table
    	        AreaReference reference = new AreaReference(
    	            new CellReference(0, 0),
    	            new CellReference(sheet.getLastRowNum(), headers.length - 1),
    	            workbook.getSpreadsheetVersion()
    	        );
    	        XSSFTable table = ((XSSFSheet) sheet).createTable(reference);

    	        // set table style
    	        table.setDisplayName("TableData");
    	        table.getCTTable().addNewTableStyleInfo().setName("TableStyleMedium2");

    	        // set table column
    	        for (int i = 0; i < headers.length; i++) {
    	            XSSFTableColumn column = table.getColumns().get(i);
    	            column.setName(headers[i]);
    	        }

    	        if (table.getCTTable().getAutoFilter() == null) {
    	            table.getCTTable().addNewAutoFilter();
    	        }
    	        table.getCTTable().getAutoFilter().setRef(reference.formatAsString());
    	    
    	    }

    	    try (FileOutputStream fileOut = new FileOutputStream(excelFilePath.toFile())) {
    	        workbook.write(fileOut);
    	    } finally {
    	        workbook.close();
    	    }

    	    System.out.println("Excel file created with table at: " + excelFilePath);

    	    return excelFilePath;
    }

	@Override
	public List<String> getErrors(MultipartFile file) throws IOException {

		// TODO Auto-generated method stub
		return null;
	}

		
	}

