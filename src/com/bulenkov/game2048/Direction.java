package com.bulenkov.game2048;

import java.awt.event.KeyEvent;

/**
 * Concrete {@link MoveStrategy} implementations, one per board direction.
 * Replaces the old hard switch in the key listener that dispatched directly
 * to four separate {@code left()/right()/up()/down()} methods: new input
 * sources only need to resolve to a {@link Direction} (see
 * {@link #fromKeyCode(int)}), and new directions can be added here without
 * touching {@link GameModel}.
 */
enum Direction implements MoveStrategy {
  LEFT {
    @Override
    public boolean move(GameModel model) {
      boolean changed = false;
      for (int i = 0; i < 4; i++) {
        Tile[] line = model.getLine(i);
        Tile[] merged = model.mergeLine(model.moveLine(line));
        model.setLine(i, merged);
        if (!changed && !model.compare(line, merged)) {
          changed = true;
        }
      }
      if (changed) {
        model.addTile();
      }
      return changed;
    }
  },
  RIGHT {
    @Override
    public boolean move(GameModel model) {
      model.setTiles(model.rotate(180));
      boolean changed = LEFT.move(model);
      model.setTiles(model.rotate(180));
      return changed;
    }
  },
  UP {
    @Override
    public boolean move(GameModel model) {
      model.setTiles(model.rotate(270));
      boolean changed = LEFT.move(model);
      model.setTiles(model.rotate(90));
      return changed;
    }
  },
  DOWN {
    @Override
    public boolean move(GameModel model) {
      model.setTiles(model.rotate(90));
      boolean changed = LEFT.move(model);
      model.setTiles(model.rotate(270));
      return changed;
    }
  };

  /**
   * Resolves a keyboard code to its corresponding direction, or {@code null}
   * if the key does not represent a move.
   */
  static Direction fromKeyCode(int keyCode) {
    switch (keyCode) {
      case KeyEvent.VK_LEFT:
        return LEFT;
      case KeyEvent.VK_RIGHT:
        return RIGHT;
      case KeyEvent.VK_UP:
        return UP;
      case KeyEvent.VK_DOWN:
        return DOWN;
      default:
        return null;
    }
  }
}
