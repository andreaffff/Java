package com.example.java.controller;

import com.example.java.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;


@RestController
@RequestMapping("/api/file")
public class FileController {

    private FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/getResultFromQuery")
    public ResponseEntity<HashSet<String>> processFile(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("id") String id) throws IOException {
        return ResponseEntity.ok(fileService.getFileWithQuery(file, id));
    }

    @PostMapping("/getResultFromCoobol")
    public ResponseEntity<HashSet<String>> processFile(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(fileService.getFileWithCobol(file));
    }

}
