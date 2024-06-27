package ase.meditrack.exception;

/**
 * Exception that signals, that no solution for the schedule with the
 * specified constraints.
 */
public class NoSolutionException extends RuntimeException {
    public NoSolutionException(String message) {
        super(message);
    }

    public NoSolutionException(Throwable cause) {
        super(cause);
    }

    public NoSolutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
