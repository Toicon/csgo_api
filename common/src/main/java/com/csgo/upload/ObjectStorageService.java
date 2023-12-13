package com.csgo.upload;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author admin
 * @since 2018/6/18
 */
public interface ObjectStorageService<T extends StorageConfig> {

    String upload(MultipartFile file, String fileName);

    String filePath(String key);

    byte[] getResource(String key, String directory);

    /**
     * 文件路径
     */
    void deleteFile(String filePath);
}
