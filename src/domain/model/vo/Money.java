package domain.model.vo;

import domain.exceptions.InsufficientFundsException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object representing in-game currency (Pokémon Dollars).
 *
 * <p>Domain properties:
 * <ul>
 *   <li>Bounded between {@value #MIN} and {@value #MAX}</li>
 *   <li>Integer-based (no decimals)</li>
 *   <li>Immutable — all operations return new instances</li>
 *   <li>{@link #add} and {@link #multiply} saturate at {@value #MAX}</li>
 *   <li>{@link #subtract} throws {@link InsufficientFundsException}
 *       (explicit domain decision: spending more than you have is an error,
 *       not a silent saturation)</li>
 * </ul>
 */
public record Money(int amount) implements Comparable<Money> {

    public static final int MIN = 0;
    public static final int MAX = 999_999;

    public Money {
        if (amount < MIN || amount > MAX) {
            throw new IllegalArgumentException(
                    "Money must be between %d and %d, but was: %d"
                            .formatted(MIN, MAX, amount));
        }
    }

    // ── Factory methods ──────────────────────────────────────

    public static Money of(int amount) {
        return new Money(amount);
    }

    public static Money zero() {
        return new Money(MIN);
    }

    public static Money max() {
        return new Money(MAX);
    }

    // ── Arithmetic operations ────────────────────────────────

    /**
     * Adds two money amounts together.
     *
     * <p>Saturates at {@value #MAX} — excess money is silently discarded.
     *
     * @param other the money to add
     * @return a new Money instance with the combined value, capped at {@value #MAX}
     */
    public Money add(Money other) {
        Objects.requireNonNull(other, "Money to add cannot be null");
        int result = (int) Math.min((long) this.amount + other.amount, MAX);
        return result == this.amount ? this : new Money(result);
    }

    /**
     * Subtracts money from this amount.
     *
     * <p><b>Design decision:</b> Unlike {@link #add}, subtraction does NOT
     * saturate. Spending more than you have is a domain error — the caller
     * should check {@link #canAfford} first.
     *
     * @param other the money to subtract
     * @return a new Money instance with the reduced value
     * @throws InsufficientFundsException if insufficient funds
     */
    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Money to subtract cannot be null");

        if (this.amount < other.amount) {
            throw new InsufficientFundsException(this.amount, other.amount);
        }
        return new Money(this.amount - other.amount);
    }

    /**
     * Multiplies money by a precise factor (e.g., discounts, price multipliers).
     *
     * <p>Uses {@link RoundingMode#DOWN} (truncation) for deterministic behavior.
     * Saturates at {@value #MAX}.
     *
     * @param factor the multiplier (must be {@code >= 0})
     * @return a new Money instance with the scaled value
     * @throws IllegalArgumentException if factor is negative
     */
    public Money multiply(BigDecimal factor) {
        Objects.requireNonNull(factor, "Factor cannot be null");
        if (factor.signum() < 0) {
            throw new IllegalArgumentException(
                    "Factor cannot be negative: " + factor);
        }
        int result = BigDecimal.valueOf(amount)
                .multiply(factor)
                .setScale(0, RoundingMode.DOWN)
                .min(BigDecimal.valueOf(MAX))
                .intValue();
        return result == this.amount ? this : new Money(result);
    }

    /**
     * Convenience overload that accepts a {@code double}.
     *
     * <p>Delegates to {@link #multiply(BigDecimal)} for precision.
     *
     * @param factor the multiplier (must be {@code >= 0})
     * @return a new Money instance with the scaled value
     * @throws IllegalArgumentException if factor is negative
     */
    public Money multiply(double factor) {
        return multiply(BigDecimal.valueOf(factor));
    }

    // ── Domain queries ───────────────────────────────────────

    /**
     * Checks whether this amount is sufficient to cover a cost.
     *
     * @param cost the cost to check against
     * @return {@code true} if this amount is greater than or equal to the cost
     */
    public boolean canAfford(Money cost) {
        Objects.requireNonNull(cost, "Cost cannot be null");
        return this.amount >= cost.amount;
    }

    public boolean isZero() {
        return amount == MIN;
    }

    public boolean isPositive() {
        return amount > MIN;
    }

    // ── Comparison ───────────────────────────────────────────

    @Override
    public int compareTo(Money other) {
        Objects.requireNonNull(other, "Money to compare cannot be null");
        return Integer.compare(this.amount, other.amount);
    }

    public boolean isGreaterThan(Money other) {
        return this.compareTo(other) > 0;
    }

    public boolean isLessThan(Money other) {
        return this.compareTo(other) < 0;
    }

    public boolean isGreaterOrEqual(Money other) {
        return this.compareTo(other) >= 0;
    }

    // ── Display ──────────────────────────────────────────────

    @Override
    public String toString() {
        return amount + "₽";
    }
}