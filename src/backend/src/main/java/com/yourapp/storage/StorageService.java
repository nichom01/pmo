package com.yourapp.storage;

public interface StorageService {
    String save(String filename, byte[] content, String mimeType);
}
