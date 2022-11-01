package com.github.ezioisawesome56.masto4j.jsonObjects;

public class MediaUploadResponse {

    private String id;
    private String type;
    private String url;
    private String preview_url;
    private String remote_url;

    public String getId() {
        return id;
    }

    public String getPreview_url() {
        return preview_url;
    }

    public String getType() {
        return type;
    }

    public String getRemote_url() {
        return remote_url;
    }

    public String getUrl() {
        return url;
    }
}
