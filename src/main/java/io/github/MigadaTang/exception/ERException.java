package io.github.MigadaTang.exception;

public class ERException extends RuntimeException {
    public ERException() {
        super();
    }

    public ERException(String message) {
        super(message);
    }

    public ERException(String message, Throwable cause) {
        super(message, cause);
    }

    public ERException(Throwable cause) {
        super(cause);
    }

}
