package com.bulenkov.game2048;

import java.awt.Graphics2D;

/**
 * State pattern: replaces the {@code myWin}/{@code myLose} boolean flags
 * that used to be checked (and could become inconsistent) throughout
 * {@code Game2048}. Each concrete state centralizes the behavior that
 * depends on the current status of the match: whether moves are accepted,
 * how the board is re-evaluated after a move, and what overlay (if any) is
 * drawn on top of the board.
 */
interface GameState {
  /** Whether a move attempt should be processed while in this state. */
  boolean acceptsMoves();

  /**
   * Re-evaluates this state against the current board (e.g. detects that no
   * more moves are possible) and returns the state the game should be in
   * afterwards. Returns {@code this} when nothing changes.
   */
  GameState afterBoardCheck(Game2048 game);

  /** Draws this state's overlay (if any) on top of the board. */
  void drawOverlay(Graphics2D g, int width, int height, String fontName);
}
