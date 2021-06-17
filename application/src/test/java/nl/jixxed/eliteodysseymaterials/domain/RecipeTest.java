package nl.jixxed.eliteodysseymaterials.domain;

import nl.jixxed.eliteodysseymaterials.enums.Asset;
import nl.jixxed.eliteodysseymaterials.enums.Data;
import nl.jixxed.eliteodysseymaterials.enums.Good;
import nl.jixxed.eliteodysseymaterials.enums.Material;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class RecipeTest {

    @Test
    void getMaterialCollection() {

        final Recipe recipe = new Recipe(Map.of(
                Good.IONISEDGAS, 10,
                Data.BIOMETRICDATA, 5,
                Data.COMBATANTPERFORMANCE, 10,
                Asset.VISCOELASTICPOLYMER, 10,
                Asset.RDX, 10
        ));
        Assertions.assertThat(recipe.getMaterialCollection(Asset.class)).containsExactlyInAnyOrderEntriesOf(Map.of(
                Asset.VISCOELASTICPOLYMER, 10,
                Asset.RDX, 10
        ));
        Assertions.assertThat(recipe.getMaterialCollection(Data.class)).containsExactlyInAnyOrderEntriesOf(Map.of(
                Data.BIOMETRICDATA, 5,
                Data.COMBATANTPERFORMANCE, 10
        ));
        Assertions.assertThat(recipe.getMaterialCollection(Good.class)).containsExactlyInAnyOrderEntriesOf(Map.of(
                Good.IONISEDGAS, 10
        ));
        Assertions.assertThat(recipe.getMaterialCollection(Material.class)).containsExactlyInAnyOrderEntriesOf(Map.of(
                Good.IONISEDGAS, 10,
                Data.BIOMETRICDATA, 5,
                Data.COMBATANTPERFORMANCE, 10,
                Asset.VISCOELASTICPOLYMER, 10,
                Asset.RDX, 10
        ));
    }
}