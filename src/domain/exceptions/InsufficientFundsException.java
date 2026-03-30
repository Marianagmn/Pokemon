package domain.exceptions;

/**
 * Domain exception thrown when a monetary operation fails
 * due to insufficient funds.
 *
 * <p>This is a domain-specific exception (not a generic
 * {@link IllegalStateException}), providing richer semantics
 * for the Pokémon economy bounded context.
 */
public class InsufficientFundsException extends RuntimeException {

    private final int available;
    private final int required;

    public InsufficientFundsException(int available, int required) {
        super("Insufficient funds: have %d₽ but tried to spend %d₽"
                .formatted(available, required));
        this.available = available;
        this.required = required;
    }

    public int getAvailable() {
        return available;
    }

    public int getRequired() {
        return required;
    }

    public int getDeficit() {
        return required - available;
    }
}
