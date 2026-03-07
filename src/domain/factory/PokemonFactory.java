package domain.factory;

import application.ports.PokemonSpeciesRepositoryPort;
import domain.model.Pokemon;
import domain.model.PokemonSpecies;

public class PokemonFactory {

    private final PokemonSpeciesRepositoryPort speciesRepository;

    public PokemonFactory(PokemonSpeciesRepositoryPort speciesRepository) {
        this.speciesRepository = speciesRepository;
    }

    public Pokemon createPokemon(String speciesName, int targetLevel) {
        // En lugar de instanciar un new Pikachu(), pedimos la base estática al
        // repositorio inyectado
        PokemonSpecies baseSpecies = speciesRepository.findByName(speciesName);

        if (baseSpecies == null) {
            throw new IllegalArgumentException("Especie desconocida: " + speciesName);
        }

        // Aquí iría la lógica adicional (generación de IVs, cálculo de Stats reales
        // según el Level VO)
        return new Pokemon(baseSpecies, targetLevel);
    }
}
