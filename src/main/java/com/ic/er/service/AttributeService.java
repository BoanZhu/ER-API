package com.ic.er.service;

import com.ic.er.bean.dto.AttributeDTO;
import com.ic.er.bean.vo.AttributeVO;
import com.ic.er.common.ResultState;

public interface AttributeService {
    AttributeVO create(AttributeDTO attributeDTO);

    ResultState delete(AttributeDTO attributeDTO);

    ResultState update(AttributeDTO attributeDTO);

}
