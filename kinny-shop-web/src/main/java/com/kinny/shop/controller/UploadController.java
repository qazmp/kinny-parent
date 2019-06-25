package com.kinny.shop.controller;

import com.kinny.util.FastDfsUtil;
import com.kinny.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author qgy
 * @create 2019/5/16 - 20:06
 */
@RestController
public class UploadController {

    @Value("${fileService.url}")
    private String fileServiceUrl;


    @RequestMapping("/upload")
    public ResponseVo upload(MultipartFile multipartFile) {

        String ext = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);

        try {
            String fileId = FastDfsUtil.upload(multipartFile.getBytes(), ext);
            return new ResponseVo(true, this.fileServiceUrl + fileId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "文件上传失败");
        }

    }

}
