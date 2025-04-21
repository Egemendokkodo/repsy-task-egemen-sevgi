package com.repsy_case.egemen_sevgi.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.repsy_case.egemen_sevgi.service.StorageService;
import com.repsy_case.egemen_sevgi.util.AppStrings;

@RestController
public class RepositoryController {

    private final StorageService storageService;

    
    public RepositoryController(StorageService storageService) {
        this.storageService = storageService;
    }

    // Deployment endpoint
    @PutMapping("/{packageName}/{version}")
    public ResponseEntity<?> deployPackage(
            @PathVariable String packageName,
            @PathVariable String version,
            @RequestParam(required = false) MultipartFile packageFile,
            @RequestParam(required = false) MultipartFile metaFile) {

        try {
            // is metaFile provided ?
            if (metaFile == null) {
                return ResponseEntity.badRequest().body(AppStrings.META_JSON_REQUIRED);
            }

            // packageFile is given?
            if (packageFile == null) {
                return ResponseEntity.badRequest().body(AppStrings.PACKAGE_REP_REQUIRED);
            }

            //packageFile name check
            String packageFileName = packageFile.getOriginalFilename();
            if (packageFileName == null || !packageFileName.endsWith(".rep")) {
                return ResponseEntity.badRequest().body(AppStrings.PACKAGE_FILE_MUST_BE_REP_EXT);
            }

            // metaFile name check
            String metaFileName = metaFile.getOriginalFilename();
            if (metaFileName == null || !metaFileName.equals("meta.json") || !metaFileName.endsWith(".json")) {
                return ResponseEntity.badRequest().body(AppStrings.META_FILE_MUST_BE_NAMED);
            }

            // store file
            storageService.storePackage(packageName, version, packageFile, metaFile);

            return ResponseEntity.ok().body("Package " + packageName + " v" + version + AppStrings.DEPLOYED_SUCCESSFULLY);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AppStrings.FAILED_TO_DEPLOY_PACKAGE + e.getMessage());
        }
    }

    // download endpoit here
    @GetMapping("/{packageName}/{version}/{fileName}")
    public ResponseEntity<?> downloadPackage(
            @PathVariable String packageName,
            @PathVariable String version,
            @PathVariable String fileName) {

        try {
            // get file from storage
            byte[] fileContent = storageService.retrieveFile(packageName, version, fileName);

            // Determine content type based on file extension
            String contentType = "application/octet-stream";
            if (fileName.endsWith(".json")) {
                contentType = "application/json";
            } else if (fileName.endsWith(".rep")) {
                contentType = "application/zip";
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + fileName)
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .body(fileContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AppStrings.FILE_NOT_FOUND + e.getMessage());
        }
    }
}
