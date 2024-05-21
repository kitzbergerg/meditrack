package ase.meditrack.model;


import jakarta.validation.groups.Default;

/**
 * Needed to allow validation annotations to always trigger if groups is empty.
 * <p>
 * DO NOT DELETE even if unused.
 */
public interface AllValidators extends Default, CreateValidator, UpdateValidator {
}
