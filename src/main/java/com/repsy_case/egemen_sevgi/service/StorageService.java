package com.repsy_case.egemen_sevgi.service;

import java.io.IOException;
import java.util.Date;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repsy_case.egemen_sevgi.dto.MetaData;
import com.repsy_case.egemen_sevgi.dto.MetaData.Dependency;
import com.repsy_case.egemen_sevgi.entity.DependencyEntity;
import com.repsy_case.egemen_sevgi.entity.PackageEntity;
import com.repsy_case.egemen_sevgi.repository.PackageEntityRepository;


@Service
public class StorageService {

    private final FileSystemStorageService fileSystemStorageService;
    private final ObjectStorageService objectStorageService;
    private final PackageEntityRepository packageRepository;
    private final ObjectMapper objectMapper;

    @Value("${storageStrategy}")
    private String storageStrategy;

    public StorageService(
        FileSystemStorageService fileSystemStorageService,
        ObjectStorageService objectStorageService,
        PackageEntityRepository packageRepository,
        ObjectMapper objectMapper
    ) {
        this.fileSystemStorageService = fileSystemStorageService;
        this.objectStorageService = objectStorageService;
        this.packageRepository = packageRepository;
        this.objectMapper = objectMapper;
    }

    public void storePackage(String packageName, String version, MultipartFile packageFile, MultipartFile metaFile) throws IOException {
        MetaData metaData = objectMapper.readValue(metaFile.getInputStream(), MetaData.class);

        if (!metaData.getName().equals(packageName)) {
            throw new IllegalArgumentException("Package name in meta.json doesn't match URL path");
        }

        if (!metaData.getVersion().equals(version)) {
            throw new IllegalArgumentException("Version in meta.json doesn't match URL path");
        }

        PackageEntity pkg = new PackageEntity();
        pkg.setName(packageName);
        pkg.setVersion(version);
        pkg.setAuthor(metaData.getAuthor());
        pkg.setUploadDate(new Date());
         if (metaData.getDependencies() != null) {
        for (MetaData.Dependency depData : metaData.getDependencies()) {
            DependencyEntity dep = new DependencyEntity();
            dep.setPackageName(depData.getPackage());
            dep.setVersion(depData.getVersion());
            pkg.getDependencies().add(dep); 
        }
    }
        packageRepository.save(pkg);

        if ("file-system".equals(storageStrategy)) {
            fileSystemStorageService.store(packageName, version, "package.rep", packageFile.getBytes());
            fileSystemStorageService.store(packageName, version, "meta.json", metaFile.getBytes());
        } else if ("object-storage".equals(storageStrategy)) {
            objectStorageService.store(packageName, version, "package.rep", packageFile.getBytes());
            objectStorageService.store(packageName, version, "meta.json", metaFile.getBytes());
        } else {
            throw new IllegalStateException("Invalid storage strategy: " + storageStrategy);
        }
    }

    public byte[] retrieveFile(String packageName, String version, String fileName) throws IOException {
        if (!packageRepository.existsByNameAndVersion(packageName, version)) {
            throw new IOException("Package " + packageName + " v" + version + " not found");
        }

        if ("file-system".equals(storageStrategy)) {
            return fileSystemStorageService.retrieve(packageName, version, fileName);
        } else if ("object-storage".equals(storageStrategy)) {
            return objectStorageService.retrieve(packageName, version, fileName);
        } else {
            throw new IllegalStateException("Invalid storage strategy: " + storageStrategy);
        }
    }
}
