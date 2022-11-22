package io.github.MigadaTang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ERBaseObj {
    private Long ID;
    private Long schemaID;
    private String name;
    private LayoutInfo layoutInfo;
    private Date gmtCreate;
    private Date gmtModified;
}
