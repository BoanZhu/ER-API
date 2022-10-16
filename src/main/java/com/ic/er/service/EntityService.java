package com.ic.er.service;

import com.ic.er.bean.dto.EntityDTO;
import com.ic.er.bean.vo.EntityVO;
import com.ic.er.common.ResultState;

public interface EntityService {

    EntityVO createEntity(EntityDTO entityDTO);

    ResultState deleteEntity(EntityDTO entityDTO);

    ResultState updateEntity(EntityDTO entityDTO);
}
