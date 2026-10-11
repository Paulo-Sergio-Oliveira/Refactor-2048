package com.bulenkov.game2048;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises {@link GameModel} directly, with no Swing/AWT involved - possible
 * only after the Observer refactor extracted the game engine out of the
 * {@code Game2048} {@code JPanel}.
 */
class GameModelTest {

  @Test
  @DisplayName("um novo jogo comeca com 2 pecas, placar zero e estado PLAYING")
  void newGameStartsWithTwoTilesZeroScoreAndPlayingState() {
    GameModel model = new GameModel();

    assertEquals(2, countNonEmpty(model.getTiles()));
    assertEquals(0, model.getScore());
    assertEquals(MatchStatus.PLAYING, model.getState());
    assertTrue(model.getState().acceptsMoves());
  }

  @Test
  @DisplayName("resetGame() volta o jogo ao estado inicial mesmo apos alteracoes")
  void resetGameRestoresInitialState() {
    GameModel model = new GameModel();
    Tile[] fullBoard = new Tile[16];
    for (int i = 0; i < fullBoard.length; i++) {
      fullBoard[i] = new Tile(2);
    }
    model.setTiles(fullBoard);

    model.resetGame();

    assertEquals(0, model.getScore());
    assertEquals(MatchStatus.PLAYING, model.getState());
    assertEquals(2, countNonEmpty(model.getTiles()));
  }

  @Test
  @DisplayName("mergeLine funde um unico par adjacente igual, da esquerda para a direita")
  void mergeLineMergesOneAdjacentPairFromLeft() {
    GameModel model = new GameModel();
    Tile[] line = {new Tile(2), new Tile(2), new Tile(2), new Tile(0)};

    Tile[] merged = model.mergeLine(line);

    assertEquals(4, merged[0].value);
    assertEquals(2, merged[1].value);
    assertEquals(0, merged[2].value);
    assertEquals(0, merged[3].value);
  }

  @Test
  @DisplayName("moveLine compacta pecas nao vazias para a esquerda, preservando a ordem")
  void moveLineCompactsNonEmptyTilesPreservingOrder() {
    GameModel model = new GameModel();
    Tile[] line = {new Tile(2), new Tile(0), new Tile(0), new Tile(4)};

    Tile[] moved = model.moveLine(line);

    assertEquals(2, moved[0].value);
    assertEquals(4, moved[1].value);
    assertEquals(0, moved[2].value);
    assertEquals(0, moved[3].value);
  }

  @Test
  @DisplayName("Direction.LEFT.move funde pecas iguais e soma a pontuacao")
  void leftMoveMergesAndScores() {
    GameModel model = new GameModel();
    Tile[] board = emptyBoard();
    board[0] = new Tile(2);
    board[1] = new Tile(2);
    model.setTiles(board);

    boolean changed = Direction.LEFT.move(model);

    assertTrue(changed);
    assertEquals(4, model.getScore());
    assertEquals(4, model.getLine(0)[0].value);
  }

  @Test
  @DisplayName("tabuleiro cheio e sem jogadas possiveis transiciona o estado para LOST")
  void unplayableBoardTransitionsToLost() {
    GameModel model = new GameModel();
    model.setTiles(checkerboard(512, 256));
    assertFalse(model.canMove(), "pre-condicao do teste: o tabuleiro nao deveria ter jogadas");

    model.handleMove(Direction.LEFT);

    assertEquals(MatchStatus.LOST, model.getState());
    assertFalse(model.getState().acceptsMoves());
  }

  @Test
  @DisplayName(
      "alcancar 2048 mantem o estado WON mesmo que o tabuleiro fique sem jogadas - "
          + "regressao do bug original, em que myWin e myLose podiam ficar true ao mesmo tempo")
  void reachingTargetStaysWonEvenWhenBoardBecomesUnplayable() {
    GameModel model = new GameModel();
    Tile[] board = checkerboard(512, 256);
    // Linha 0 (y=0): duas pecas de 1024 se fundem em 2048 ao mover para a esquerda;
    // os demais valores da linha (8, 16) nunca colidem com 2048, nem com o spawn
    // aleatorio que preenchera a unica casa vazia resultante da fusao (sempre 2 ou 4).
    board[0] = new Tile(1024);
    board[1] = new Tile(1024);
    board[2] = new Tile(8);
    board[3] = new Tile(16);
    model.setTiles(board);

    model.handleMove(Direction.LEFT);

    assertEquals(MatchStatus.WON, model.getState(),
        "deveria permanecer WON mesmo com o tabuleiro cheio e sem jogadas possiveis");
    assertFalse(model.canMove(),
        "pre-condicao do teste: apos a jogada o tabuleiro nao deveria ter mais jogadas");
  }

  @Test
  @DisplayName("GameModel notifica os observadores registrados a cada mudanca de estado")
  void notifiesRegisteredObserversOnChange() {
    GameModel model = new GameModel();
    int[] notifications = {0};
    model.addObserver(() -> notifications[0]++);

    model.resetGame();
    model.handleMove(null);

    assertEquals(2, notifications[0]);
  }

  private static int countNonEmpty(Tile[] tiles) {
    int count = 0;
    for (Tile tile : tiles) {
      if (!tile.isEmpty()) {
        count++;
      }
    }
    return count;
  }

  private static Tile[] emptyBoard() {
    Tile[] board = new Tile[16];
    for (int i = 0; i < board.length; i++) {
      board[i] = new Tile();
    }
    return board;
  }

  /**
   * A 4x4 board alternating between two values by cell parity, so that no two
   * orthogonally-adjacent cells are ever equal - i.e. a board with no
   * possible move left.
   */
  static Tile[] checkerboard(int a, int b) {
    Tile[] board = new Tile[16];
    for (int y = 0; y < 4; y++) {
      for (int x = 0; x < 4; x++) {
        board[x + y * 4] = new Tile((x + y) % 2 == 0 ? a : b);
      }
    }
    return board;
  }
}
