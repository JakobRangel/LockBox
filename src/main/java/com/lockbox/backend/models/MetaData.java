package com.lockbox.backend.models;

import jakarta.persistence.*;

import java.util.Date;
/**
 * Entity representing metadata for files stored in the LockBox system.
 */
@Entity
@Table(name = "files", schema = "lockbox")
public class MetaData {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "link")
    private String link;

    @Column(name = "size")
    private long size;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "upload_date")
    private Date uploadDate;

    @Column(name = "extension")
    private String extension;

    @Column(name = "uploader_id")
    private int uploaderId;

    @Column(name = "is_encrypted")
    private boolean isEncrypted;

    @Column(name = "is_private")
    private boolean isPrivate;

    /**
     * Default constructor required by JPA.
     */
    public MetaData() {}

    // Getters and setters

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}
