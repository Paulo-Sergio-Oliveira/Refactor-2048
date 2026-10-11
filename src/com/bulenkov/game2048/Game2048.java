/*
 * Copyright 1998-2014 Konstantin Bulenkov http://bulenkov.com/about
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bulenkov.game2048;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Swing view for the game. Holds no game state of its own: it renders
 * whatever {@link GameModel} currently reports and forwards key presses to
 * it, repainting only when the model notifies it of a change (Observer
 * pattern) instead of the model and the view being the same object, as in
 * the original single-class implementation.
 *
 * @author Konstantin Bulenkov
 */
public class Game2048 extends JPanel implements GameObserver {
  private static final Color BG_COLOR = new Color(0xbbada0);
  private static final String FONT_NAME = "Arial";
  private static final int TILE_SIZE = 64;
  private static final int TILES_MARGIN = 16;

  private final GameModel myModel = new GameModel();

  public Game2048() {
    setPreferredSize(new Dimension(340, 400));
    setFocusable(true);
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          myModel.resetGame();
          return;
        }
        myModel.handleMove(Direction.fromKeyCode(e.getKeyCode()));
      }
    });
    myModel.addObserver(this);
  }

  public void resetGame() {
    myModel.resetGame();
  }

  @Override
  public void onGameChanged() {
    repaint();
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    g.setColor(BG_COLOR);
    g.fillRect(0, 0, this.getSize().width, this.getSize().height);
    Tile[] tiles = myModel.getTiles();
    for (int y = 0; y < 4; y++) {
      for (int x = 0; x < 4; x++) {
        drawTile(g, tiles[x + y * 4], x, y);
      }
    }
  }

  private void drawTile(Graphics g2, Tile tile, int x, int y) {
    Graphics2D g = ((Graphics2D) g2);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
    int value = tile.value;
    int xOffset = offsetCoors(x);
    int yOffset = offsetCoors(y);
    g.setColor(tile.getBackground());
    g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 14, 14);
    g.setColor(tile.getForeground());
    final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
    final Font font = new Font(FONT_NAME, Font.BOLD, size);
    g.setFont(font);

    String s = String.valueOf(value);
    final FontMetrics fm = getFontMetrics(font);

    final int w = fm.stringWidth(s);
    final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

    if (value != 0)
      g.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 - 2);

    GameState state = myModel.getState();
    state.drawOverlay(g, getWidth(), getHeight(), FONT_NAME);
    if (!state.acceptsMoves()) {
      g.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
      g.setColor(new Color(128, 128, 128, 128));
      g.drawString("Press ESC to play again", 80, getHeight() - 40);
    }
    g.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
    g.drawString("Score: " + myModel.getScore(), 200, 365);

  }

  private static int offsetCoors(int arg) {
    return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN;
  }

  public static void main(String[] args) {
    JFrame game = new JFrame();
    game.setTitle("2048 Game");
    game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    game.setSize(340, 400);
    game.setResizable(false);

    game.add(new Game2048());

    game.setLocationRelativeTo(null);
    game.setVisible(true);
  }
}
