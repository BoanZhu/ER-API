package io.github.MigadaTang.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultState {

    private Integer status;

    private String msg;

    private Object data;

    public static ResultState build(Integer status, String msg, Object data) {
        return new ResultState(status, msg, data);
    }

    public static ResultState ok(Object data) {
        return new ResultState(data);
    }

    public static ResultState ok() {
        return new ResultState(null);
    }

    public static ResultState build(Integer status, String msg) {
        return new ResultState(status, msg, null);
    }

    public ResultState(Object data) {
        this.status = ResultStateCode.Success;
        this.msg = "OK";
        this.data = data;
    }
}
