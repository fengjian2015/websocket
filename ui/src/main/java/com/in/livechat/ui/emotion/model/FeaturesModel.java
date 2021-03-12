package com.in.livechat.ui.emotion.model;

/**
 * description ： 功能
 * author : Fickle
 * date : 2021/3/11 15:49
 */
public class FeaturesModel {
    private String name;
    private String url;
    private int id;
    private int resourcesId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResourcesId() {
        return resourcesId;
    }

    public void setResourcesId(int resourcesId) {
        this.resourcesId = resourcesId;
    }
}
