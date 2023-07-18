package com.ylb.front.view;

import com.ylb.common.enums.RCode;

import java.sql.ResultSet;
import java.util.List;

//统一的应答结果，controller方法的返回值都是它
public class RespResult {
    //应答码,自定义数字
    private int code;
    //code的文字说明，一般做提示给用户看
    private String msg;
    //访问token
    private String accessToken;

    public void setCode(int code) {
        this.code = code;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    //单个数据
    private Object data;
    //集合数据
    private List list;
    //分页
    private PageInfo page;
    //表示成功的RespResult对象
    public static RespResult ok(){
        RespResult result = new RespResult();
        result.setRCode(RCode.SUCC);
        return result;
    }
    public static RespResult fail(){
        RespResult result = new RespResult();
        result.setRCode(RCode.UNKONW);
        return result;
    }
    public void setRCode(RCode rCode){
        this.code = rCode.getCode();
        this.msg = rCode.getText();
    }

    public PageInfo getPage() {
        return page;
    }

    public void setPage(PageInfo page) {
        this.page = page;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public int getCode() {
        return code;
    }

    public void setCode(RCode rcode) {
        this.code = rcode.getCode();
        this.msg = rcode.getText();
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
