package com.kinny.manager.controller;

import com.kinny.util.FastDfsUtil;
import com.kinny.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author qgy
 * @create 2019/5/27 - 11:01
 */
@RestController
public class UploadController {


    @Value("${fileServer}")
    private String fileServer;



    @RequestMapping("/upload")
    public ResponseVo upload(MultipartFile multipartFile) {

        String originalFilename = multipartFile.getOriginalFilename();

        String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

        byte[] bytes = new byte[0];
        try {
            bytes = multipartFile.getBytes();
        } catch (IOException e) {
            return new ResponseVo(false, "文件上传失败");
        }
        String fileId = FastDfsUtil.upload(bytes, extName);

        System.out.println(this.fileServer + fileId);


        return new ResponseVo(true, this.fileServer + fileId);
    }



}
