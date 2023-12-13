package com.csgo.web.controller;

import com.csgo.support.Result;
import com.csgo.upload.ObjectStorageService;
import com.csgo.web.support.Log;
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
public class AdminUploadController {

    @Resource(name = "storageService")
    private ObjectStorageService aliOSSService;

    /**
     * 上传文件
     **/
    @PostMapping("/file")
    @Log(desc = "上传附件")
    public Result uploadFile(MultipartFile file) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        // 生成文件名称
        String nameSuffix = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."))
                .replaceAll(" ", "_").replaceAll(",", "") + format.format(new Date())
                + new Random().nextInt(1000);
        //上传原始图片到阿里云
        String uploadPath = aliOSSService.upload(file, nameSuffix);
        return new Result().result(uploadPath);
    }
}
