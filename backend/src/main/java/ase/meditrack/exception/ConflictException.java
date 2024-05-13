package ase.meditrack.exception;

import java.util.List;

/**
 * Exception that signals, that data,
 * that came from outside the backend, conflicts with the current state of the system.
 * The data violates some constraint on relationships
 * (rather than an invariant).
 * Contains a list of all conflict checks that failed when validating the piece of data in question.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String messageSummary) {

        super(messageSummary);
    }
}
