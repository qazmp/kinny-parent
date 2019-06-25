package com.kinny.pojo.group;

import com.kinny.pojo.TbSpecification;
import com.kinny.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 *  规格 规格选项组合实体类 与 规格一对一 规格选项一对多
 * @author qgy
 * @create 2019/5/6 - 14:54
 */
public class SpecificationGroup implements Serializable {

    private TbSpecification specification;

    private List<TbSpecificationOption> specificationOptionList;

    public SpecificationGroup() {
    }

    public SpecificationGroup(TbSpecification specification, List<TbSpecificationOption> specificationOptionList) {
        this.specification = specification;
        this.specificationOptionList = specificationOptionList;
    }

    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List<TbSpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
