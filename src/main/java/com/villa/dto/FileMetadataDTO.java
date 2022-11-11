package com.villa.dto;

public class FileMetadataDTO {
    /** 文件的md5值 */
    private String name;
    /** 文件访问url */
    private String url;
    /** 文件类型 */
    private String type;
    private String mimeType;
    private String suffix;
    /** 文件大小 单位字节 */
    private Long size;

    public FileMetadataDTO() {
    }

    public FileMetadataDTO(String name, String url, String type, String mimeType, String suffix, Long size) {
        this.name = name;
        this.url = url;
        this.type = type;
        this.mimeType = mimeType;
        this.suffix = suffix;
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
