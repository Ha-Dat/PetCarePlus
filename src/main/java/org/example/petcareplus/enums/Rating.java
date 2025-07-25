package org.example.petcareplus.enums;

public enum Rating {
    UPVOTE("Upvote"), DOWNVOTE("Downvote");

    private final String value;

    Rating(String value) {
        this.value = value;
    }
}
