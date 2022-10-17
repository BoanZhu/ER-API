package com.ic.er.service;

import com.ic.er.bean.dto.RelationshipDTO;
import com.ic.er.bean.vo.RelationshipVO;
import com.ic.er.common.ResultState;

public interface RelationshipService {

    RelationshipVO create(RelationshipDTO relationshipDTO);

    ResultState delete(RelationshipDTO relationshipDTO);

    ResultState update(RelationshipDTO relationshipDTO);
}
