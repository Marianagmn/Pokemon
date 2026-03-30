package domain.enums;

/**
 * Enumeration of all 18 Pokémon types.
 *
 * <p>Each type participates in the type effectiveness matrix,
 * which determines damage multipliers during battle.
 *
 * @see domain.battle.TypeEffectivenessMatrix
 */
public enum PokemonType {

    NORMAL,
    FIRE,
    WATER,
    ELECTRIC,
    GRASS,
    ICE,
    FIGHTING,
    POISON,
    GROUND,
    FLYING,
    PSYCHIC,
    BUG,
    ROCK,
    GHOST,
    DRAGON,
    DARK,
    STEEL,
    FAIRY;

    /**
     * Returns a user-friendly display name (e.g., "Fire" instead of "FIRE").
     *
     * @return the type name in title case
     */
    public String displayName() {
        String name = name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
