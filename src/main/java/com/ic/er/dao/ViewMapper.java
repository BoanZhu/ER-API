package com.ic.er.dao;

import com.ic.er.dto.entity.ViewDO;

import java.util.List;

public interface ViewMapper {
    List<ViewDO> selectAll();

    List<ViewDO> selectByView(ViewDO viewDO);

    ViewDO selectByID(Long ID);

    int insert(ViewDO viewDO);

    // rarely use, please use update to change is_delete to 1
    int deleteByID(Long ID);

    int updateByID(ViewDO viewDO);

}
