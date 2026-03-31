package domain.model;

import domain.enums.AbilityTrigger;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable domain model representing a Pokémon ability.
 *
 * <p>An Ability is a passive effect that activates automatically
 * during battle or in the overworld. Each Pokémon species has
 * one or more possible abilities.
 *
 * <p>This record defines the <i>template</i> (static data).
 * The actual behavioral effects are handled by the battle system
 * and domain services — keeping this model pure and decoupled.
 *
 * <p><b>Design note:</b> Trigger contexts are modeled as a
 * {@code Set<AbilityTrigger>} instead of boolean flags, following
 * the Open/Closed Principle. Adding a new trigger context only
 * requires adding a new enum constant.
 *
 * @param name        the ability's display name (e.g., "Overgrow")
 * @param description a short explanation of the ability's effect
 * @param triggers    the set of contexts where this ability activates
 *
 * @see AbilityTrigger
 */
public record Ability(
        String name,
        String description,
        Set<AbilityTrigger> triggers) {

    public Ability {
        Objects.requireNonNull(name, "Ability name cannot be null");
        Objects.requireNonNull(description, "Ability description cannot be null");
        Objects.requireNonNull(triggers, "Triggers cannot be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Ability name cannot be blank");
        }
        if (description.isBlank()) {
            throw new IllegalArgumentException("Ability description cannot be blank");
        }
        if (triggers.isEmpty()) {
            throw new IllegalArgumentException("Ability must have at least one trigger");
        }

        // Validate no null elements, then defensive copy for immutability
        triggers.forEach(t -> Objects.requireNonNull(t, "Trigger element cannot be null"));
        triggers = Collections.unmodifiableSet(EnumSet.copyOf(triggers));
    }

    // ── Factory methods ──────────────────────────────────────

    /**
     * Creates an ability with a single trigger context.
     *
     * @param name        the ability name
     * @param description the ability description
     * @param trigger     the activation context
     * @return a new Ability
     */
    public static Ability of(String name, String description, AbilityTrigger trigger) {
        return new Ability(name, description, EnumSet.of(trigger));
    }

    /**
     * Creates an ability with multiple trigger contexts.
     *
     * @param name        the ability name
     * @param description the ability description
     * @param triggers    the activation contexts
     * @return a new Ability
     */
    public static Ability of(String name, String description, Set<AbilityTrigger> triggers) {
        return new Ability(name, description, triggers);
    }

    // ── Domain queries ───────────────────────────────────────

    /**
     * Returns {@code true} if this ability activates during battle
     * (has at least one battle trigger).
     *
     * @return {@code true} if any trigger is a battle trigger
     */
    public boolean triggersInBattle() {
        return triggers.stream().anyMatch(AbilityTrigger::isBattleTrigger);
    }

    /**
     * Returns {@code true} if this ability has an overworld effect.
     *
     * @return {@code true} if triggers contain {@link AbilityTrigger#OVERWORLD}
     */
    public boolean triggersInOverworld() {
        return triggers.contains(AbilityTrigger.OVERWORLD);
    }

    /**
     * Returns {@code true} if this ability responds to the given trigger.
     *
     * @param trigger the trigger to check
     * @return {@code true} if this ability has that trigger
     */
    public boolean hasTrigger(AbilityTrigger trigger) {
        return triggers.contains(trigger);
    }
}
