package domain.enums;

/**
 * Defines when and how a Pokémon ability activates.
 *
 * <p>Used as a {@code Set<AbilityTrigger>} in {@link domain.model.Ability}
 * instead of boolean flags, following the Open/Closed Principle.
 *
 * <p>Adding a new trigger context requires only adding a new enum constant —
 * no changes to existing code.
 */
public enum AbilityTrigger {

    /** Activates when the Pokémon enters battle (e.g., Intimidate, Drizzle). */
    ON_SWITCH_IN,

    /** Activates when the Pokémon deals damage (e.g., Sheer Force, Poison Touch). */
    ON_DAMAGE_DEALT,

    /** Activates when the Pokémon receives damage (e.g., Rough Skin, Flame Body). */
    ON_DAMAGE_RECEIVED,

    /** Activates when a status condition is applied (e.g., Synchronize). */
    ON_STATUS_APPLIED,

    /** Activates when stats are changed (e.g., Competitive, Defiant). */
    ON_STAT_CHANGE,

    /** Activates at the end of each turn (e.g., Speed Boost, Poison Heal). */
    ON_TURN_END,

    /** Activates when the Pokémon's HP drops below a threshold (e.g., Overgrow, Blaze). */
    ON_LOW_HP,

    /** Activates when the Pokémon faints (e.g., Aftermath). */
    ON_FAINT,

    /** Activates while the Pokémon is on the field — passive aura (e.g., Air Lock). */
    ON_FIELD,

    /** Activates in the overworld outside of battle (e.g., Flame Body for hatching). */
    OVERWORLD;

    /**
     * Returns {@code true} if this trigger activates during battle.
     *
     * @return {@code true} for all triggers except {@link #OVERWORLD}
     */
    public boolean isBattleTrigger() {
        return this != OVERWORLD;
    }
}
