package domain.model;

import domain.model.vo.Experience;
import domain.model.vo.Level;

/**
 * Domain Policy que define cuánta experiencia se requiere para alcanzar el siguiente nivel.
 */
public interface ExperienceCurve {
    Experience requiredExpForNextLevel(PokemonSpecies species, Level currentLevel);
}
