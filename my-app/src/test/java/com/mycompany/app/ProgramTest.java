package com.mycompany.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GridLayout;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class ProgramTest {

    @Test
    void checkStateDetectsXWin() {
        Game game = new Game();
        game.symbol = 'X';
        char[] board = {'X', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' '};
        assertEquals(State.XWIN, game.checkState(board));
    }

    @Test
    void checkStateDetectsOWin() {
        Game game = new Game();
        game.symbol = 'O';
        char[] board = {'O', ' ', ' ', 'O', ' ', ' ', 'O', ' ', ' '};
        assertEquals(State.OWIN, game.checkState(board));
    }

    @Test
    void checkStateDetectsDraw() {
        Game game = new Game();
        game.symbol = 'X';
        char[] board = {'X', 'O', 'X', 'X', 'O', 'O', 'O', 'X', 'X'};
        assertEquals(State.DRAW, game.checkState(board));
    }

    @Test
    void generateMovesReturnsFreeCells() {
        Game game = new Game();
        char[] board = {'X', 'O', ' ', ' ', 'O', 'X', ' ', ' ', 'X'};
        ArrayList<Integer> moves = new ArrayList<>();
        game.generateMoves(board, moves);
        assertEquals(4, moves.size());
        assertTrue(moves.contains(2));
        assertTrue(moves.contains(3));
        assertTrue(moves.contains(6));
        assertTrue(moves.contains(7));
    }

    @Test
    void evaluatePositionScoresWinLossAndDraw() {
        Game game = new Game();
        Player playerX = new Player();
        playerX.symbol = 'X';
        Player playerO = new Player();
        playerO.symbol = 'O';

        char[] xWin = {'X', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' '};
        game.symbol = 'X';
        assertEquals(Game.INF, game.evaluatePosition(xWin, playerX));
        assertEquals(-Game.INF, game.evaluatePosition(xWin, playerO));

        char[] draw = {'X', 'O', 'X', 'X', 'O', 'O', 'O', 'X', 'X'};
        game.symbol = 'X';
        assertEquals(0, game.evaluatePosition(draw, playerX));
    }

    @Test
    void miniMaxFindsWinningMove() {
        Game game = new Game();
        Player ai = new Player();
        ai.symbol = 'O';

        char[] board = {'O', 'O', ' ', 'X', 'X', ' ', ' ', ' ', ' '};
        int bestMove = game.miniMax(board, ai);
        assertEquals(3, bestMove);
    }

    @Test
    void minAndMaxMoveReturnTerminalScores() {
        Game game = new Game();
        Player playerX = new Player();
        playerX.symbol = 'X';

        char[] terminal = {'X', 'X', 'X', 'O', 'O', ' ', ' ', ' ', ' '};
        game.symbol = 'X';
        assertEquals(Game.INF, game.minMove(terminal, playerX));
        assertEquals(Game.INF, game.maxMove(terminal, playerX));
    }

    @Test
    void ticTacToeCellStoresMetadata() {
        TicTacToeCell cell = new TicTacToeCell(4, 1, 2);
        assertEquals(4, cell.getNum());
        assertEquals(1, cell.getCol());
        assertEquals(2, cell.getRow());
        cell.setMarker("X");
        assertEquals('X', cell.getMarker());
        assertFalse(cell.isEnabled());
    }

    @Test
    void utilityPrintMethodsRun() {
        PrintStream oldOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        try {
            Utility.print(new char[]{'X', 'O', 'X', 'O', 'X', 'O', 'X', 'O', 'X'});
            Utility.print(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
            ArrayList<Integer> moves = new ArrayList<>();
            moves.add(0);
            moves.add(2);
            Utility.print(moves);
        } finally {
            System.setOut(oldOut);
        }
        assertTrue(out.toString().length() > 0);
    }

    @Test
    void panelClickUpdatesBoardAndKeepsPlaying() throws Exception {
        TicTacToePanel panel = new TicTacToePanel(new GridLayout(3, 3));

        Field cellsField = TicTacToePanel.class.getDeclaredField("cells");
        cellsField.setAccessible(true);
        TicTacToeCell[] cells = (TicTacToeCell[]) cellsField.get(panel);

        cells[0].doClick();

        Field gameField = TicTacToePanel.class.getDeclaredField("game");
        gameField.setAccessible(true);
        Game game = (Game) gameField.get(panel);

        int filled = 0;
        for (char marker : game.board) {
            if (marker != ' ') {
                filled++;
            }
        }

        assertTrue(filled >= 2);
        assertEquals(State.PLAYING, game.state);
    }
}
