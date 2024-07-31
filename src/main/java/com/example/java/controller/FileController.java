package com.example.java.controller;

import com.example.java.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.List;


@RestController
@RequestMapping("/api/file")

public class FileController {

    private FileService fileService;


    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/getResultFromQuery")

    public ResponseEntity<List<String>> processFile(@RequestParam("file") MultipartFile file,
                                                    @RequestParam(name = "id", required = false) String id) throws IOException {
        return ResponseEntity.ok(fileService.getFileWithQuery(file, id));
    }

    @PostMapping("/getResultFromCoobol")
    public ResponseEntity<List<String>> processFile(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(fileService.getFileWithCobol(file));
    }

    @PostMapping("/getResultWithDate")
    public ResponseEntity<List<String>> getResultWithDate(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(fileService.getResultWithDate(file));

    }

    @PostMapping("/getResultWithId")
    public ResponseEntity<List<String>> getResultWithId(@RequestParam("file") MultipartFile file,
                                                        @RequestParam(name = "id") String id) throws IOException {
        return ResponseEntity.ok(fileService.getResultWithId(file, id));
    }

    @PostMapping("/getResultWithCard")
    public ResponseEntity<List<String>> getResultWithCard(@RequestParam("file") MultipartFile file,
                                                          @RequestParam(name = "id") String id) throws IOException {
        return ResponseEntity.ok(fileService.getResultWithCard(file, id));

    }

    @PostMapping("/getResultWithSql")
    public ResponseEntity<List<String>> getResultWithSql(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(fileService.getResultWithSql(file));

    }

    @PostMapping("/getResultWithRegex")
    public ResponseEntity<List<String>> getResultWithRegex(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(fileService.getResultWithRegex(file));
    }

    @PostMapping("/uploadForQuery")
    public String handleFileUploadForQuery(@RequestParam("file") MultipartFile file) {

        return (String) fileService.convertToCsvForQuery(file);
    }
    @PostMapping("/uploadForStatement")
    public String handleFileUploadForStatement(@RequestParam("file") MultipartFile file) {

        return (String) fileService.convertToCsvForStatement(file);
    }

    @PostMapping("/getResultWithStatement")
    public ResponseEntity<List<String>> getResultWithStatement(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(fileService.getResultWithStatement(file));
    }

    @PostMapping("/getResultListStatement")
    public ResponseEntity<List<String>> getListWithStatement(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(fileService.getListWithStatement(file));
    }
}