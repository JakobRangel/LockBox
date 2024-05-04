package com.lockbox.backend.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(schema = "lockbox", name="files")
public class MetaData {

    @Id
    @Column(length = 100)
    private String fileName;

    private String link;

    private long size;

    private Date uploadDate;

    private String extension;

    private int uploaderId;

    public MetaData() {
        // Default constructor required by JPA
    }

    // Getters and setters

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public int getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(int uploaderId) {
        this.uploaderId = uploaderId;
    }
}
