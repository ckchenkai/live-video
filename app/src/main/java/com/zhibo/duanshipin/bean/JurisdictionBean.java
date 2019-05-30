package com.zhibo.duanshipin.bean;

/**
 * Created by ${CC} on 2018/2/9.
 */

public class JurisdictionBean {


    /**
     * code : 0
     * msg : success
     * data : {"is_ok":"0"}
     */

    private String code;
    private String msg;
    private DataBean data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * is_ok : 0
         */

        private String is_ok;

        public String getIs_ok() {
            return is_ok;
        }

        public void setIs_ok(String is_ok) {
            this.is_ok = is_ok;
        }
    }
}
