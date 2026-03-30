package domain.enums;

/**
 * Classification of items in the Pokémon inventory system.
 *
 * <p>Each category determines where the item appears in the
 * bag UI and what actions are available for it.
 *
 * <p>Flags are declared in the constructor (Open/Closed Principle):
 * adding a new category requires no changes to existing logic.
 *
 * @see domain.model.Item
 */
public enum ItemType {

    /** Poké Balls used for capturing wild Pokémon. */
    POKEBALL("Poké Balls", true, true),

    /** Restores HP, cures status, or revives fainted Pokémon. */
    MEDICINE("Medicine", true, true),

    /** Held items or consumables used during battle (X Attack, etc.). */
    BATTLE("Battle Items", true, true),

    /** Berries that can be held or used manually. */
    BERRY("Berries", true, true),

    /** TMs and HMs that teach moves to Pokémon. */
    TM("TMs & HMs", true, false),

    /** Evolution stones, Rare Candy, and other stat-boosting items. */
    POWER_UP("Power-Up", true, false),

    /** Story-critical items that cannot be sold or discarded. */
    KEY_ITEM("Key Items", false, false);

    private final String displayName;
    private final boolean sellable;
    private final boolean usableInBattle;

    ItemType(String displayName, boolean sellable, boolean usableInBattle) {
        this.displayName = displayName;
        this.sellable = sellable;
        this.usableInBattle = usableInBattle;
    }

    /**
     * Returns a user-friendly display name for the UI.
     *
     * @return the formatted category name
     */
    public String displayName() {
        return displayName;
    }

    /**
     * Returns {@code true} if items of this type can be sold at a shop.
     *
     * @return {@code false} only for KEY_ITEM
     */
    public boolean isSellable() {
        return sellable;
    }

    /**
     * Returns {@code true} if items of this type can be used during battle.
     *
     * @return {@code true} for POKEBALL, MEDICINE, BATTLE, and BERRY
     */
    public boolean isUsableInBattle() {
        return usableInBattle;
    }
}
