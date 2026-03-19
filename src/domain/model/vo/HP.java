package domain.model.vo;

/**
 * Value Object que representa los puntos de salud (HP) de una entidad.
 *
 * <p>
 * Propiedades del dominio:
 * <ul>
 * <li>0 ≤ current ≤ max</li>
 * <li>max > 0</li>
 * <li>El daño reduce HP hasta un mínimo de 0</li>
 * <li>La curación incrementa HP hasta un máximo de max</li>
 * </ul>
 */
public record HP(int current, int max) {

    /**
     * Constructor con validación de invariantes.
     */
    public HP {
        if (max <= 0) {
            throw new IllegalArgumentException(
                    "Max HP must be greater than 0, but was: " + max);
        }

        if (current < 0 || current > max) {
            throw new IllegalArgumentException(
                    "HP must be between 0 and %d, but was: %d"
                            .formatted(max, current));
        }
    }

    // =========================
    // Factory methods
    // =========================

    /**
     * Crea HP completamente lleno.
     */
    public static HP full(int max) {
        return new HP(max, max);
    }

    /**
     * Crea HP con valor actual específico.
     */
    public static HP of(int current, int max) {
        return new HP(current, max);
    }

    /**
     * HP en estado debilitado (0).
     */
    public static HP zero(int max) {
        return new HP(0, max);
    }

    // =========================
    // Domain behavior
    // =========================

    /**
     * Aplica daño.
     *
     * <p>
     * El HP nunca baja de 0.
     */
    public HP takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException(
                    "Damage must be positive, but was: " + damage);
        }

        int newCurrent = Math.max(0, current - damage);
        if (newCurrent == this.current) return this;
        return new HP(newCurrent, max);
    }

    /**
     * Aplica curación.
     *
     * <p>
     * El HP nunca supera el máximo.
     */
    public HP heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Heal amount must be positive, but was: " + amount);
        }

        int newCurrent = Math.min(max, current + amount);
        if (newCurrent == this.current) return this;
        return new HP(newCurrent, max);
    }

    /**
     * Cura completamente.
     */
    public HP fullHeal() {
        return new HP(max, max);
    }

    // =========================
    // State queries
    // =========================

    /**
     * Indica si el Pokémon está debilitado.
     */
    public boolean isFainted() {
        return current == 0;
    }

    /**
     * Indica si está con vida.
     */
    public boolean isAlive() {
        return current > 0;
    }

    /**
     * Indica si está completamente curado.
     */
    public boolean isFull() {
        return current == max;
    }

    /**
     * Porcentaje de vida (0.0 - 1.0)
     */
    public double percentage() {
        return (double) current / max;
    }

    /**
     * Indica si un daño causará debilitamiento.
     */
    public boolean willFaint(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException(
                    "Damage must be positive, but was: " + damage);
        }

        return current - damage <= 0;
    }
}