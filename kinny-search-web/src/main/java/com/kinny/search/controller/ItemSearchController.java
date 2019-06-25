package com.kinny.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.search.service.ItemSearchService;
import com.kinny.vo.ResponseVo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/6/10 - 10:51
 */
@RestController
@RequestMapping("/ItemSearch")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;


    @RequestMapping("/search")
    public ResponseVo<Map<String, Object>> search(
            @RequestBody(required = true) Map<String, Object> map) {

        Map<String, Object> objectMap = new HashMap<>();
        try {
            objectMap = this.itemSearchService.search(map);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "检索sku失败");
        }

        return new ResponseVo<>(true, objectMap);
    }



}
