package com.youle.gamebox.ui.api.dynamic;

import com.youle.gamebox.ui.api.AbstractApi;
import com.youle.gamebox.ui.http.HttpMethod;

/**
 * Created by Administrator on 2014/6/3.
 */
public class DymaicListApi extends AbstractApi{

    private String sid ;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    @Override
    protected String getPath() {
        return "/gamebox/dynamic/list";
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.GET;
    }
}
