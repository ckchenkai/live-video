package com.zhibo.duanshipin.bean;

import java.util.List;

/**
 * Created by ${CC} on 2018/2/11.
 */

public class UploadRecordBean {


    /**
     * code : 0
     * msg : success
     * data : [{"id":"1","uid":"894","url":"httpwefewfewfewfewf","content":"inifewnifiew"},{"id":"2","uid":"894","url":"gergerg ","content":"gregergr"}]
     */

    private String code;
    private String msg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * uid : 894
         * url : httpwefewfewfewfewf
         * content : inifewnifiew
         */

        private String id;
        private String uid;
        private String url;
        private String content;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        private String time;
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
