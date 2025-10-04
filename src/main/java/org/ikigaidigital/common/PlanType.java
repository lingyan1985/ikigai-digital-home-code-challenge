package org.ikigaidigital.common;

/**
 * Enumeration of supported time deposit plan types.
 * Provides a safe abstraction over raw strings.
 */
public enum PlanType {
    BASIC,
    STUDENT,
    PREMIUM,
    UNKNOWN;

    /**
     * Parse a string safely into a PlanType.
     * Returns UNKNOWN for invalid or null inputs.
     */
    public static PlanType from(String raw) {
        if (raw == null) return UNKNOWN;
        try {
            return PlanType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return UNKNOWN;
        }
    }
}
