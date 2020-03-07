package org.isf.shared.controller;

public enum OHResponseType {

    SUCCESS("success"), INFO("success"), WARNING("warning"), ERROR("error");

    private final String descriptiuon;

    OHResponseType(String description) {
        this.descriptiuon = description;
    }

    public String getValue() {
        return descriptiuon;
    }
}
