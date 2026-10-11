package com.bulenkov.game2048;

/**
 * Factory Method pattern: centralizes creation of {@link Game2048.Tile}
 * instances, which were previously scattered across {@code Game2048}
 * ({@code resetGame}, {@code addTile}, {@code mergeLine}, {@code ensureSize}).
 * Subclasses override {@link #createRandomTile()} to vary the policy used to
 * spawn a new tile (the factory method), while the creation of empty and
 * fixed-value tiles is shared, concrete behavior.
 */
abstract class TileFactory {
  Game2048.Tile createEmptyTile() {
    return new Game2048.Tile();
  }

  Game2048.Tile createTile(int value) {
    return new Game2048.Tile(value);
  }

  /**
   * Factory method: creates the tile that spawns on the board after a move.
   * Overridable so the spawn policy (which values are possible and with
   * what probability) can be replaced without changing {@link Game2048}.
   */
  abstract Game2048.Tile createRandomTile();
}
