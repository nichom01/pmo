package com.yourapp.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Profile("dev")
public class LocalStorageServiceImpl implements StorageService {
    @Override
    public String save(String filename, byte[] content, String mimeType) {
        try {
            Path dir = Path.of("storage");
            Files.createDirectories(dir);
            String storedName = UUID.randomUUID() + "-" + filename;
            Path target = dir.resolve(storedName);
            Files.write(target, content);
            return target.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to write file to local storage", ex);
        }
    }
}
