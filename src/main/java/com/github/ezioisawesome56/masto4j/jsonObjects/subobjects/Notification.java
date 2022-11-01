package com.github.ezioisawesome56.masto4j.jsonObjects.subobjects;

public class Notification {
    // TODO: fully support all fields here
    private Account account;
    private String created_at;
    private String id;
    private Status status;
    private String type;
    private PleromaNotificationExtensions pleroma;

    public String getCreated_at() {
        return created_at;
    }

    public String getId() {
        return id;
    }

    public PleromaNotificationExtensions getPleroma() {
        return pleroma;
    }

    public String getType() {
        return type;
    }

    public Account getAccount() {
        return account;
    }

    public Status getStatus() {
        return status;
    }

    public class PleromaNotificationExtensions{
        public final boolean is_muted;
        public final boolean is_seen;

        public PleromaNotificationExtensions(boolean is_muted, boolean is_seen) {
            this.is_muted = is_muted;
            this.is_seen = is_seen;
        }
    }
}


