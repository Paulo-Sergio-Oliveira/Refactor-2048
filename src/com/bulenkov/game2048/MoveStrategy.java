package com.bulenkov.game2048;

/**
 * Strategy pattern: encapsulates the algorithm used to move/merge the board
 * in a given direction, so that new ways of moving the board can be added
 * without changing {@link Game2048}.
 */
interface MoveStrategy {
  /**
   * Applies this strategy to the given game's board.
   *
   * @return {@code true} if the board changed as a result of the move.
   */
  boolean move(Game2048 game);
}
