package com.dasheng.model;

import java.io.Serializable;

/**
 * 操作结果返回对象
 */
public class ServiceResult implements Serializable {

    private Integer errorCode = Integer.valueOf(0);
    private String errorMsg;
    private Object data;
    private Long total;
    private Integer status = Integer.valueOf(1);

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
