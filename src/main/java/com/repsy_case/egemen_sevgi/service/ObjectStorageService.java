package com.repsy_case.egemen_sevgi.service;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class ObjectStorageService {

    @Value("${minio.endpoint}")
    private String minioEndpoint;
    
    @Value("${minio.access-key}")
    private String accessKey;
    
    @Value("${minio.secret-key}")
    private String secretKey;
    
    @Value("${minio.bucket}")
    private String bucket;
    
    private MinioClient getMinioClient() {
        return MinioClient.builder()
                .endpoint(minioEndpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
    
    public void store(String packageName, String version, String fileName, byte[] content) throws IOException {
        try {
            MinioClient minioClient = getMinioClient();
            
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
            
  
            String objectKey = packageName + "/" + version + "/" + fileName;
            
          
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .stream(new ByteArrayInputStream(content), content.length, -1)
                            .build()
            );
        } catch (Exception e) {
            throw new IOException("Failed to store file in object storage: " + e.getMessage(), e);
        }
    }
    
    public byte[] retrieve(String packageName, String version, String fileName) throws IOException {
        try {
            MinioClient minioClient = getMinioClient();
            

            String objectKey = packageName + "/" + version + "/" + fileName;
            

            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .build()
            );
            
            // Read object content
            return response.readAllBytes();
        } catch (Exception e) {
            throw new IOException("Failed to retrieve file from object storage: " + e.getMessage(), e);
        }
    }
}