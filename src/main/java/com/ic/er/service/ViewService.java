package com.ic.er.service;

import com.ic.er.bean.dto.ViewDTO;
import com.ic.er.bean.vo.ViewVO;
import com.ic.er.common.ResultState;
import java.util.List;

public interface ViewService {
    ViewVO createView(ViewDTO view);

    ResultState deleteView(ViewDTO view);

    List<ViewVO> queryAllView();

    ViewVO queryViewById();

    ResultState updateView(ViewDTO name);
}
