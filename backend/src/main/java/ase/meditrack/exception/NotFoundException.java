package ase.meditrack.exception;


/**
 * Exception that signals, that whatever resource,
 * that has been tried to access,
 * was not found.
 */
public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}