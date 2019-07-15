package com.kinny.mapper;

import com.kinny.pojo.tbSeckillGoods;
import com.kinny.pojo.tbSeckillGoodsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface tbSeckillGoodsMapper {
    int countByExample(tbSeckillGoodsExample example);

    int deleteByExample(tbSeckillGoodsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(tbSeckillGoods record);

    int insertSelective(tbSeckillGoods record);

    List<tbSeckillGoods> selectByExample(tbSeckillGoodsExample example);

    tbSeckillGoods selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") tbSeckillGoods record, @Param("example") tbSeckillGoodsExample example);

    int updateByExample(@Param("record") tbSeckillGoods record, @Param("example") tbSeckillGoodsExample example);

    int updateByPrimaryKeySelective(tbSeckillGoods record);

    int updateByPrimaryKey(tbSeckillGoods record);
}