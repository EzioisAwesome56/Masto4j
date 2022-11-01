package com.github.ezioisawesome56.masto4j.exceptions;

import com.github.ezioisawesome56.masto4j.jsonObjects.MastodonError;

public class MastodonAPIException extends Exception{

    private int error;

    public MastodonAPIException(int errorcode, MastodonError error){
        super("Mastodon API Returned an error: " + error.getError());
        this.error = errorcode;
    }

    public int getError() {
        return error;
    }
}
