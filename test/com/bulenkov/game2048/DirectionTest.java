package com.bulenkov.game2048;

import java.awt.event.KeyEvent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Covers the Strategy pattern: resolving a key code to a {@link Direction},
 * and each concrete strategy sliding a tile to the expected edge of the
 * board.
 */
class DirectionTest {

  @Test
  @DisplayName("fromKeyCode resolve as teclas de seta para a direcao correspondente")
  void fromKeyCodeResolvesArrowKeysToTheMatchingDirection() {
    assertEquals(Direction.LEFT, Direction.fromKeyCode(KeyEvent.VK_LEFT));
    assertEquals(Direction.RIGHT, Direction.fromKeyCode(KeyEvent.VK_RIGHT));
    assertEquals(Direction.UP, Direction.fromKeyCode(KeyEvent.VK_UP));
    assertEquals(Direction.DOWN, Direction.fromKeyCode(KeyEvent.VK_DOWN));
  }

  @Test
  @DisplayName("fromKeyCode retorna null para teclas que nao representam uma jogada")
  void fromKeyCodeReturnsNullForNonMoveKeys() {
    assertNull(Direction.fromKeyCode(KeyEvent.VK_ESCAPE));
    assertNull(Direction.fromKeyCode(KeyEvent.VK_SPACE));
  }

  @Test
  @DisplayName("RIGHT desliza uma peca ate a borda direita do tabuleiro")
  void rightMoveSlidesTileToTheRightEdge() {
    GameModel model = new GameModel();
    Tile[] board = emptyBoard();
    board[0] = new Tile(2); // (x=0, y=0)
    model.setTiles(board);

    Direction.RIGHT.move(model);

    assertEquals(2, model.getLine(0)[3].value);
  }

  @Test
  @DisplayName("DOWN desliza uma peca ate a borda inferior do tabuleiro")
  void downMoveSlidesTileToTheBottomEdge() {
    GameModel model = new GameModel();
    Tile[] board = emptyBoard();
    board[0] = new Tile(2); // (x=0, y=0)
    model.setTiles(board);

    Direction.DOWN.move(model);

    assertEquals(2, model.getLine(3)[0].value);
  }

  @Test
  @DisplayName("um movimento que nao altera o tabuleiro reporta changed=false")
  void moveThatDoesNotChangeTheBoardReportsNoChange() {
    GameModel model = new GameModel();
    Tile[] board = emptyBoard();
    board[0] = new Tile(2); // ja esta na borda esquerda
    model.setTiles(board);

    boolean changed = Direction.LEFT.move(model);

    assertEquals(false, changed);
  }

  private static Tile[] emptyBoard() {
    Tile[] board = new Tile[16];
    for (int i = 0; i < board.length; i++) {
      board[i] = new Tile();
    }
    return board;
  }
}
