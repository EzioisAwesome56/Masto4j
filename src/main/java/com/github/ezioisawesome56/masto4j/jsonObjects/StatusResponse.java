package com.github.ezioisawesome56.masto4j.jsonObjects;

public class StatusResponse {

    private String id;
    private String created_at;
    private String content;

    public String getContent() {
        return this.content;
    }

    public String getId() {
        return this.id;
    }

    public String getCreated_at() {
        return this.created_at;
    }
}
