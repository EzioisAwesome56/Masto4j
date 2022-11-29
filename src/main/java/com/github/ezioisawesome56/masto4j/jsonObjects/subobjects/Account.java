package com.github.ezioisawesome56.masto4j.jsonObjects.subobjects;

public class Account {
    private String id;
    private String acct;
    private String avatar;
    private String avatar_static;
    private boolean bot;
    private String created_at;
    private String display_name;
    private int followers_count;
    private int following_count;
    private String fqn;
    private String header;
    private String header_static;
    private String note;
    private PleromaAccountExtensions pleroma;

    public String getId() {
        return id;
    }

    public String getHeader() {
        return header;
    }

    public String getHeader_static() {
        return header_static;
    }

    public boolean isBot() {
        return bot;
    }

    public int getFollowing_count() {
        return following_count;
    }

    public String getAvatar_static() {
        return avatar_static;
    }

    public String getFqn() {
        return this.fqn;
    }

    public String getNote() {
        return note;
    }

    public PleromaAccountExtensions getPleroma() {
        return pleroma;
    }

    public String getCreated_at() {
        return this.created_at;
    }

    public String getAcct() {
        return this.acct;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public String getDisplay_name() {
        return this.display_name;
    }

    public int getFollowers_count() {
        return this.followers_count;
    }

    public class PleromaAccountExtensions {
        public final String ap_id;
        public final String background_image;
        public final String favicon;
        public final boolean hide_favorites;
        public final boolean hide_followers;
        public final boolean hide_followers_count;
        public final boolean hide_follows;
        public final boolean hide_follows_count;
        public final boolean is_admin;

        public PleromaAccountExtensions(String ap_id, String background_image, String favicon, boolean hide_favorites, boolean hide_followers, boolean hide_followers_count, boolean hide_follows, boolean hide_follows_count, boolean is_admin) {
            this.ap_id = ap_id;
            this.background_image = background_image;
            this.favicon = favicon;
            this.hide_favorites = hide_favorites;
            this.hide_followers = hide_followers;
            this.hide_followers_count = hide_followers_count;
            this.hide_follows = hide_follows;
            this.hide_follows_count = hide_follows_count;
            this.is_admin = is_admin;
        }
    }
}
