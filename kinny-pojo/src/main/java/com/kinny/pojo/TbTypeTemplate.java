package com.kinny.pojo;

import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

public class TbTypeTemplate implements Serializable {
    private Long id;
    @NotBlank(message = "模板名称不能为空")
    private String name;
    @NotBlank(message = "规格不能为空")
    private String specIds;
    @NotBlank(message = "品牌不能为空")
    private String brandIds;
    @NotBlank(message = "扩展属性不能为空")
    private String customAttributeItems;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getSpecIds() {
        return specIds;
    }

    public void setSpecIds(String specIds) {
        this.specIds = specIds == null ? null : specIds.trim();
    }

    public String getBrandIds() {
        return brandIds;
    }

    public void setBrandIds(String brandIds) {
        this.brandIds = brandIds == null ? null : brandIds.trim();
    }

    public String getCustomAttributeItems() {
        return customAttributeItems;
    }

    public void setCustomAttributeItems(String customAttributeItems) {
        this.customAttributeItems = customAttributeItems == null ? null : customAttributeItems.trim();
    }
}