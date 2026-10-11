package com.bulenkov.game2048;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Concrete states of a match: {@link #PLAYING}, {@link #WON} and
 * {@link #LOST}. Only {@link #PLAYING} ever transitions away (to
 * {@link #LOST} when the board becomes unplayable, or to {@link #WON} when
 * {@code Game2048} reaches the 2048 tile) - once the match is won or lost it
 * stays that way, which also fixes a latent inconsistency in the original
 * flag-based code: reaching 2048 on a now-full, unmovable board used to set
 * both {@code myWin} and {@code myLose} to {@code true} at once, drawing
 * both overlays on top of each other.
 */
enum MatchStatus implements GameState {
  PLAYING {
    @Override
    public boolean acceptsMoves() {
      return true;
    }

    @Override
    public GameState afterBoardCheck(Game2048 game) {
      return game.canMove() ? this : LOST;
    }

    @Override
    public void drawOverlay(Graphics2D g, int width, int height, String fontName) {
      // nothing to draw while the match is in progress
    }
  },
  WON {
    @Override
    public boolean acceptsMoves() {
      return false;
    }

    @Override
    public GameState afterBoardCheck(Game2048 game) {
      return this;
    }

    @Override
    public void drawOverlay(Graphics2D g, int width, int height, String fontName) {
      drawBanner(g, width, height, fontName);
      g.drawString("You won!", 68, 150);
    }
  },
  LOST {
    @Override
    public boolean acceptsMoves() {
      return false;
    }

    @Override
    public GameState afterBoardCheck(Game2048 game) {
      return this;
    }

    @Override
    public void drawOverlay(Graphics2D g, int width, int height, String fontName) {
      drawBanner(g, width, height, fontName);
      g.drawString("Game over!", 50, 130);
      g.drawString("You lose!", 64, 200);
    }
  };

  private static void drawBanner(Graphics2D g, int width, int height, String fontName) {
    g.setColor(new Color(255, 255, 255, 30));
    g.fillRect(0, 0, width, height);
    g.setColor(new Color(78, 139, 202));
    g.setFont(new Font(fontName, Font.BOLD, 48));
  }
}
