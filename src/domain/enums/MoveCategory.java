package domain.enums;

import domain.model.Stats;

/**
 * Categorization of moves that determines which stats are used
 * in damage calculation.
 *
 * <p>Implements the <b>Strategy Pattern</b> — each category knows
 * which offensive and defensive stats to extract from a {@link Stats}
 * block, eliminating switch/if chains in the damage calculator.
 *
 * <ul>
 *   <li>{@link #PHYSICAL} — uses Attack vs Defense</li>
 *   <li>{@link #SPECIAL}  — uses Sp.Attack vs Sp.Defense</li>
 *   <li>{@link #STATUS}   — does not deal direct damage (returns 0)</li>
 * </ul>
 *
 * @see domain.battle.DamageCalculator
 */
public enum MoveCategory {

    /** Uses the attacker's Attack against the defender's Defense. */
    PHYSICAL {
        @Override
        public int getAttackerStat(Stats stats) { return stats.attack(); }

        @Override
        public int getDefenderStat(Stats stats) { return stats.defense(); }
    },

    /** Uses the attacker's Sp.Attack against the defender's Sp.Defense. */
    SPECIAL {
        @Override
        public int getAttackerStat(Stats stats) { return stats.specialAttack(); }

        @Override
        public int getDefenderStat(Stats stats) { return stats.specialDefense(); }
    },

    /** Does not deal direct damage; applies effects, buffs, or debuffs. */
    STATUS {
        @Override
        public int getAttackerStat(Stats stats) { return 0; }

        @Override
        public int getDefenderStat(Stats stats) { return 0; }
    };

    /**
     * Extracts the relevant offensive stat for this move category.
     *
     * @param stats the attacker's stat block
     * @return the attack value used in damage calculation
     */
    public abstract int getAttackerStat(Stats stats);

    /**
     * Extracts the relevant defensive stat for this move category.
     *
     * @param stats the defender's stat block
     * @return the defense value used in damage calculation
     */
    public abstract int getDefenderStat(Stats stats);

    /**
     * Returns {@code true} if this category deals direct damage.
     *
     * @return {@code true} for PHYSICAL and SPECIAL
     */
    public boolean isDamaging() {
        return this != STATUS;
    }
}
