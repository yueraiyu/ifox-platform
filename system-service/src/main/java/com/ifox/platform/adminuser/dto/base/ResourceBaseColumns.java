package com.ifox.platform.adminuser.dto.base;

import com.ifox.platform.entity.common.ResourceEO;

public class ResourceBaseColumns {
    /**
     * 资源名称
     */
    private String name;

    /**
     * 资源对应的控制器的RequestMapping, 比如:adminUser
     */
    private String controller;

    /**
     * 资源类型
     */
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

    @Override
    public String toString() {
        return "ResourceColumns{" +
            "name='" + name + '\'' +
            ", controller='" + controller + '\'' +
            ", type=" + type +
            ", remark='" + remark + '\'' +
            '}';
    }
}
