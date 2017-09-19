package com.ifox.platform.adminuser.service.impl;

import com.ifox.platform.adminuser.dto.RoleDTO;
import com.ifox.platform.adminuser.exception.NotFoundAdminUserException;
import com.ifox.platform.adminuser.modelmapper.RoleEOMapDTO;
import com.ifox.platform.adminuser.request.role.RolePageRequest;
import com.ifox.platform.adminuser.service.RoleService;
import com.ifox.platform.baseservice.impl.GenericServiceImpl;
import com.ifox.platform.common.bean.QueryConditions;
import com.ifox.platform.common.bean.QueryProperty;
import com.ifox.platform.common.bean.SimpleOrder;
import com.ifox.platform.common.enums.EnumDao;
import com.ifox.platform.common.exception.BuildinSystemException;
import com.ifox.platform.common.page.Page;
import com.ifox.platform.common.page.SimplePage;
import com.ifox.platform.dao.sys.RoleDao;
import com.ifox.platform.entity.sys.RoleEO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ifox.platform.common.constant.ExceptionStatusConstant.BUILDIN_SYSTEM_EXP;
import static com.ifox.platform.common.constant.ExceptionStatusConstant.NOT_FOUND_ADMIN_USER_EXP;

@Service
public class RoleServiceImpl extends GenericServiceImpl<RoleEO, String> implements RoleService {

    @Autowired
    public void setGenericDao(RoleDao roleDao){
        super.genericDao = roleDao;
    }

    /**
     * 分页查询角色
     * @param pageRequest 分页参数
     * @return Page<RoleDTO>
     */
    @Override
    public Page<RoleDTO> page(RolePageRequest pageRequest) {
        SimplePage simplePage = pageRequest.convertSimplePage();

        List<QueryProperty> queryPropertyList = getQueryPropertyList(pageRequest);
        List<SimpleOrder> simpleOrderList = pageRequest.getSimpleOrderList();

        QueryConditions queryConditions = new QueryConditions(null, queryPropertyList, simpleOrderList);

        Page<RoleEO> roleEOPage = pageByQueryConditions(simplePage, queryConditions);

        return RoleEOMapDTO.mapPage(roleEOPage);
    }

    /**
     * 删除多个角色
     * @param ids ID
     */
    @Override
    public void delete(String[] ids) throws NotFoundAdminUserException, BuildinSystemException {
        for (String id : ids) {
            RoleEO roleEO = get(id);
            if (roleEO == null) {
                throw new NotFoundAdminUserException(NOT_FOUND_ADMIN_USER_EXP, "角色不存在");
            } else if(roleEO.getBuildinSystem()) {
                throw new BuildinSystemException(BUILDIN_SYSTEM_EXP, "系统内置角色，不允许删除");
            } else {
                deleteByEntity(roleEO);
            }
        }
    }

    /**
     * 根据page请求生成查询参数
     * @param pageRequest 分页请求
     * @return List<QueryProperty>
     */
    private List<QueryProperty> getQueryPropertyList(RolePageRequest pageRequest) {
        List<QueryProperty> queryPropertyList = new ArrayList<>();

        String name = pageRequest.getName();
        if (!StringUtils.isEmpty(name)) {
            String appendName = "%" + name + "%";
            QueryProperty nameQuery = new QueryProperty("name", EnumDao.Operation.LIKE, appendName);
            queryPropertyList.add(nameQuery);
        }

        RoleEO.RoleEOStatus status = pageRequest.getStatus();
        if (status != null) {
            QueryProperty statusQuery = new QueryProperty("status", EnumDao.Operation.EQUAL, status);
            queryPropertyList.add(statusQuery);
        }
        return queryPropertyList;
    }

}
