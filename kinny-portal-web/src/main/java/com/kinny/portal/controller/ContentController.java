package com.kinny.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.content.service.ContentService;
import com.kinny.pojo.TbContent;
import com.kinny.vo.ResponseVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qgy
 * @create 2019/5/31 - 20:11
 */
@RequestMapping("/content") // 交由服务器解析的路径 '/'代表项目应用的根路径
@RestController
public class ContentController {


    @Reference
    private ContentService contentService;


    @RequestMapping("/findByCategoryId")
    public ResponseVo<List<TbContent>> findAll(String categoryId) {

        List<TbContent> all = null;

        try {
            all = this.contentService.findByCategoryId(categoryId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "广告列表查询失败");
        }


        return new ResponseVo<>(true, all);
    }


}
