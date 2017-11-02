package com.ifox.platform.system.dto;

import com.ifox.platform.system.entity.ResourceEO;
import org.hibernate.validator.constraints.NotBlank;

public class ResourceDTO {

    /**
     * ID
     */
    @NotBlank
    private String id;

    /**
     * 资源名称
     */
    @NotBlank
    private String name;

    /**
     * 资源对应的控制器的RequestMapping, 比如:adminUser
     */
    @NotBlank
    private String controller;

    /**
     * 资源类型
     */
    @NotBlank
    private ResourceEO.ResourceEOType type;

    /**
     * 描述
     */
    private String remark;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public ResourceEO.ResourceEOType getType() {
        return type;
    }

    public void setType(ResourceEO.ResourceEOType type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ResourceDTO{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", controller='" + controller + '\'' +
            ", type=" + type +
            ", remark='" + remark + '\'' +
            '}';
    }
}