package com.kinny.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.pojo.TbSeller;
import com.kinny.sellergoods.service.SellerService;
import com.kinny.vo.ResponseVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qgy
 * @create 2019/5/8 - 15:16
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    @RequestMapping("/findPage")
    public ResponseVo<Map<String, Object>> findPage(
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") String pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") String pageSize,
            @RequestParam(value = "status", required = true) String status,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "nickName", required = false) String nickName) {


        Map<String, Object> map = new HashMap<>();

        map.put("pageIndex", pageIndex);
        map.put("pageSize", pageSize);
        map.put("status", status);
        map.put("name", name);
        map.put("nickName", nickName);

        Map<String, Object> page = null;

        try {
            page = this.sellerService.findPage(map);
        } catch (IllegalArgumentException e) {
            return new ResponseVo<>(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "商家信息失败");
        }

        return new ResponseVo<>(true, page);
    }

    @RequestMapping("/findOne")
    public ResponseVo<TbSeller> findOne(@RequestParam(value = "id", required = true) String id) {


        TbSeller seller = null;
        try {
            seller = this.sellerService.findOne(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "商家信息失败");
        }

        return new ResponseVo<>(true, seller);
    }


    /**
     *  商家审核接口
     * @param sellerId
     * @param status
     * @return
     */
    @RequestMapping("/updateStatus")
    public ResponseVo updateStatus(@RequestParam(value = "id", required = true) String sellerId,
                                   @RequestParam(value = "status", required = true) String status) {
        try {
            this.sellerService.updateStatus(sellerId, status);
        } catch (RuntimeException e) {
            return new ResponseVo(false, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseVo<>(false, "商家信息失败");
        }

        return new ResponseVo<>(true, "商家信息成功");
    }


}
