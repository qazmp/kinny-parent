package com.kinny.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.common.exception.DbException;
import com.kinny.content.service.ContentCategoryService;
import com.kinny.pojo.TbContentCategory;
import com.kinny.vo.ResponseVo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/25 - 9:58
 */
@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {


    @Reference
    private ContentCategoryService contentCategoryService;


    @RequestMapping("/findAll")
    public ResponseVo<List<TbContentCategory>> findAll() {
        List<TbContentCategory> all = null;
        try {
            all = this.contentCategoryService.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "广告分类列表查询失败");
        }

        return new ResponseVo<>(true, all);
    }


    @RequestMapping("/findPage")
    public ResponseVo<Map<String, Object>> findPage(
            @RequestParam(required = true) String pageIndex,
            @RequestParam(required = true) String pageSize,
            @RequestBody(required = true)TbContentCategory contentCategory
            ) {

        Map<String, Object> map = new HashMap<>();
        map.put("pageIndex", pageIndex);
        map.put("pageSize", pageSize);
        map.put("contentCategory", contentCategory);
        Map<String, Object> page = null;
        try {
            page = this.contentCategoryService.findPage(map);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "广告分类列表查询失败");
        }

        return new ResponseVo<>(true, page);
    }

    @RequestMapping("/findOne")
    public ResponseVo<TbContentCategory> findOne(@RequestParam(required = true) String id) {

        try {
            TbContentCategory one = this.contentCategoryService.findOne(id);
            return new ResponseVo<>(true, one);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "广告分类查询失败");
        }

    }


    @RequestMapping("/save")
    public ResponseVo save(
            @RequestBody(required = true) TbContentCategory contentCategory
    ) {

        try {
            this.contentCategoryService.save(contentCategory);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        } catch (DbException e) {
            return new ResponseVo<>(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "广告分类新增失败");
        }

        return new ResponseVo<>(true, "广告分类新增成功");
    }

    @RequestMapping("/delete")
    public ResponseVo delete(
            @RequestParam(value = "id", required = true) String idSplit
    ) {

        // 字符串分割
        String[] split = idSplit.split("-");
        try {
            this.contentCategoryService.batchDelete(split);
        }  catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        } catch (DbException e) {
            return new ResponseVo<>(false, e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "广告分类删除失败");
        }


        return new ResponseVo<>(true, "广告分类删除成功");
    }



}
