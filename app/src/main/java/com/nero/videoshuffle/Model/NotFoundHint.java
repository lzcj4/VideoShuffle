package com.nero.videoshuffle.model;

/**
 * Created by nlang on 15/11/24.
 */
public class NotFoundHint {
    public String message;
    public String documentation_url;

    public String getMessage() {
        return message;
    }

    public void setMessage(String newValue) {
        message = newValue;
    }

    public String getDocumentation_url() {
        return documentation_url;
    }

    public void setDocumentation_url(String newValue) {
        documentation_url = newValue;
    }
}
