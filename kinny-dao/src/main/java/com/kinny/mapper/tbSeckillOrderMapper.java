package com.kinny.mapper;

import com.kinny.pojo.tbSeckillOrder;
import com.kinny.pojo.tbSeckillOrderExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface tbSeckillOrderMapper {
    int countByExample(tbSeckillOrderExample example);

    int deleteByExample(tbSeckillOrderExample example);

    int deleteByPrimaryKey(Long id);

    int insert(tbSeckillOrder record);

    int insertSelective(tbSeckillOrder record);

    List<tbSeckillOrder> selectByExample(tbSeckillOrderExample example);

    tbSeckillOrder selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") tbSeckillOrder record, @Param("example") tbSeckillOrderExample example);

    int updateByExample(@Param("record") tbSeckillOrder record, @Param("example") tbSeckillOrderExample example);

    int updateByPrimaryKeySelective(tbSeckillOrder record);

    int updateByPrimaryKey(tbSeckillOrder record);
}