package com.bulenkov.game2048;

/**
 * Concrete creator reproducing the original game's spawn policy: a new tile
 * is a 2 with 90% probability, or a 4 with 10% probability.
 */
class ClassicTileFactory extends TileFactory {
  @Override
  Tile createRandomTile() {
    return createTile(Math.random() < 0.9 ? 2 : 4);
  }
}
