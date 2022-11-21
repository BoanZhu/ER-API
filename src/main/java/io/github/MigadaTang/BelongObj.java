package io.github.MigadaTang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BelongObj {
    private Long ID;
    private String name;
    private Long schemaID;
    private LayoutInfo layoutInfo;
    private Date gmtCreate;
    private Date gmtModified;
}
