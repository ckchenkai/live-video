package com.zhibo.duanshipin.bean;

import java.util.List;

/**
 * Created by admin on 2017/8/31.
 * 首页——直播
 */

public class HomeItemLiveBean {


    /**
     * status : 0
     * info : 获取成功
     * lists : [{"phonethumb":"http://newlive.longhoo.net/data/upload/20170623/594ce4da76b34.jpg","term_id":"250","starttime":"2017-06-24 14:00:22","endtime":"2017-06-24 15:00:22","roomid":"room-1","name":"2017江苏高考分数线发布","istart":"0","isend":"0","term_hits":"76","description":"江苏省教育厅将于6月24日下午公布江苏普通高校招生第一阶段录取各批次最低控制分数线及高考志愿填报等相关情况。","zburl":""},{"phonethumb":"http://newlive.longhoo.net/data/upload/20170622/594b6e62c6b54.jpg","term_id":"247","starttime":"2017-06-23 09:00:00","endtime":"2017-06-24 16:30:00","roomid":"room-5","name":"看\u201c大美河西 锦绣建邺\u201d 惊艳亮相第5届西部旅游产业博览会","istart":"0","isend":"0","term_hits":"78398","description":"建邺区文化旅游局 龙虎网联合出品，带你去看第五届中国西部旅游产业博览会。","zburl":""},{"phonethumb":"http://newlive.longhoo.net/data/upload/20170614/5940ee45abf85.jpg","term_id":"233","starttime":"2017-06-15 10:00:00","endtime":"2017-06-15 11:00:44","roomid":"room-2","name":"2017年世界全项目轮滑锦标赛新闻发布会","istart":"0","isend":"1","term_hits":"46561","description":"2017年世界全项目轮滑锦标赛（简称世锦赛）将于8月27日至9月10日在宁举办，组委会于6月15日召开首场新闻发布会，向社会各界介绍本届轮滑世锦赛的整体情况，发布相关赛会信息。","zburl":""},{"phonethumb":"http://newlive.longhoo.net/data/upload/20170509/591119249a346.jpg","term_id":"191","starttime":"2017-05-09 10:00:00","endtime":"2017-05-09 18:25:47","roomid":"room-1","name":"2017中国（昆山）品牌产品进口交易发布会","istart":"0","isend":"0","term_hits":"12709","description":"2017中国品牌产品进口交易会将于5月17日-19日在江苏昆山举办。品牌销售展区集合英国、澳大利亚、新西兰、南非等19个国家展团参展，其中\u201c一带一路\u201d沿线国家和地区展团有12个，\u201c金砖国家\u201d展团有4个。英国、澳大利亚、日本、巴西、印度、马来西亚、南非等国家将展示皇室御用的茶具、雨伞、童装礼服、保健品、厨房用品等特色产品。","zburl":""},{"phonethumb":"http://newlive.longhoo.net/data/upload/20170506/590cfb58e1c9a.jpg","term_id":"187","starttime":"2017-05-06 07:15:00","endtime":"2017-05-06 18:54:43","roomid":"room-1","name":"2017第二届南京·玄武紫金山城市山地马拉松赛","istart":"0","isend":"1","term_hits":"90483","description":"2017年5月6日联合玄武区人民政府、中山陵园管理局、市旅游委等部门,牵头主办2017第二届南京·玄武紫金山城市山地马拉松。此赛事在风景优美，历史悠久的玄武湖畔和紫金山举行，是为数不多的城市中心山地马拉松赛事。","zburl":""}]
     */

    private int status;
    private String info;
    private List<ListsBean> lists;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<ListsBean> getLists() {
        return lists;
    }

    public void setLists(List<ListsBean> lists) {
        this.lists = lists;
    }

    public static class ListsBean {
        /**
         * phonethumb : http://newlive.longhoo.net/data/upload/20170623/594ce4da76b34.jpg
         * term_id : 250
         * starttime : 2017-06-24 14:00:22
         * endtime : 2017-06-24 15:00:22
         * roomid : room-1
         * name : 2017江苏高考分数线发布
         * istart : 0
         * isend : 0
         * term_hits : 76
         * description : 江苏省教育厅将于6月24日下午公布江苏普通高校招生第一阶段录取各批次最低控制分数线及高考志愿填报等相关情况。
         * zburl :
         */

        private String phonethumb;
        private String mthumb;
        private String term_id;
        private String starttime;
        private String endtime;
        private String roomid;
        private String name;
        private String istart;
        private String isend;
        private String term_hits;
        private String description;
        private String zburl;
        private String term_type;
        private String share_url;
        private String status;

        public String getPhonethumb() {
            return phonethumb;
        }

        public void setPhonethumb(String phonethumb) {
            this.phonethumb = phonethumb;
        }

        public String getMthumb() {
            return mthumb;
        }

        public void setMthumb(String mthumb) {
            this.mthumb = mthumb;
        }

        public String getTerm_id() {
            return term_id;
        }

        public void setTerm_id(String term_id) {
            this.term_id = term_id;
        }

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
        }

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        public String getRoomid() {
            return roomid;
        }

        public void setRoomid(String roomid) {
            this.roomid = roomid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIstart() {
            return istart;
        }

        public void setIstart(String istart) {
            this.istart = istart;
        }

        public String getIsend() {
            return isend;
        }

        public void setIsend(String isend) {
            this.isend = isend;
        }

        public String getTerm_hits() {
            return term_hits;
        }

        public void setTerm_hits(String term_hits) {
            this.term_hits = term_hits;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getZburl() {
            return zburl;
        }

        public void setZburl(String zburl) {
            this.zburl = zburl;
        }

        public String getTerm_type() {
            return term_type;
        }

        public void setTerm_type(String term_type) {
            this.term_type = term_type;
        }
        public String getShare_url() {
            return share_url;
        }

        public void setShare_url(String share_url) {
            this.share_url = share_url;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
