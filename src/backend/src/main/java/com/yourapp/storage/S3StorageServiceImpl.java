package com.yourapp.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class S3StorageServiceImpl implements StorageService {
    @Override
    public String save(String filename, byte[] content, String mimeType) {
        // Placeholder for production S3 integration wiring.
        return "s3://bucket/" + filename;
    }
}
