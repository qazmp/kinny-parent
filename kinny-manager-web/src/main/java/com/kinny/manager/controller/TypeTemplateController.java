package com.kinny.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.pojo.TbTypeTemplate;
import com.kinny.sellergoods.service.TypeTemplateService;
import com.kinny.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/7 - 9:49
 */
@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {


    @Reference
    private TypeTemplateService typeTemplateService;




    @RequestMapping("/findAll")
    public ResponseVo<List<TbTypeTemplate>> findAll() {

        try {
            List<TbTypeTemplate> typeTemplateList = this.typeTemplateService.findAll();
            return new ResponseVo<>(true, typeTemplateList);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "查询全部类型模板失败");
        }

    }

    @RequestMapping("/findPage")
    public ResponseVo<Map<String, Object>> findPage(
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") String pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") String pageSize,
            @RequestParam(value = "name", required = false) String name) {

        Map<String, Object> param = new HashMap<>();
        param.put("pageIndex", pageIndex);
        param.put("pageSize", pageSize);
        param.put("name", name);

        try {
            Map<String, Object> page = this.typeTemplateService.findPage(param);
            return new ResponseVo<>(true, page);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "查询类型模板失败");
        }

    }

    @RequestMapping("/findOne")
    public ResponseVo<TbTypeTemplate> findOne(@RequestParam(value = "id", required = true) String id) {

        TbTypeTemplate typeTemplate = null;

        try {
            typeTemplate = this.typeTemplateService.findOne(id);
        }catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "查询类型模板失败");
        }
        return new ResponseVo<>(true, typeTemplate);
    }

    @RequestMapping("/save.do")
    public ResponseVo save(@Valid @RequestBody(required = true) TbTypeTemplate typeTemplate, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            String s = "";
            for (FieldError fieldError : fieldErrors) {
                s += fieldError.getDefaultMessage() + ";";
            }
            return new ResponseVo(false, s);
        }

        try {
            this.typeTemplateService.save(typeTemplate);
        } catch (RuntimeException e) {
            return new ResponseVo(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "模板保存失败");
        }

        return new ResponseVo(true, "模板保存成功");
    }


    @RequestMapping("/delete")
    public ResponseVo delete(@RequestParam(value = "id", required = true) String idSplit) {


        try {
            this.typeTemplateService.batchDelete(idSplit.split("-"));
        } catch (RuntimeException e) {
            return new ResponseVo(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "模板删除失败");
        }

        return new ResponseVo(true, "模板删除成功");
    }


}
