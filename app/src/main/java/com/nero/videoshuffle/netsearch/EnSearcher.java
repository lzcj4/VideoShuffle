package com.nero.videoshuffle.netsearch;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by nlang on 4/11/2016.
 */
public class EnSearcher {
    private final String HOME_URI = "https://c.getsatisfaction.com";
    private final String NERO_HOME_URI = HOME_URI + "/nero_eng";
    private final String SEARCH_URI = NERO_HOME_URI + "/topics/search/show";

    static OkHttpClient httpClient;

    static {
        final int TIMEOUT = 2;
        final TimeUnit TIMEUNIT = TimeUnit.MINUTES;
        httpClient = new OkHttpClient();
        httpClient.setReadTimeout(TIMEOUT, TIMEUNIT);
        httpClient.setConnectTimeout(TIMEOUT, TIMEUNIT);
        httpClient.setWriteTimeout(TIMEOUT, TIMEUNIT);
    }

    public void search(final String searchKeyword) {
        Request request = new Request.Builder().url(NERO_HOME_URI).build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String html = response.body().string();
                SetQuota(html);
                RequestBody postValue = getPostValue(html, searchKeyword);
                doSearch(postValue);
            }
        });
    }


    protected final String SINGLE_QUOTA = "'";
    protected final String DOUBLE_QUOTA = "\"";
    protected String current_quota = SINGLE_QUOTA;

    private void SetQuota(String html) {
        if (html.contains("<article class=\"topic\">")) {
            current_quota = DOUBLE_QUOTA;
        } else if (html.contains("<article class='topic'>")) {
            current_quota = SINGLE_QUOTA;
        }
    }

    private String encodeAuthToken = "";

    private void doSearch(RequestBody postBody) {
        Request request = new Request.Builder().url(SEARCH_URI).post(postBody).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String html = response.body().toString();
                GetTopics(html);
            }
        });

    }

    private RequestBody getPostValue(String html, String searchKeyword) {
        if (TextUtils.isEmpty(html)) {
            throw new IllegalArgumentException("getPostValue");
        }

        String postForm = HtmlUtil.getSubString(html, String.format("<form accept-charset=%sUTF-8%s action=%s/nero_eng/topics/search/show%s method=%spost%s>",
                current_quota, current_quota, current_quota, current_quota, current_quota, current_quota), "</form>");
        if (TextUtils.isEmpty(postForm)) {
            return null;
        }

        String utf8Value = HtmlUtil.getProperty(postForm, String.format("<input name=%sutf8", current_quota), "/>", "value");
        String authToken = HtmlUtil.getProperty(postForm, String.format("<input name=%sauthenticity_token", current_quota), "/>", "value");
        String searchCateName = HtmlUtil.getProperty(postForm, String.format("<input id=%ssearch_default_category_id", current_quota), "/>", "name");
        String searchKeywordName = HtmlUtil.getProperty(postForm, String.format("<input class=%ssearch-keywords", current_quota), "/>", "name");

        String encodeUTF8Value = Uri.encode(utf8Value);
        encodeAuthToken = Uri.encode(authToken);
        String encodeSearchCateName = Uri.encode(searchCateName);
        String encodeSearchKeywordName = Uri.encode(searchKeywordName);

        String encodeKeyword = Uri.encode(searchKeyword);
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.add("utf8", utf8Value);
        builder.add("authenticity_token", authToken);
        builder.add(searchCateName, "");
        builder.add(searchKeywordName, searchKeyword);

        String value = String.format("utf8=%s&authenticity_token=%s&%s=&%s=%s&button=", encodeUTF8Value, encodeAuthToken, encodeSearchCateName, encodeSearchKeywordName, encodeKeyword);
        Log.i("test", value);
        return builder.build();


    }

    public class Topic {
        public String UserProfileUri;

        public String getUserProfileUri() {
            return UserProfileUri;
        }

        public void setUserProfileUri(String userProfileUri) {
            UserProfileUri = userProfileUri;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitleUri() {
            return titleUri;
        }

        public void setTitleUri(String titleUri) {
            this.titleUri = titleUri;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getAddDate() {
            return addDate;
        }

        public void setAddDate(String addDate) {
            this.addDate = addDate;
        }

        public String getAddDateDesc() {
            return addDateDesc;
        }

        public void setAddDateDesc(String addDateDesc) {
            this.addDateDesc = addDateDesc;
        }

        public String getLastReplyDate() {
            return lastReplyDate;
        }

        public void setLastReplyDate(String lastReplyDate) {
            this.lastReplyDate = lastReplyDate;
        }

        public String getLastReplyUri() {
            return lastReplyUri;
        }

        public void setLastReplyUri(String lastReplyUri) {
            this.lastReplyUri = lastReplyUri;
        }

        public String title;
        public String titleUri;

        public String content;

        public String author;
        public String addDate;
        public String addDateDesc;

        public String lastReplyDate;
        public String lastReplyUri;
    }

    private List<Topic> GetTopics(String html) {
        if (TextUtils.isEmpty(html)) {
            throw new IllegalArgumentException("GetTopics");
        }
        List<Topic> result = new ArrayList<>();
        String item = HtmlUtil.getSubString(html, String.format("<article class=%stopic%s>", current_quota, current_quota), "</article>");

        do {
            if (!TextUtils.isEmpty(item)) {
                Topic topic = ParseTopic(item);
                result.add(topic);
            }
            html = html.replace(item, "");
            item = HtmlUtil.getSubString(html, String.format("<article class=%stopic%s>", current_quota, current_quota), "</article>");
        } while (!TextUtils.isEmpty(item));

        return result;
    }

    private Topic ParseTopic(String html) {
        if (TextUtils.isEmpty(html)) {
            throw new IllegalArgumentException("ParseTopic");
        }

        Topic result = new Topic();
        // result.IconUri = html.GetProperty("<img", "/>", "src");
        String userId = HtmlUtil.getProperty(html, "<a", "</a>", "data-user-id");
        String userUri = "https://c.getsatisfaction.com/nero_eng?component=profile_summary&options%5Bas%5D=profile-summary&options%5Bsettings%5D%5Buser_id%5D=";
        result.UserProfileUri = userUri + userId;

        String titleHtml = HtmlUtil.getSubString(html, "<h6>", "</h6>");
        result.titleUri = HOME_URI + HtmlUtil.getProperty(titleHtml, "<a", "</a>", "href");
        result.title = HtmlUtil.getValue(titleHtml, "<a", "</a>");

        result.content = HtmlUtil.getValue(html, String.format("<div class=%suser-generated-content%s>", current_quota, current_quota), "</div>").trim();

        String authorHtml = HtmlUtil.getSubString(html, String.format("<li class=%sauthor%s>", current_quota, current_quota), "</li>");
        result.author = HtmlUtil.getValue(authorHtml, "<a", "</a>");
        result.addDateDesc = HtmlUtil.getValue(authorHtml, "<span", "</span>");
        result.addDate = HtmlUtil.getProperty(authorHtml, "<span", "</span>", "title");

        if (html.contains(String.format("<li class=%sreplier%s>", current_quota, current_quota))) {
            String replyHtml = HtmlUtil.getSubString(html, String.format("<li class=%sreplier%s>", current_quota, current_quota), "</li>");
            result.lastReplyDate = HtmlUtil.getValue(replyHtml, "<a", "</a>");
            result.lastReplyUri = HOME_URI + HtmlUtil.getProperty(replyHtml, "<a", "</a>", "href");
        }

        return result;

    }
}
