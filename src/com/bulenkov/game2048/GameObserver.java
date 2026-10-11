package com.bulenkov.game2048;

/**
 * Observer pattern: notified by {@link GameModel} whenever the game state
 * changes, so that the Swing view (or any other consumer) can react — e.g.
 * by repainting — without the model knowing anything about it.
 */
interface GameObserver {
  void onGameChanged();
}
