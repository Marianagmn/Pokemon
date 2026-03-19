package domain.model;

import domain.model.vo.*;

import java.util.Objects;
import java.util.UUID;

/**
 * Entity que representa un Pokémon dentro del dominio.
 *
 * <p>
 * Encapsula estado mutable y comportamiento de combate.
 */
public class Pokemon {

    private final UUID id;
    private final PokemonSpecies species;

    private Level level;
    private Experience experience;
    private Stats stats;
    private HP hp;

    public Pokemon(
            UUID id,
            PokemonSpecies species,
            Level level,
            Experience experience,
            Stats stats,
            HP hp) {
        this.id = Objects.requireNonNull(id);
        this.species = Objects.requireNonNull(species);
        this.level = Objects.requireNonNull(level);
        this.experience = Objects.requireNonNull(experience);
        this.stats = Objects.requireNonNull(stats);
        this.hp = Objects.requireNonNull(hp);

        validateInvariants();
    }

    // =========================
    // Domain behavior
    // =========================

    /**
     * Aplica daño al Pokémon.
     */
    public void takeDamage(int damage) {
        this.hp = this.hp.takeDamage(damage);
    }

    /**
     * Cura al Pokémon.
     */
    public void heal(int amount) {
        this.hp = this.hp.heal(amount);
    }

    /**
     * Cura completamente.
     */
    public void fullHeal() {
        this.hp = this.hp.fullHeal();
    }

    /**
     * Gana experiencia delegando las reglas de nivel y stats a servicios de dominio.
     */
    public void gainExperience(int amount, ExperienceCurve curve, StatCalculator statCalculator) {
        this.experience = this.experience.gain(amount);

        // Se usa Inyección de Dependencias (Double Dispatch) para que el Pokémon no conozca la matemática específica
        while (!level.isMax() && experience.compareTo(curve.requiredExpForNextLevel(this.species, this.level)) >= 0) {
            // Sube de nivel
            this.level = this.level.next();

            // Recalcula los stats en base a su nueva forma/nivel
            this.stats = statCalculator.calculate(this.species, this.level);

            // Restaura el HP completamente al subir de nivel (decisión de diseño tipo juego base)
            this.hp = HP.full(this.stats.maxHp());
        }
    }

    // =========================
    // State queries
    // =========================

    public boolean isFainted() {
        return hp.isFainted();
    }

    public boolean isAlive() {
        return hp.isAlive();
    }

    public boolean isAtFullHealth() {
        return hp.isFull();
    }

    // =========================
    // Getters (controlados)
    // =========================

    public UUID getId() {
        return id;
    }

    public PokemonSpecies getSpecies() {
        return species;
    }

    public Level getLevel() {
        return level;
    }

    public Experience getExperience() {
        return experience;
    }

    public Stats getStats() {
        return stats;
    }

    public HP getHp() {
        return hp;
    }

    // =========================
    // Invariants
    // =========================

    private void validateInvariants() {
        if (hp.max() != stats.maxHp()) {
            throw new IllegalStateException(
                    "HP max must match stats maxHp");
        }
    }
}