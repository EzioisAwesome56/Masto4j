package com.github.ezioisawesome56.masto4j.jsonObjects;

public class TokenResponse {

    private String access_token;
    private String token_type;
    private String scope;
    private long created_at;

    public String getAccess_token() {
        return access_token;
    }

    public String getScope() {
        return scope;
    }

    public String getToken_type() {
        return token_type;
    }


}
