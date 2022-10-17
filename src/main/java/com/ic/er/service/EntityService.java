package com.ic.er.service;

import com.ic.er.bean.dto.EntityDTO;
import com.ic.er.bean.vo.EntityVO;
import com.ic.er.common.ResultState;

public interface EntityService {

    EntityVO create(EntityDTO entityDTO);

    ResultState delete(EntityDTO entityDTO);

    ResultState update(EntityDTO entityDTO);
}
