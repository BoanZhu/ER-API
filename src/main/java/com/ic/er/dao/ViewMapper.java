package com.ic.er.dao;

import com.ic.er.bean.entity.ViewDO;

import java.util.List;

public interface ViewMapper {
    List<ViewDO> selectAll();

    List<ViewDO> selectByView(ViewDO viewDO);

    ViewDO selectById(Long id);

    Long insert(ViewDO viewDO);

    // rarely use, please use update to change is_delete to 1
    int deleteById(Long id);

    int updateById(Long id);

}
