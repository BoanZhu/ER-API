package com.ic.er.service;

import com.ic.er.bean.dto.AttributeDTO;
import com.ic.er.bean.vo.AttributeVO;
import com.ic.er.common.ResultState;

public interface AttributeService {
    AttributeVO createAttribute(AttributeDTO attributeDTO);

    ResultState deleteAttribute(AttributeDTO attributeDTO);

    ResultState updateAttribute(AttributeDTO attributeDTO);

}
