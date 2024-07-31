package com.example.java.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.springframework.web.multipart.MultipartFile;

public class CustomMultipartFile implements MultipartFile {
    private final String name;
    private final String contentType;
    private final byte[] content;

    // Costruttore che accetta InputStream
    public CustomMultipartFile(String name, String contentType, InputStream inputStream) throws IOException {
        this.name = name;
        this.contentType = contentType;
        this.content = inputStream.readAllBytes();
    }

    // Costruttore che accetta File
    public CustomMultipartFile(File file) throws IOException {
        this.name = file.getName();
        this.contentType = Files.probeContentType(file.toPath());
        this.content = Files.readAllBytes(file.toPath());
    }

    // Costruttore che accetta array di byte
    public CustomMultipartFile(String name, String contentType, byte[] content) {
        this.name = name;
        this.contentType = contentType;
        this.content = content;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getOriginalFilename() {
        return this.name;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return this.content.length == 0;
    }

    @Override
    public long getSize() {
        return this.content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        Files.write(dest.toPath(), this.content);
    }
}
