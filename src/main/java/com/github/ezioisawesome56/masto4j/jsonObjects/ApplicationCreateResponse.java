package com.github.ezioisawesome56.masto4j.jsonObjects;

public class ApplicationCreateResponse {

    private String id;
    private String name;
    private String website;
    private String redirect_uri;
    private String client_id;
    private String client_secret;
    private String vapid_key;


    public String getClient_id() {
        return client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public String getVapid_key() {
        return vapid_key;
    }
}
