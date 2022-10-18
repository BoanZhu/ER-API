package com.ic.er.bean.vo;

import com.ic.er.bean.entity.AttributeDO;
import com.ic.er.common.ResultState;
import java.util.List;

public class EntityVO {
    private Long id;

    private String name;

    private Long viewId;

    private List<AttributeDO> attributeDOList;

    private ResultState resultState;
}
