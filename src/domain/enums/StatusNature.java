package domain.enums;

/**
 * Defines the semantic role of a status effect within a move.
 *
 * <p>More expressive than a boolean and scalable for future mechanics.
 *
 * @see domain.model.vo.StatusApplication
 */
public enum StatusNature {

    /** The move's main purpose is to inflict status (e.g., Thunder Wave). */
    PRIMARY,

    /** A side effect of a damaging move (e.g., Flamethrower's 10% burn). */
    SECONDARY
}
