package com.ic.er.dao;

import com.ic.er.bean.entity.View;
import java.util.List;

/**
 * @Desceiption View mapper
 * @author wendi
 * @data 15/10/2022
 */
public interface ViewMapper {
    List<View> selectAll();

    List<View> selectByView(View view);

    View selectById(Long id);

    int insert(View view);

    // rarely use, please use update to change is_delete to 1
    int deleteById(Long id);

    int updateById(Long id);

}
