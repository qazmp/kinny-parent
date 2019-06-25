package com.kinny.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.common.exception.DbException;
import com.kinny.common.pojo.em.ContentCategoryEnum;
import com.kinny.content.service.ContentService;
import com.kinny.pojo.TbContent;
import com.kinny.vo.ResponseVo;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/25 - 9:56
 */
@RestController
@RequestMapping("/content")
public class ContentController {


    @Reference
    private ContentService contentService;


    @RequestMapping("/findPage")
    public ResponseVo<Map<String, Object>> findPage(
            @RequestParam(required = true) String pageIndex,
            @RequestParam(required = true) String pageSize
    ) {


        Map<String, Object> map = new HashMap<>();
        map.put("pageIndex", pageIndex);
        map.put("pageSize", pageSize);
        Map<String, Object> page = null;
        try {
            page = this.contentService.findPage(map);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "广告列表查询失败");
        }

        return new ResponseVo<>(true, page);

    }



    @RequestMapping("/findOne")
    public ResponseVo<TbContent> findOne(String id) {

        TbContent one = null;

        try {
            one = this.contentService.findOne(id);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "广告查询失败");
        }

        return new ResponseVo<>(true, one);
    }

    @RequestMapping("/save")
    public ResponseVo save(
           @RequestBody(required = true) @Valid TbContent content,
           BindingResult result
    ) {

        if (result.hasErrors()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (FieldError fieldError : result.getFieldErrors()) {
                stringBuilder.append(fieldError.getDefaultMessage());
                stringBuilder.append(";");
            }

            return new ResponseVo(false, stringBuilder.toString());
        }


        try {
            this.contentService.save(content);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        } catch (DbException e) {
            return new ResponseVo<>(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "广告插入失败");
        }

        return new ResponseVo<>(true, "广告插入成功");
    }

    @RequestMapping("/delete")
    public ResponseVo delete(String ids) {

        String[] split = ids.split("-");

        try {
            this.contentService.delete(split);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        } catch (DbException e) {
            return new ResponseVo<>(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "广告删除失败");
        }

        return new ResponseVo<>(true, "广告删除成功");
    }





}
