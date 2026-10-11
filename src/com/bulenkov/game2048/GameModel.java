package com.bulenkov.game2048;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The game's board, score and status, extracted out of the {@code Game2048}
 * {@code JPanel} so that game rules can live (and be exercised) independently
 * of Swing. This class has no AWT/Swing dependency: it only exposes the
 * board/score/state to whoever asks, and tells its {@link GameObserver}s
 * when something changed (Observer pattern) instead of reaching into the UI
 * itself (e.g. calling {@code repaint()}), as the original {@code Game2048}
 * used to do directly.
 */
class GameModel {
  private Tile[] myTiles;
  private final TileFactory myTileFactory = new ClassicTileFactory();
  private GameState myGameState = MatchStatus.PLAYING;
  private int myScore;
  private final List<GameObserver> myObservers = new ArrayList<GameObserver>();

  GameModel() {
    resetGame();
  }

  void addObserver(GameObserver observer) {
    myObservers.add(observer);
  }

  private void notifyObservers() {
    for (GameObserver observer : myObservers) {
      observer.onGameChanged();
    }
  }

  Tile[] getTiles() {
    return myTiles;
  }

  int getScore() {
    return myScore;
  }

  GameState getState() {
    return myGameState;
  }

  void resetGame() {
    myScore = 0;
    myGameState = MatchStatus.PLAYING;
    myTiles = new Tile[4 * 4];
    for (int i = 0; i < myTiles.length; i++) {
      myTiles[i] = myTileFactory.createEmptyTile();
    }
    addTile();
    addTile();
    notifyObservers();
  }

  /**
   * Processes one move attempt, re-evaluating the match state before and
   * after it, exactly as the original key handler used to.
   *
   * @param direction the direction to move, or {@code null} if the pressed
   *                   key was not a move.
   */
  void handleMove(Direction direction) {
    myGameState = myGameState.afterBoardCheck(this);

    if (myGameState.acceptsMoves() && direction != null) {
      direction.move(this);
    }

    myGameState = myGameState.afterBoardCheck(this);

    notifyObservers();
  }

  void setTiles(Tile[] tiles) {
    myTiles = tiles;
  }

  private Tile tileAt(int x, int y) {
    return myTiles[x + y * 4];
  }

  void addTile() {
    List<Integer> emptyIndices = availableIndices();
    if (!emptyIndices.isEmpty()) {
      int index = (int) (Math.random() * emptyIndices.size()) % emptyIndices.size();
      myTiles[emptyIndices.get(index)] = myTileFactory.createRandomTile();
    }
  }

  private List<Integer> availableIndices() {
    final List<Integer> indices = new ArrayList<Integer>(16);
    for (int i = 0; i < myTiles.length; i++) {
      if (myTiles[i].isEmpty()) {
        indices.add(i);
      }
    }
    return indices;
  }

  private boolean isFull() {
    return availableIndices().isEmpty();
  }

  boolean canMove() {
    if (!isFull()) {
      return true;
    }
    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < 4; y++) {
        Tile t = tileAt(x, y);
        if ((x < 3 && t.value == tileAt(x + 1, y).value)
          || ((y < 3) && t.value == tileAt(x, y + 1).value)) {
          return true;
        }
      }
    }
    return false;
  }

  boolean compare(Tile[] line1, Tile[] line2) {
    if (line1 == line2) {
      return true;
    } else if (line1.length != line2.length) {
      return false;
    }

    for (int i = 0; i < line1.length; i++) {
      if (line1[i].value != line2[i].value) {
        return false;
      }
    }
    return true;
  }

  Tile[] rotate(int angle) {
    Tile[] newTiles = new Tile[4 * 4];
    int offsetX = 3, offsetY = 3;
    if (angle == 90) {
      offsetY = 0;
    } else if (angle == 270) {
      offsetX = 0;
    }

    double rad = Math.toRadians(angle);
    int cos = (int) Math.cos(rad);
    int sin = (int) Math.sin(rad);
    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < 4; y++) {
        int newX = (x * cos) - (y * sin) + offsetX;
        int newY = (x * sin) + (y * cos) + offsetY;
        newTiles[(newX) + (newY) * 4] = tileAt(x, y);
      }
    }
    return newTiles;
  }

  Tile[] moveLine(Tile[] oldLine) {
    LinkedList<Tile> l = new LinkedList<Tile>();
    for (int i = 0; i < 4; i++) {
      if (!oldLine[i].isEmpty())
        l.addLast(oldLine[i]);
    }
    if (l.size() == 0) {
      return oldLine;
    } else {
      Tile[] newLine = new Tile[4];
      ensureSize(l, 4);
      for (int i = 0; i < 4; i++) {
        newLine[i] = l.removeFirst();
      }
      return newLine;
    }
  }

  Tile[] mergeLine(Tile[] oldLine) {
    LinkedList<Tile> list = new LinkedList<Tile>();
    for (int i = 0; i < 4 && !oldLine[i].isEmpty(); i++) {
      int num = oldLine[i].value;
      if (i < 3 && oldLine[i].value == oldLine[i + 1].value) {
        num *= 2;
        myScore += num;
        int ourTarget = 2048;
        if (num == ourTarget) {
          myGameState = MatchStatus.WON;
        }
        i++;
      }
      list.add(myTileFactory.createTile(num));
    }
    if (list.size() == 0) {
      return oldLine;
    } else {
      ensureSize(list, 4);
      return list.toArray(new Tile[4]);
    }
  }

  private void ensureSize(List<Tile> l, int s) {
    while (l.size() != s) {
      l.add(myTileFactory.createEmptyTile());
    }
  }

  Tile[] getLine(int index) {
    Tile[] result = new Tile[4];
    for (int i = 0; i < 4; i++) {
      result[i] = tileAt(i, index);
    }
    return result;
  }

  void setLine(int index, Tile[] re) {
    System.arraycopy(re, 0, myTiles, index * 4, 4);
  }
}
