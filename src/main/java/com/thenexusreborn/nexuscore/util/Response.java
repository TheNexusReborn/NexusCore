package com.thenexusreborn.nexuscore.util;

public class Response<T> {

    /**
     * Status of the response
     */
    public enum Status {
        SUCCESS, FAILURE
    }

    private final T response;
    private final Status status;
    private final Throwable error;

    public Response(T response, Status status, Throwable error) {
        this.response = response;
        this.status = status;
        this.error = error;
    }

    public Response(T response, Status status) {
        this(response, status, null);
    }

    public Response(Status status, Throwable error) {
        this(null, status, error);
    }

    /**
     * Gets the primary response. Can be null
     * @return The response
     */
    public T get() {
        return response;
    }

    /**
     * @return The status of this response
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @return The error (if any) that happened as a result 
     */
    public Throwable getError() {
        return error;
    }

    /**
     * @return If the status is equal to the SUCCESS enum value.
     */
    public boolean success() {
        return this.status == Status.SUCCESS;
    }
}
