package com.lockbox.backend.repositories;

public class FileRepository {

    public static String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex != -1) {
            return fileName.substring(lastIndex + 1);
        }
        return "";
    }
}
