package com.cca2.musiclibrary;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Subscription {

    private String email;
    @JsonProperty("subscription")
    private List<String> subscription;

    public Subscription() {
    }

    public Subscription(String email, List<String> subscription) {
        this.email = email;
        this.subscription = subscription;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getSubscription() {
        return subscription;
    }

    public void setSubscription(List<String> subscription) {
        this.subscription = subscription;
    }

}
