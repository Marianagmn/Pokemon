package domain.model;

import domain.enums.MoveCategory;
import domain.enums.PokemonType;
import domain.model.vo.StatusApplication;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Immutable domain model representing a Pokémon move template.
 *
 * <p>A Move describes the static definition of an attack or action.
 * Dynamic state (remaining PP) belongs to the Pokémon entity, not here.
 *
 * <p>Invariants:
 * <ul>
 *   <li>PHYSICAL/SPECIAL moves must have {@code power > 0}</li>
 *   <li>STATUS moves must have {@code power == 0}</li>
 *   <li>STATUS moves must define a primary status application</li>
 *   <li>Damaging moves with status must use secondary effects</li>
 *   <li>{@code accuracy} in [0, 100] or {@value #NEVER_MISS}</li>
 *   <li>{@code maxPp > 0}</li>
 * </ul>
 *
 * <p><b>Design note on PP:</b> {@code maxPp} represents the base PP of the
 * move template. The actual usable PP (after PP Up items) is tracked
 * in the Pokémon entity, not here.
 *
 * @param name              the move's display name
 * @param type              the elemental type (determines STAB and effectiveness)
 * @param category          PHYSICAL, SPECIAL, or STATUS (determines which stats are used)
 * @param power             base power (0 for non-damaging moves)
 * @param accuracy          hit chance in percent, or {@value #NEVER_MISS} for moves that never miss
 * @param maxPp             maximum Power Points (number of times this move can be used)
 * @param priority          turn order modifier (0 = normal, positive = faster, negative = slower)
 * @param statusApplication the status effect this move may inflict (empty if none)
 *
 * @see MoveCategory
 * @see PokemonType
 * @see StatusApplication
 */
public record Move(
        String name,
        PokemonType type,
        MoveCategory category,
        int power,
        int accuracy,
        int maxPp,
        int priority,
        Optional<StatusApplication> statusApplication) {

    /** Sentinel value indicating a move that bypasses accuracy checks. */
    public static final int NEVER_MISS = -1;

    public Move {
        Objects.requireNonNull(name, "Move name cannot be null");
        Objects.requireNonNull(type, "Move type cannot be null");
        Objects.requireNonNull(category, "Move category cannot be null");
        Objects.requireNonNull(statusApplication,
                "Status application cannot be null (use Optional.empty())");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Move name cannot be blank");
        }
        if (power < 0) {
            throw new IllegalArgumentException(
                    "Power must be >= 0, but was: " + power);
        }
        if (accuracy != NEVER_MISS && (accuracy < 0 || accuracy > 100)) {
            throw new IllegalArgumentException(
                    "Accuracy must be between 0 and 100 (or NEVER_MISS), but was: " + accuracy);
        }
        if (maxPp <= 0) {
            throw new IllegalArgumentException(
                    "Max PP must be > 0, but was: " + maxPp);
        }
        if (category.isDamaging() && power == 0) {
            throw new IllegalArgumentException(
                    "Damaging moves (PHYSICAL/SPECIAL) must have power > 0");
        }
        if (!category.isDamaging() && power > 0) {
            throw new IllegalArgumentException(
                    "Status moves cannot have base power, but was: " + power);
        }

        // ── Cross-validation: category ↔ statusApplication ──
        statusApplication.ifPresent(status -> {
            if (category.isDamaging() && status.isPrimary()) {
                throw new IllegalArgumentException(
                        "Damaging moves cannot have primary status effects");
            }
        });

        if (!category.isDamaging()) {
            StatusApplication status = statusApplication.orElseThrow(() ->
                    new IllegalArgumentException("Status moves must define a status application"));

            if (!status.isPrimary()) {
                throw new IllegalArgumentException(
                        "Status moves must use primary status effects");
            }
        }
    }

    // ── Factory methods ──────────────────────────────────────

    /**
     * Creates a damaging move with no status effect.
     *
     * @param name     the move name
     * @param type     the elemental type
     * @param category PHYSICAL or SPECIAL
     * @param power    base power
     * @param accuracy hit chance (0–100)
     * @param maxPp    maximum PP
     * @return a new Move instance
     */
    public static Move damaging(String name, PokemonType type, MoveCategory category,
                                int power, int accuracy, int maxPp) {
        return new Move(name, type, category, power, accuracy, maxPp, 0,
                Optional.empty());
    }

    /**
     * Creates a damaging move that may inflict a status condition.
     *
     * @param name   the move name
     * @param type   the elemental type
     * @param category PHYSICAL or SPECIAL
     * @param power  base power
     * @param accuracy hit chance (0–100)
     * @param maxPp  maximum PP
     * @param status the status application (must be secondary)
     * @return a new Move instance
     * @throws IllegalArgumentException if the status is primary
     */
    public static Move damagingWithStatus(String name, PokemonType type, MoveCategory category,
                                          int power, int accuracy, int maxPp,
                                          StatusApplication status) {
        Objects.requireNonNull(status, "Status application cannot be null");

        if (status.isPrimary()) {
            throw new IllegalArgumentException(
                    "Damaging moves must use secondary status effects");
        }

        return new Move(name, type, category, power, accuracy, maxPp, 0,
                Optional.of(status));
    }

    /**
     * Creates a status move (no direct damage, guaranteed primary status).
     *
     * @param name     the move name
     * @param type     the elemental type
     * @param accuracy hit chance (0–100 or {@value #NEVER_MISS})
     * @param maxPp    maximum PP
     * @param status   the status application (must be primary)
     * @return a new Move instance
     * @throws IllegalArgumentException if the status is not primary
     */
    public static Move status(String name, PokemonType type,
                              int accuracy, int maxPp, StatusApplication status) {
        Objects.requireNonNull(status, "Status application cannot be null");
        return new Move(name, type, MoveCategory.STATUS, 0, accuracy, maxPp, 0,
                Optional.of(status));
    }

    /**
     * Creates a priority move (e.g., Quick Attack).
     *
     * @param name     the move name
     * @param type     the elemental type
     * @param category PHYSICAL or SPECIAL
     * @param power    base power
     * @param accuracy hit chance (0–100)
     * @param maxPp    maximum PP
     * @param priority turn order modifier (positive = faster)
     * @return a new Move instance
     */
    public static Move priority(String name, PokemonType type, MoveCategory category,
                                int power, int accuracy, int maxPp, int priority) {
        return new Move(name, type, category, power, accuracy, maxPp, priority,
                Optional.empty());
    }

    // ── Domain queries ───────────────────────────────────────

    /** Returns {@code true} if this move deals direct damage. */
    public boolean isDamaging() {
        return category.isDamaging();
    }

    /** Returns {@code true} if this move may inflict a status condition. */
    public boolean hasStatusEffect() {
        return statusApplication.isPresent();
    }

    /** Returns {@code true} if this move always hits (bypasses accuracy check). */
    public boolean neverMisses() {
        return accuracy == NEVER_MISS;
    }

    /** Returns {@code true} if this move has increased or decreased priority. */
    public boolean hasPriority() {
        return priority != 0;
    }

    /**
     * Returns {@code true} if this move receives STAB (Same Type Attack Bonus)
     * for a Pokémon with the given types.
     *
     * <p>A move gets STAB if its type matches <i>any</i> of the Pokémon's types.
     * Accepts a {@code Set} to support mono-type, dual-type, and potential
     * future multi-type mechanics.
     *
     * @param pokemonTypes the Pokémon's type(s) — must not be empty
     * @return {@code true} if the move type matches any of the Pokémon's types
     * @throws IllegalArgumentException if the set is empty
     */
    public boolean isStab(Set<PokemonType> pokemonTypes) {
        Objects.requireNonNull(pokemonTypes, "Pokémon types cannot be null");
        if (pokemonTypes.isEmpty()) {
            throw new IllegalArgumentException("Pokémon must have at least one type");
        }
        return pokemonTypes.contains(this.type);
    }
}
