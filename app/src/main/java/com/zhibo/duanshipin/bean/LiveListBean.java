package com.zhibo.duanshipin.bean;

import java.util.List;

/**
 * Created by CK on 2017/9/5.
 * Email:910663958@qq.com
 */

public class LiveListBean {

    /**
     * status : 0
     * info : 成功
     * lists : [{"post_date":"2017-09-05 10:47:28","post_content":"主持人： 今天非常感谢郑局长来到我们演播室，给我们详细的介绍了南京市地税局大走访活动开展的一些情况，同时也谢谢台下各位嘉宾的参与。 南京市地税局开展大走访，重在发现问题、解决问题、推动工作，让大走访工作成为密切党群干群关系、锤炼党员干部作风、解决基层实际问题的有力工具，走访工作也取得了一定的实实在在的政治、社会和工作效果，让广大纳税人和人民群众更加满意。 本期南京市地税局\u201c大走访\u201d访谈到此就结束了，接下来，我们还将陆续走进南京市的其他区和部委办局进行\u201c大走访\u201d的访谈直播活动，也欢迎大家的积极参与！同时您也可以通过龙虎网、龙虎网官方微博微信；龙虎政能量、南京作风建设微信和我们进行互动！再次感谢大家的收看，我们下一期节目再见！","photos":[]}]
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
         * post_date : 2017-09-05 10:47:28
         * post_content : 主持人： 今天非常感谢郑局长来到我们演播室，给我们详细的介绍了南京市地税局大走访活动开展的一些情况，同时也谢谢台下各位嘉宾的参与。 南京市地税局开展大走访，重在发现问题、解决问题、推动工作，让大走访工作成为密切党群干群关系、锤炼党员干部作风、解决基层实际问题的有力工具，走访工作也取得了一定的实实在在的政治、社会和工作效果，让广大纳税人和人民群众更加满意。 本期南京市地税局“大走访”访谈到此就结束了，接下来，我们还将陆续走进南京市的其他区和部委办局进行“大走访”的访谈直播活动，也欢迎大家的积极参与！同时您也可以通过龙虎网、龙虎网官方微博微信；龙虎政能量、南京作风建设微信和我们进行互动！再次感谢大家的收看，我们下一期节目再见！
         * photos : []
         */

        private String post_date;
        private String post_content;
        private List<String> photos;
        private String qn_video;
        private String qn_video_img;

        public String getPost_date() {
            return post_date;
        }

        public void setPost_date(String post_date) {
            this.post_date = post_date;
        }

        public String getPost_content() {
            return post_content;
        }

        public void setPost_content(String post_content) {
            this.post_content = post_content;
        }

        public List<String> getPhotos() {
            return photos;
        }

        public void setPhotos(List<String> photos) {
            this.photos = photos;
        }

        public String getQn_video() {
            return qn_video;
        }

        public void setQn_video(String qn_video) {
            this.qn_video = qn_video;
        }

        public String getQn_video_img() {
            return qn_video_img;
        }

        public void setQn_video_img(String qn_video_img) {
            this.qn_video_img = qn_video_img;
        }
    }
}
