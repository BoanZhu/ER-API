package com.ic.er.service;

import com.ic.er.bean.dto.RelationshipDTO;
import com.ic.er.bean.vo.RelationshipVO;
import com.ic.er.common.ResultState;

public interface RelationshipService {

    RelationshipVO createRelationship(RelationshipDTO relationshipDTO);

    ResultState deleteRelationship(RelationshipDTO relationshipDTO);

    ResultState updateRelationship(RelationshipDTO relationshipDTO);
}
