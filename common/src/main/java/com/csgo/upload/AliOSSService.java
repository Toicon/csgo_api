package com.csgo.upload;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.csgo.support.BusinessException;
import com.csgo.support.ExceptionCode;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author admin
 * @since 2018/6/18
 */
public class AliOSSService implements ObjectStorageService<AliOSSConfig> {

    private static Logger logger = LoggerFactory.getLogger(AliOSSService.class);

    private static final String HTTPS_SCHEMA = "https://";
    private static final String RESOURCE_SEPARATOR = "/";
    private static final String LINE_SEPARATOR = "_";

    private final AliOSSConfig aliOSSConfig;

    public AliOSSService(AliOSSConfig aliOSSConfig) {
        this.aliOSSConfig = aliOSSConfig;
    }

    public void createBucket(OSSClient ossClient, String bucketName) {
        ossClient.createBucket(bucketName);
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
        createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
        ossClient.createBucket(createBucketRequest);
    }

    @Override
    public String upload(MultipartFile file, String fileName) {
        try (AutoCloseOSSClient ossClient = new AutoCloseOSSClient(aliOSSConfig.getEndpoint(), aliOSSConfig.getAccessKeyId(), aliOSSConfig.getAccessKeySecret())) {
            String bucketName = aliOSSConfig.getBucketName();
            if (!ossClient.doesBucketExist(bucketName)) {
                logger.info("create bucket while no exist, bucket-name:{}.", bucketName);
                this.createBucket(ossClient, bucketName);
            }
            String key = RandomStringUtils.randomAlphabetic(10) + LINE_SEPARATOR + fileName;
            String directory = aliOSSConfig.getDirectory();
            if (StringUtils.hasText(directory)) {
                key = directory + "/" + key;
            }
            logger.info("Uploading a new object to OSS from a file, file-name:{}, resource-key:{}.", fileName, key);

            PutObjectRequest put = new PutObjectRequest(bucketName, key, file.getInputStream());
            if (directory.contains("saas/download")) {
                ObjectMetadata om = new ObjectMetadata();
                om.setContentDisposition("attachment;filename=\"" + fileName + "\"");
                put.setMetadata(om);
            }

            ossClient.putObject(put);

            //Determine whether an object residents in your bucket
            boolean exists = ossClient.doesObjectExist(bucketName, key);
            if (exists) {
                // set object readable permission
                ossClient.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
            }
            return HTTPS_SCHEMA + aliOSSConfig.getPublicEndpoint() + "/" + key;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new BusinessException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String filePath(String key) {
        StringBuilder builder = new StringBuilder();
        builder.append(HTTPS_SCHEMA)
                .append(aliOSSConfig.getBucketName())
                .append(Constants.DOT)
                .append(aliOSSConfig.getPublicEndpoint())
                .append(RESOURCE_SEPARATOR).append(key);
        return builder.toString();
    }

    @Override
    public byte[] getResource(String key, String directory) {
        try (AutoCloseOSSClient ossClient = new AutoCloseOSSClient(aliOSSConfig.getEndpoint(), aliOSSConfig.getAccessKeyId(), aliOSSConfig.getAccessKeySecret());) {
            String realKey = key;
            String bucketName = aliOSSConfig.getBucketName();
            if (!ossClient.doesBucketExist(bucketName)) {
                throw new OSSIOException("The specified bucket does not exist.");
            }
            if (StringUtils.hasText(directory)) {
                realKey = directory + realKey;
            }
            logger.info("Loading resource from OSS resource-key:{}.", realKey);
            OSSObject object = ossClient.getObject(new GetObjectRequest(bucketName, realKey));
            if (null == object) {
                throw new OSSIOException("The specified resource does not exist at cloud, resource-key:" + realKey);
            }
            return IOUtils.toByteArray(object.getObjectContent());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new BusinessException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        // 创建OSSClient实例。
        OSS ossClient = new AutoCloseOSSClient(aliOSSConfig.getEndpoint(), aliOSSConfig.getAccessKeyId(), aliOSSConfig.getAccessKeySecret());
        //获取key
        String suffixPath = suffixPath(filePath);
        if (ossClient.doesObjectExist(aliOSSConfig.getBucketName(), suffixPath)) {
            // 删除文件。如需删除文件夹，请将ObjectName设置为对应的文件夹名称。如果文件夹非空，则需要将文件夹下的所有object删除后才能删除该文件夹。
            ossClient.deleteObject(aliOSSConfig.getBucketName(), suffixPath);
        }
        // 关闭OSSClient。
        ossClient.shutdown();
    }

    private String suffixPath(String filePath) {
        StringBuilder builder = new StringBuilder();
        builder.append(HTTPS_SCHEMA)
                .append(aliOSSConfig.getBucketName())
                .append(Constants.DOT)
                .append(aliOSSConfig.getPublicEndpoint())
                .append(RESOURCE_SEPARATOR);
        return filePath.replace(builder.toString(), "");
    }
}
