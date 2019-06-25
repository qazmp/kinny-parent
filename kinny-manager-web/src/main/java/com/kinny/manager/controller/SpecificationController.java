package com.kinny.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.pojo.TbSpecification;
import com.kinny.pojo.group.SpecificationGroup;
import com.kinny.sellergoods.service.SpecificationService;
import com.kinny.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/6 - 9:15
 */
@RestController
@RequestMapping("/specification")
public class SpecificationController {


    @Reference
    private SpecificationService specificationService;


    @RequestMapping("/findAll")
    public ResponseVo<List<TbSpecification>> findAll() {

        try {
            List<TbSpecification> specificationList = this.specificationService.findAll();
            return new ResponseVo<>(true, specificationList);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "查询全部规格数据失败");
        }

    }

    @RequestMapping("/findPage")
    public ResponseVo<Map<String, Object>> findPage(
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") String pageIndexStr,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") String pageSizeStr,
            @RequestParam(value = "specName", required = false) String specName) {
        System.out.println("pageIndexStr = [" + pageIndexStr + "], pageSizeStr = [" + pageSizeStr + "], specName = [" + specName + "]");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pageIndex", pageIndexStr);
        paramMap.put("pageSize", pageSizeStr);
        paramMap.put("specName", specName);
        Map<String, Object> map = null;
        try {
            map = this.specificationService.findPage(paramMap);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "分页获取规格数据失败");
        }

        return new ResponseVo<>(true, map);
    }

    @RequestMapping("/findOne")
    public ResponseVo<SpecificationGroup> findOne(String id) {
        SpecificationGroup specificationGroup = null;
        try {
            specificationGroup = this.specificationService.findOne(id);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "查询规格以及规格选项失败");
        }

        return new ResponseVo<>(true, specificationGroup);
    }


    /**
     *  新增规格包含规格选项
     * @param specificationGroup
     * @return
     */
    @RequestMapping("/add")
    public ResponseVo add(@RequestBody(required = true) SpecificationGroup specificationGroup) {

        try {
            this.specificationService.cascadeAdd(specificationGroup);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "新增规格失败");
        }

        return new ResponseVo(true, "新增规格成功");
    }
    /**
     *  修改规格包含规格选项
     * @param specificationGroup
     * @return
     */
    @RequestMapping("/update")
    public ResponseVo update(@RequestBody(required = true) SpecificationGroup specificationGroup) {

        try {
            this.specificationService.cascadeUpdate(specificationGroup);
        }  catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "修改规格失败");
        }

        return new ResponseVo(true, "修改规格成功");
    }

    @RequestMapping("/delete")
    public ResponseVo delete(String idSplit) {

        String[] split = idSplit.split("-");
        try {
            this.specificationService.batchRemove(split);
        } catch (RuntimeException e) {
            return new ResponseVo(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo(false, "删除规格失败");
        }

        return new ResponseVo(true, "删除规格成功");
    }
}
