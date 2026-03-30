package domain.enums;

import java.util.Set;

/**
 * Weather conditions that affect the battlefield.
 *
 * <p>Weather modifies damage calculations, enables/disables certain
 * moves, and may deal residual damage at the end of each turn
 * to Pokémon that are not immune.
 *
 * <p>All behavioral data is declared in the constructor
 * (Open/Closed Principle): adding a new weather condition requires
 * no changes to existing query methods.
 *
 * @see domain.battle.pipeline.WeatherModifier
 */
public enum WeatherType {

    /** No active weather; default state. */
    CLEAR(Set.of(), null, null, 1.0, 1.0),

    /** Boosts Fire moves by 50%, weakens Water moves by 50%. */
    HARSH_SUNLIGHT(Set.of(), PokemonType.FIRE, PokemonType.WATER, 1.5, 0.5),

    /** Boosts Water moves by 50%, weakens Fire moves by 50%. */
    RAIN(Set.of(), PokemonType.WATER, PokemonType.FIRE, 1.5, 0.5),

    /** Deals 1/16 max HP per turn to non-immune types. Boosts Sp.Def of Rock types by 50%. */
    SANDSTORM(Set.of(PokemonType.ROCK, PokemonType.GROUND, PokemonType.STEEL), null, null, 1.0, 1.0),

    /** Deals 1/16 max HP per turn to non-Ice types. */
    HAIL(Set.of(PokemonType.ICE), null, null, 1.0, 1.0);

    private final Set<PokemonType> immuneTypes;
    private final PokemonType boostedType;
    private final PokemonType weakenedType;
    private final double boostMultiplier;
    private final double weakenMultiplier;

    WeatherType(Set<PokemonType> immuneTypes,
                PokemonType boostedType, PokemonType weakenedType,
                double boostMultiplier, double weakenMultiplier) {
        this.immuneTypes = immuneTypes;
        this.boostedType = boostedType;
        this.weakenedType = weakenedType;
        this.boostMultiplier = boostMultiplier;
        this.weakenMultiplier = weakenMultiplier;
    }

    /**
     * Determines whether this weather deals residual damage to a Pokémon
     * with a single type.
     *
     * @param targetType the type of the Pokémon being evaluated
     * @return {@code true} if this weather deals chip damage to that type
     */
    public boolean dealsDamageTo(PokemonType targetType) {
        if (immuneTypes.isEmpty()) {
            return false;
        }
        return !immuneTypes.contains(targetType);
    }

    /**
     * Determines whether this weather deals residual damage to a dual-type Pokémon.
     *
     * <p>A Pokémon is immune if <i>either</i> of its types is in the immunity set.
     *
     * @param primaryType   the Pokémon's primary type
     * @param secondaryType the Pokémon's secondary type (may be {@code null} for mono-type)
     * @return {@code true} if this weather deals chip damage to that Pokémon
     */
    public boolean dealsDamageTo(PokemonType primaryType, PokemonType secondaryType) {
        if (immuneTypes.isEmpty()) {
            return false;
        }
        if (immuneTypes.contains(primaryType)) {
            return false;
        }
        return secondaryType == null || !immuneTypes.contains(secondaryType);
    }

    /**
     * Returns {@code true} if any weather is active.
     *
     * @return {@code false} only for CLEAR
     */
    public boolean isActive() {
        return this != CLEAR;
    }

    /**
     * Returns the damage multiplier this weather applies to a move of the given type.
     *
     * @param moveType the type of the move being used
     * @return 1.5 for boosted, 0.5 for weakened, 1.0 for neutral
     */
    public double moveMultiplier(PokemonType moveType) {
        if (moveType == boostedType) {
            return boostMultiplier;
        }
        if (moveType == weakenedType) {
            return weakenMultiplier;
        }
        return 1.0;
    }
}
