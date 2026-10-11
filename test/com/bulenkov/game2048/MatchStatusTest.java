package com.bulenkov.game2048;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the State pattern in isolation: which states accept moves, and the
 * transition rules enforced by {@link MatchStatus#afterBoardCheck(GameModel)}
 * - in particular, that {@code WON}/{@code LOST} never transition away once
 * reached, which is what makes the simultaneous "won and lost" inconsistency
 * from the original flag-based code impossible to reproduce here.
 */
class MatchStatusTest {

  @Test
  @DisplayName("apenas PLAYING aceita jogadas")
  void onlyPlayingAcceptsMoves() {
    assertTrue(MatchStatus.PLAYING.acceptsMoves());
    assertFalse(MatchStatus.WON.acceptsMoves());
    assertFalse(MatchStatus.LOST.acceptsMoves());
  }

  @Test
  @DisplayName("WON e LOST nunca transicionam para outro estado")
  void wonAndLostNeverTransitionAway() {
    GameModel unplayableModel = new GameModel();
    unplayableModel.setTiles(GameModelTest.checkerboard(512, 256));

    assertSame(MatchStatus.WON, MatchStatus.WON.afterBoardCheck(unplayableModel));
    assertSame(MatchStatus.LOST, MatchStatus.LOST.afterBoardCheck(unplayableModel));
  }

  @Test
  @DisplayName("PLAYING so transiciona para LOST quando o tabuleiro fica sem jogadas")
  void playingTransitionsToLostOnlyWhenBoardIsUnplayable() {
    GameModel playableModel = new GameModel();
    assertSame(MatchStatus.PLAYING, MatchStatus.PLAYING.afterBoardCheck(playableModel));

    GameModel unplayableModel = new GameModel();
    unplayableModel.setTiles(GameModelTest.checkerboard(512, 256));
    assertSame(MatchStatus.LOST, MatchStatus.PLAYING.afterBoardCheck(unplayableModel));
  }
}
