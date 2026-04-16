package com.cug.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class R {
    private String code;
    private String message;
    private Object data;
    public static R ok(){
        return ok(null);
    }
    public static R ok(Object data){
        R r = new R();
        r.setCode("200");
        r.setMessage("操作成功");
        r.setData(data);
        return r;
    }
    public static R error(String message){
        R r = new R();
        r.setCode("500");
        r.setMessage(message);
        return r;
    }
}
