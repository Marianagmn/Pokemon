package domain.model;

import domain.enums.ItemType;
import domain.model.vo.Money;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Immutable domain model representing an item in the Pokémon world.
 *
 * <p>An Item is a <b>template</b> that describes the static properties of
 * an inventory object (the concept of "Potion"). Actual usage effects
 * (healing, capturing, etc.) are handled by application services.
 *
 * <p><b>Design note (Item vs ItemStack):</b> This record represents
 * <i>what</i> an item is, not <i>how many</i> you have. Quantity tracking
 * belongs in an {@code ItemStack} or {@code InventorySlot} — a separate
 * concept that references this template. This allows a single Item instance
 * to be shared across multiple inventory slots (flyweight).
 *
 * <p>Invariants:
 * <ul>
 *   <li>Name and description must not be blank</li>
 *   <li>Key items must have a sell price of 0 (unsellable)</li>
 *   <li>Sellable items with a buy price must have a sell price > 0</li>
 * </ul>
 *
 * @param name        the item's display name (e.g., "Potion")
 * @param type        the item category (determines bag placement and usability)
 * @param description a short explanation of the item's effect
 * @param buyPrice    the price to buy from a shop
 * @param sellPrice   the price when sold to a shop
 *
 * @see ItemType
 * @see domain.model.vo.Money
 */
public record Item(
        String name,
        ItemType type,
        String description,
        Money buyPrice,
        Money sellPrice) {

    private static final BigDecimal SELL_RATIO = BigDecimal.valueOf(0.5);

    public Item {
        Objects.requireNonNull(name, "Item name cannot be null");
        Objects.requireNonNull(type, "Item type cannot be null");
        Objects.requireNonNull(description, "Item description cannot be null");
        Objects.requireNonNull(buyPrice, "Buy price cannot be null");
        Objects.requireNonNull(sellPrice, "Sell price cannot be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Item name cannot be blank");
        }
        if (description.isBlank()) {
            throw new IllegalArgumentException("Item description cannot be blank");
        }
        if (!type.isSellable() && sellPrice.isPositive()) {
            throw new IllegalArgumentException(
                    "Unsellable items (e.g., Key Items) must have a sell price of 0");
        }
        if (type.isSellable() && sellPrice.isZero() && buyPrice.isPositive()) {
            throw new IllegalArgumentException(
                    "Sellable items with a buy price must have a sell price > 0");
        }
        if (buyPrice.isZero() && sellPrice.isPositive()) {
            throw new IllegalArgumentException(
                    "Item cannot have a sell price without a buy price");
        }
    }

    // ── Factory methods ──────────────────────────────────────

    /**
     * Creates a standard shop item with sell price = half buy price.
     *
     * <p>Uses {@link BigDecimal} for precise calculation of the sell ratio.
     *
     * @param name        the item name
     * @param type        the item category
     * @param description the item description
     * @param buyPrice    the shop buy price
     * @return a new Item with sell price = buyPrice / 2
     */
    public static Item shopItem(String name, ItemType type,
                                String description, Money buyPrice) {
        Money sellPrice = buyPrice.multiply(SELL_RATIO);
        return new Item(name, type, description, buyPrice, sellPrice);
    }

    /**
     * Creates a key item (unsellable, unpurchasable).
     *
     * @param name        the item name
     * @param description the item description
     * @return a new Key Item with both prices at 0
     */
    public static Item keyItem(String name, String description) {
        return new Item(name, ItemType.KEY_ITEM, description,
                Money.zero(), Money.zero());
    }

    // ── Domain queries ───────────────────────────────────────

    /** Returns {@code true} if this item can be sold at a shop. */
    public boolean isSellable() {
        return type.isSellable() && sellPrice.isPositive();
    }

    /** Returns {@code true} if this item can be used during battle. */
    public boolean isUsableInBattle() {
        return type.isUsableInBattle();
    }

    /** Returns {@code true} if this item is a key/story item. */
    public boolean isKeyItem() {
        return type == ItemType.KEY_ITEM;
    }
}
