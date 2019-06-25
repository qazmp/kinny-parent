package com.kinny.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.pojo.TbBrand;
import com.kinny.sellergoods.service.BrandService;
import com.kinny.vo.ResponseVo;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/2 - 16:33
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;


    @RequestMapping("/findAll")
    public List<TbBrand> findAll(TbBrand tbBrand) {
        return this.brandService.findAll();
    }

    @RequestMapping("/findPage")
    public ResponseVo<Map<String, Object>> findPage(@RequestParam(value = "pageIndex", required = false, defaultValue = "1") String pageIndex,
                               @RequestParam(value = "pageSize", required = false, defaultValue = "10") String pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("pageIndex", pageIndex);
        map.put("pageSize", pageSize);
        Map<String, Object> page = null;
        try {
            page = this.brandService.findPage(map);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        }
        catch (Exception e) {
            return new ResponseVo<>(false, "获取数据失败");
        }

        return new ResponseVo<>(true, page);
    }

    @RequestMapping("/findOne")
    public ResponseVo<TbBrand> findOne(@RequestParam(value = "id", required = true) String id) {

        TbBrand brand = null;
        try {
            brand = this.brandService.findOne(id);

        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "查询单个品牌失败");
        }

        return new ResponseVo<>(true, brand);
    }

    /**
     *  新增或者修改品牌
     * @param brand
     * @param bindingResult 数据校验结果
     * @return
     */
    @RequestMapping("/save")
    public ResponseVo save(@Valid @RequestBody(required = true) TbBrand brand, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            StringBuilder errors = new StringBuilder();
            for (FieldError fieldError : fieldErrors) {
                errors.append(fieldError.getDefaultMessage());
                errors.append(";");
                return new ResponseVo(false, errors.toString());
            }
        }

        try {
            this.brandService.save(brand);
        } catch (RuntimeException e) {

            return new ResponseVo(false, e.getMessage());

        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "新增或者修改失败");
        }

        return new ResponseVo(true, "新增或者修改成功");
    }

    @RequestMapping("/delete")
    public ResponseVo betchDelete(@RequestParam(value = "ids", required = true) String idSp) {
        String[] split = idSp.split("-");

        try {
            this.brandService.batchDelete(Arrays.asList(split));
        } catch (RuntimeException e) {

            return new ResponseVo(false, e.getMessage());

        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "删除失败");
        }
        return new ResponseVo(true, "删除成功");
    }




}
