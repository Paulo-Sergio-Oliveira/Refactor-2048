package com.bulenkov.game2048;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the Factory Method pattern: {@link ClassicTileFactory} must
 * reproduce the original game's spawn policy (90% chance of 2, 10% chance
 * of 4), something that was previously inlined inside {@code addTile()} and
 * impossible to exercise without going through {@code Math.random()}
 * directly.
 */
class TileFactoryTest {

  @Test
  @DisplayName("createEmptyTile sempre produz uma peca vazia")
  void createEmptyTileIsAlwaysEmpty() {
    TileFactory factory = new ClassicTileFactory();

    assertTrue(factory.createEmptyTile().isEmpty());
  }

  @Test
  @DisplayName("createTile usa exatamente o valor informado")
  void createTileUsesTheGivenValue() {
    TileFactory factory = new ClassicTileFactory();

    assertEquals(8, factory.createTile(8).value);
  }

  @Test
  @DisplayName("ClassicTileFactory so sorteia os valores 2 ou 4")
  void classicFactoryOnlySpawnsTwosAndFours() {
    TileFactory factory = new ClassicTileFactory();

    for (int i = 0; i < 500; i++) {
      int value = factory.createRandomTile().value;
      assertTrue(value == 2 || value == 4, "valor sorteado inesperado: " + value);
    }
  }
}
