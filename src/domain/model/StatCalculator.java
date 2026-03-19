package domain.model;

import domain.model.vo.Level;

/**
 * Domain Service utilizado para proyectar o recalcular los Stats de un Pokémon en base a su nivel.
 */
public interface StatCalculator {
    Stats calculate(PokemonSpecies species, Level level);
    
    // Próximamente: Stats calculate(PokemonSpecies species, Level level, IVs ivs, EVs evs, Nature nature);
}
