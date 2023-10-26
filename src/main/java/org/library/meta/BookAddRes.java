package org.library.meta;

/**
 * enum class representing book add results
 */
public enum BookAddRes {
    SUCCESS("Book return successfully"),
    REMAIN_NEGATIVE("Remain copy number is negative"),
    SYSTEM_ERROR("Book add failed");

    private String message;

    BookAddRes(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
