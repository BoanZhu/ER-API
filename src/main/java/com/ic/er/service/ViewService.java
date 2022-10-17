package com.ic.er.service;

import com.ic.er.bean.dto.ViewDTO;
import com.ic.er.bean.vo.ViewVO;
import com.ic.er.common.ResultState;
import java.util.List;

public interface ViewService {
    ViewVO create(ViewDTO view);

    ResultState delete(ViewDTO view);

    List<ViewVO> queryAll();

    ViewVO queryById();

    ResultState update(ViewDTO name);
}
