package domain.enums;

/**
 * Non-volatile and volatile status conditions that can affect a Pokémon.
 *
 * <p><b>Non-volatile</b> conditions persist after switching out:
 * {@link #BURN}, {@link #FREEZE}, {@link #PARALYSIS},
 * {@link #POISON}, {@link #TOXIC}, {@link #SLEEP}.
 *
 * <p><b>Volatile</b> conditions are cleared on switch:
 * {@link #CONFUSION}, {@link #FLINCH}.
 *
 * <p>A Pokémon can only have one non-volatile status at a time,
 * but may have multiple volatile conditions simultaneously.
 *
 * <p><b>Design note:</b> There is no {@code NONE} value. Absence of a
 * non-volatile status should be expressed via {@code Optional<StatusEffect>}
 * in the Pokémon entity (idiomatic Java, avoids null ambiguity).
 *
 * <p>All behavioral flags are declared in the constructor
 * (Open/Closed Principle): adding a new status requires no changes
 * to existing query methods.
 */
public enum StatusEffect {

    // ── Non-volatile (persist after switch) ──────────────────

    /** Halves Attack; deals 1/16 max HP damage per turn. */
    BURN(false, false, true),

    /** Pokémon cannot act; thaws with certain moves or by chance. */
    FREEZE(false, true, false),

    /** Speed quartered; 25% chance of being fully paralyzed each turn. */
    PARALYSIS(false, false, false),

    /** Deals 1/8 max HP damage per turn. */
    POISON(false, false, true),

    /** Like poison, but damage increases each turn (1/16, 2/16, 3/16...). */
    TOXIC(false, false, true),

    /** Pokémon cannot act for 1–3 turns. */
    SLEEP(false, true, false),

    // ── Volatile (cleared on switch) ─────────────────────────

    /** Pokémon has a 33% chance of hitting itself each turn. */
    CONFUSION(true, false, false),

    /** Pokémon cannot act this turn (applied by certain moves). */
    FLINCH(true, true, false);

    private final boolean isVolatile;
    private final boolean preventsAction;
    private final boolean dealsResidualDamage;

    StatusEffect(boolean isVolatile, boolean preventsAction, boolean dealsResidualDamage) {
        this.isVolatile = isVolatile;
        this.preventsAction = preventsAction;
        this.dealsResidualDamage = dealsResidualDamage;
    }

    /**
     * Returns {@code true} if this condition is cleared when switching out.
     *
     * @return {@code true} for volatile conditions
     */
    public boolean isVolatile() {
        return isVolatile;
    }

    /**
     * Returns {@code true} if this is a persistent (non-volatile) condition.
     *
     * @return {@code true} for non-volatile conditions
     */
    public boolean isNonVolatile() {
        return !isVolatile;
    }

    /**
     * Returns {@code true} if this condition prevents the Pokémon from acting.
     *
     * @return {@code true} for FREEZE, SLEEP, and FLINCH
     */
    public boolean preventsAction() {
        return preventsAction;
    }

    /**
     * Returns {@code true} if this condition deals residual damage each turn.
     *
     * @return {@code true} for BURN, POISON, and TOXIC
     */
    public boolean dealsResidualDamage() {
        return dealsResidualDamage;
    }
}
