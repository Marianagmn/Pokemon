package domain.model.vo;

import domain.enums.StatusEffect;
import domain.enums.StatusNature;

import java.util.Objects;

/**
 * Value Object encapsulating a status effect, its application chance,
 * and its semantic role within a move.
 *
 * <p>Distinguishes between:
 * <ul>
 *   <li>{@link StatusNature#PRIMARY} — the move's main purpose is to inflict status
 *       (e.g., Thunder Wave → always 100%)</li>
 *   <li>{@link StatusNature#SECONDARY} — a side effect of a damaging move
 *       (e.g., Flamethrower → 10% burn chance)</li>
 * </ul>
 *
 * <p>Invariants:
 * <ul>
 *   <li>Effect and nature must not be null</li>
 *   <li>Chance must be between 1 and 100 (inclusive)</li>
 *   <li>PRIMARY effects must always have 100% chance</li>
 *   <li>SECONDARY effects must have chance {@code < 100}</li>
 * </ul>
 *
 * @param effect the status condition to inflict
 * @param chance probability of inflicting (1–100)
 * @param nature the semantic role of this status within its move
 */
public record StatusApplication(StatusEffect effect, int chance, StatusNature nature) {

    public StatusApplication {
        Objects.requireNonNull(effect, "Status effect cannot be null");
        Objects.requireNonNull(nature, "Status nature cannot be null");

        if (chance < 1 || chance > 100) {
            throw new IllegalArgumentException(
                    "Status chance must be between 1 and 100, but was: " + chance);
        }
        if (nature == StatusNature.PRIMARY && chance != 100) {
            throw new IllegalArgumentException(
                    "Primary status effects must have 100% chance, but was: " + chance);
        }
    }

    // ── Factory methods ──────────────────────────────────────

    /**
     * Creates a primary status application (the move's main purpose, 100%).
     *
     * <p>Used for pure status moves like Thunder Wave, Spore, Toxic.
     *
     * @param effect the status to inflict
     * @return a guaranteed, primary StatusApplication
     */
    public static StatusApplication primary(StatusEffect effect) {
        return new StatusApplication(effect, 100, StatusNature.PRIMARY);
    }

    /**
     * Creates a secondary status application (side effect of a damaging move).
     *
     * <p>Used for moves like Flamethrower (10% burn), Ice Beam (10% freeze).
     *
     * @param effect the status to inflict
     * @param chance the probability (1–99)
     * @return a secondary StatusApplication
     */
    public static StatusApplication secondary(StatusEffect effect, int chance) {
        return new StatusApplication(effect, chance, StatusNature.SECONDARY);
    }

    // ── Domain queries ───────────────────────────────────────

    /** Returns {@code true} if this is the move's main purpose. */
    public boolean isPrimary() {
        return nature == StatusNature.PRIMARY;
    }

    /** Returns {@code true} if this is a side effect of a damaging move. */
    public boolean isSecondary() {
        return nature == StatusNature.SECONDARY;
    }

    /** Returns {@code true} if this status is guaranteed to apply (100%). */
    public boolean isGuaranteed() {
        return chance == 100;
    }
}
