package com.repsy_case.egemen_sevgi.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileSystemStorageService {

    @Value("${filesystem.storage.path}")
    private String storagePath;
    
    public void store(String packageName, String version, String fileName, byte[] content) throws IOException {
        Path dirPath = Paths.get(storagePath, packageName, version);
        Files.createDirectories(dirPath);
        
        Path filePath = dirPath.resolve(fileName);
        Files.write(filePath, content);
    }
    
    public byte[] retrieve(String packageName, String version, String fileName) throws IOException {
        Path filePath = Paths.get(storagePath, packageName, version, fileName);
        
        if (!Files.exists(filePath)) {
            throw new IOException("File " + fileName + " not found for package " + packageName + " v" + version);
        }
        
        return Files.readAllBytes(filePath);
    }
}