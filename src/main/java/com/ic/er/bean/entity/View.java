package com.ic.er.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Desceiption View entity
 * @author wendi
 * @date 15/10/2022
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class View {
    private long id;

    private String name;

    private String creator;

    private byte is_delete;

    private Date gmt_create;

    private Date gmt_modefied;

}
