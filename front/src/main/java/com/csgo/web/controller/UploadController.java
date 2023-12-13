package com.csgo.web.controller;

import com.csgo.autoconfigure.AliGreenProperty;
import com.csgo.exception.ServiceErrorException;
import com.csgo.support.Result;
import com.csgo.upload.ObjectStorageService;
import com.csgo.util.ZcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/ali/upload")
public class UploadController {

    @Resource(name = "storageService")
    private ObjectStorageService aliOSSService;
    @Autowired
    private AliGreenProperty aliGreenProperty;

    /**
     * 上传文件
     **/
    @PostMapping("/file")
    public Result uploadFile(MultipartFile file) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        // 生成文件名称
        String nameSuffix = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."))
                .replaceAll(" ", "_").replaceAll(",", "") + format.format(new Date())
                + new Random().nextInt(1000);
        String imgUrl = aliOSSService.upload(file, nameSuffix);
        //判断图片是否合法
        try {
            boolean isOk = ZcUtils.checkImage(aliGreenProperty.getAccessKey(), aliGreenProperty.getSecretKey(), imgUrl);
            if (!isOk) {
                throw new Exception("上传图片内容不合法");
            }
        } catch (Exception ex) {
            throw new ServiceErrorException(ex.getMessage());
        }
        //上传原始图片到阿里云
        return new Result().result(imgUrl);
    }
}
