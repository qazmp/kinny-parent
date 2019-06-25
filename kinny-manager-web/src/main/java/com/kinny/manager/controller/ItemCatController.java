package com.kinny.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.common.exception.DbException;
import com.kinny.pojo.TbItemCat;
import com.kinny.sellergoods.service.ItemCatService;
import com.kinny.vo.ResponseVo;
import org.apache.zookeeper.ZooDefs;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author qgy
 * @create 2019/5/13 - 10:48
 */
@RestController
@RequestMapping("/itemCat")
public class ItemCatController {


    @Reference
    private ItemCatService itemCatService;


    @RequestMapping("/findAll")
    public ResponseVo<List<TbItemCat>> findAll() {

        try {
            List<TbItemCat> cats = this.itemCatService.findAll();
            return new ResponseVo<>(true, cats);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "查询分类失败");
        }

    }

    @RequestMapping("/findByParentId")
    public ResponseVo<List<TbItemCat>> findByParentId(@RequestParam(required = true) String parentId) {

        List<TbItemCat> tbItemCats = null;

        try {
            tbItemCats = this.itemCatService.findByParentId(parentId);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "根据父分类查询分类失败");
        }


        return new ResponseVo<>(true, tbItemCats);
    }

    @RequestMapping("/findOne")
    public ResponseVo<TbItemCat> findOne(@RequestParam(required = true) String id) {

        try {
            TbItemCat one = this.itemCatService.findOne(id);
            return  new ResponseVo<>(true, one);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "根据主键查询分类失败");
        }

    }

    @RequestMapping("/save")
    public ResponseVo save(@RequestBody(required = true) @Valid TbItemCat itemCat, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            StringBuilder stringBuilder = new StringBuilder();
            for (FieldError fieldError : fieldErrors) {
                stringBuilder.append(fieldError.getDefaultMessage());
                stringBuilder.append(";");
            }
            return new ResponseVo(false, stringBuilder.toString());
        }

        try {
            this.itemCatService.save(itemCat);
        } catch (IllegalArgumentException e) {
            return new ResponseVo(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "保存分类失败");
        }

        return new ResponseVo(true, "保存分类成功");
    }

    @RequestMapping("/batchDelete")
    public  ResponseVo batchDelete(@RequestParam(value = "ids", required = true) String idSpilt) {

        String[] split = idSpilt.split("-");
        try {
            this.itemCatService.batchDelete(split);
        } catch (IllegalArgumentException e) {
            return new ResponseVo(false, e.getMessage());
        } catch (DbException e) {
            return new ResponseVo(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "删除分类失败");
        }

        return new ResponseVo(true, "删除分类成功");
    }


}
