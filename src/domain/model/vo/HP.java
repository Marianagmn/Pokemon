package domain.model.vo;

/**
 * Value Object representing a Pokémon's Hit Points (HP).
 *
 * <p>Invariants:
 * <ul>
 *   <li>{@code 0 <= current <= max}</li>
 *   <li>{@code max > 0}</li>
 *   <li>Damage reduces HP to a minimum of 0 (floor)</li>
 *   <li>Healing increases HP to a maximum of {@code max} (ceiling)</li>
 *   <li>A fainted Pokémon (HP = 0) does NOT revive automatically</li>
 * </ul>
 *
 * <p>When {@code max} changes (e.g., on level up), the caller constructs
 * a new HP instance. See {@link #withNewMaxKeepingState(int)}.
 *
 * @param current the current hit points
 * @param max     the maximum hit points
 */
public record HP(int current, int max) {

    public HP {
        if (max <= 0) {
            throw new IllegalArgumentException(
                    "Max HP must be greater than 0, but was: " + max);
        }
        if (current < 0 || current > max) {
            throw new IllegalArgumentException(
                    "Current HP must be between 0 and %d, but was: %d"
                            .formatted(max, current));
        }
    }

    // ── Factory methods ──────────────────────────────────────

    /** Creates HP at full capacity. */
    public static HP full(int max) {
        return new HP(max, max);
    }

    /** Creates HP with a specific current value. */
    public static HP of(int current, int max) {
        return new HP(current, max);
    }

    /** Creates HP in fainted state (current = 0). */
    public static HP fainted(int max) {
        return new HP(0, max);
    }

    // ── Domain behavior ──────────────────────────────────────

    /**
     * Applies damage to this HP.
     *
     * <p>The result is floored at 0 — HP never goes negative.
     * Returns {@code this} if damage doesn't change the current value.
     *
     * @param damage the amount of damage (must be {@code >= 0})
     * @return a new HP instance with reduced current value
     * @throws IllegalArgumentException if damage is negative
     */
    public HP takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException(
                    "Damage must be non-negative, but was: " + damage);
        }
        int newCurrent = Math.max(0, current - damage);
        return newCurrent == this.current ? this : new HP(newCurrent, max);
    }

    /**
     * Applies healing to this HP.
     *
     * <p>The result is capped at {@code max} — HP never exceeds maximum.
     * Returns {@code this} if healing doesn't change the current value.
     *
     * <p><b>Important:</b> Healing a fainted Pokémon (HP = 0) is NOT allowed.
     * Use {@link #revive(int)} instead.
     *
     * @param amount the amount to heal (must be {@code >= 0})
     * @return a new HP instance with increased current value
     * @throws IllegalArgumentException if amount is negative
     * @throws IllegalStateException    if the Pokémon is fainted
     */
    public HP heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Heal amount must be non-negative, but was: " + amount);
        }
        if (isFainted()) {
            throw new IllegalStateException(
                    "Cannot heal a fainted Pokémon. Use revive() instead.");
        }
        int newCurrent = Math.min(max, current + amount);
        return newCurrent == this.current ? this : new HP(newCurrent, max);
    }

    /** Restores HP to full capacity. Returns {@code this} if already full. */
    public HP fullHeal() {
        return isFull() ? this : new HP(max, max);
    }

    /**
     * Revives a fainted Pokémon with the specified HP.
     *
     * <p>This is the only way to restore HP from 0.
     * Mirrors items like Revive (half HP) or Max Revive (full HP).
     *
     * @param hp the HP to restore (must be {@code >= 1} and {@code <= max})
     * @return a new HP instance with the specified current value
     * @throws IllegalArgumentException if hp is out of [1, max]
     * @throws IllegalStateException    if the Pokémon is not fainted
     */
    public HP revive(int hp) {
        if (!isFainted()) {
            throw new IllegalStateException(
                    "Cannot revive a Pokémon that is not fainted.");
        }
        if (hp < 1 || hp > max) {
            throw new IllegalArgumentException(
                    "Revive HP must be between 1 and %d, but was: %d"
                            .formatted(max, hp));
        }
        return new HP(hp, max);
    }

    /**
     * Creates a new HP with an updated maximum, preserving the current state.
     *
     * <p>Behavior:
     * <ul>
     *   <li>If fainted (HP = 0): stays fainted — does NOT auto-revive</li>
     *   <li>If alive: current HP increases by the same delta as max
     *       (consistent with original Pokémon games)</li>
     * </ul>
     *
     * @param newMax the new maximum HP (must be {@code > 0})
     * @return a new HP with adjusted current and max
     * @throws IllegalArgumentException if newMax is not positive
     */
    public HP withNewMaxKeepingState(int newMax) {
        if (newMax <= 0) {
            throw new IllegalArgumentException(
                    "New max HP must be greater than 0, but was: " + newMax);
        }
        if (isFainted()) {
            return new HP(0, newMax);
        }

        int delta = newMax - this.max;
        int newCurrent = this.current + delta;

        // A living Pokémon never drops to 0 from a max change
        newCurrent = Math.max(1, newCurrent);
        newCurrent = Math.min(newCurrent, newMax);

        return new HP(newCurrent, newMax);
    }

    // ── State queries ────────────────────────────────────────

    /** Returns {@code true} if the Pokémon has fainted (HP = 0). */
    public boolean isFainted() {
        return current == 0;
    }

    /** Returns {@code true} if the Pokémon is still conscious (HP > 0). */
    public boolean isAlive() {
        return current > 0;
    }

    /** Returns {@code true} if HP is at full capacity. */
    public boolean isFull() {
        return current == max;
    }

    /**
     * Returns the HP percentage as a value between 0.0 and 1.0.
     *
     * <p>For a Pokémon game, {@code double} precision is more than sufficient.
     *
     * @return the ratio of current HP to max HP
     */
    public double percentage() {
        return (double) current / max;
    }

    /**
     * Determines whether the given damage would cause fainting.
     *
     * @param damage the potential damage amount
     * @return {@code true} if this damage would reduce HP to 0
     * @throws IllegalArgumentException if damage is negative
     */
    public boolean willFaint(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException(
                    "Damage must be non-negative, but was: " + damage);
        }
        return current - damage <= 0;
    }
}