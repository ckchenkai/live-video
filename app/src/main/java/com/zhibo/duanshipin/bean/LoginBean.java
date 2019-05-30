package com.zhibo.duanshipin.bean;

/**
 * Created by admin on 2017/9/12.
 */

public class LoginBean {


    /**
     * code : 0
     * msg : 登陆成功
     * data : {"uid":"883","shareurl":"883","sharepic":"883","phone":"883","nickname":"883","headpic":"883","ukey":"0d3ce0f5ec9bfb595c396a094872e23e"}
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
         * uid : 883
         * shareurl : 883
         * sharepic : 883
         * phone : 883
         * nickname : 883
         * headpic : 883
         * ukey : 0d3ce0f5ec9bfb595c396a094872e23e
         */

        private String uid;
        private String shareurl;
        private String sharepic;
        private String phone;
        private String nickname;
        private String headpic;
        private String ukey;

        public String getIs_ok() {
            return is_ok;
        }

        public void setIs_ok(String is_ok) {
            this.is_ok = is_ok;
        }

        private String is_ok;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getShareurl() {
            return shareurl;
        }

        public void setShareurl(String shareurl) {
            this.shareurl = shareurl;
        }

        public String getSharepic() {
            return sharepic;
        }

        public void setSharepic(String sharepic) {
            this.sharepic = sharepic;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getHeadpic() {
            return headpic;
        }

        public void setHeadpic(String headpic) {
            this.headpic = headpic;
        }

        public String getUkey() {
            return ukey;
        }

        public void setUkey(String ukey) {
            this.ukey = ukey;
        }
    }
}
