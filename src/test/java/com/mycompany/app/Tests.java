package com.mycompany.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.GridLayout;
import java.lang.reflect.Field;
import java.util.ArrayList;
import org.junit.Test;

public class Tests {

    private char[] board(char... cells) {
        char[] snapshot = new char[9];
        System.arraycopy(cells, 0, snapshot, 0, 9);
        return snapshot;
    }

    @Test
    public void freshGameHasEmptyCells() {
        Game game = new Game();
        assertEquals(State.PLAYING, game.state);
        for (char cell : game.board) {
            assertEquals(' ', cell);
        }
    }

    @Test
    public void playersStartWithExpectedSymbols() {
        Game game = new Game();
        assertEquals('X', game.player1.symbol);
        assertEquals('O', game.player2.symbol);
    }

    @Test
    public void detectsHorizontalWinForCrosses() {
        Game game = new Game();
        char[] snapshot = board(' ', ' ', ' ', 'X', 'X', 'X', ' ', ' ', ' ');
        game.symbol = 'X';
        assertEquals(State.XWIN, game.checkState(snapshot));
    }

    @Test
    public void detectsVerticalWinForNoughts() {
        Game game = new Game();
        char[] snapshot = board(' ', 'O', ' ', ' ', 'O', ' ', ' ', 'O', ' ');
        game.symbol = 'O';
        assertEquals(State.OWIN, game.checkState(snapshot));
    }

    @Test
    public void detectsDiagonalDraw() {
        Game game = new Game();
        char[] snapshot = board('X', 'O', 'X', 'X', 'O', 'O', 'O', 'X', 'X');
        game.symbol = 'X';
        assertEquals(State.DRAW, game.checkState(snapshot));
    }

    @Test
    public void keepsPlayingWhenMovesRemain() {
        Game game = new Game();
        char[] snapshot = board('X', 'O', 'X', ' ', 'O', ' ', ' ', ' ', ' ');
        game.symbol = 'X';
        assertEquals(State.PLAYING, game.checkState(snapshot));
    }

    @Test
    public void listsOnlyOpenCells() {
        Game game = new Game();
        char[] snapshot = board('X', ' ', 'O', ' ', 'X', ' ', ' ', ' ', ' ');
        ArrayList<Integer> open = new ArrayList<>();
        game.collectEmptyCells(snapshot, open);
        assertEquals(6, open.size());
        assertFalse(open.contains(0));
        assertFalse(open.contains(2));
        assertFalse(open.contains(4));
    }

    @Test
    public void emptyBoardListsAllCellIndexes() {
        Game game = new Game();
        ArrayList<Integer> open = new ArrayList<>();
        game.collectEmptyCells(game.board, open);
        assertEquals(9, open.size());
        for (int i = 0; i < 9; i++) {
            assertEquals(Integer.valueOf(i), open.get(i));
        }
    }

    @Test
    public void scoresWinningPositionForSamePlayer() {
        Game game = new Game();
        char[] snapshot = board('X', 'X', 'X', ' ', 'O', ' ', ' ', ' ', ' ');
        game.symbol = 'X';
        assertEquals(Game.INF, game.scorePosition(snapshot, game.player1));
    }

    @Test
    public void scoresLosingPositionForOpponent() {
        Game game = new Game();
        char[] snapshot = board('X', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' ');
        game.symbol = 'X';
        assertEquals(-Game.INF, game.scorePosition(snapshot, game.player2));
    }

    @Test
    public void scoresDrawAsZero() {
        Game game = new Game();
        char[] snapshot = board('X', 'O', 'X', 'X', 'O', 'O', 'O', 'X', 'X');
        game.symbol = 'X';
        assertEquals(0, game.scorePosition(snapshot, game.player1));
    }

    @Test
    public void scoresOpenPositionAsUnknown() {
        Game game = new Game();
        char[] snapshot = board('X', 'O', ' ', ' ', ' ', ' ', ' ', ' ', ' ');
        game.symbol = 'X';
        assertEquals(-1, game.scorePosition(snapshot, game.player1));
    }

    @Test
    public void aiReturnsLegalMoveInMidGame() {
        Game game = new Game();
        char[] snapshot = board('O', 'O', ' ', 'X', ' ', ' ', ' ', ' ', ' ');
        game.player2.symbol = 'O';
        int move = game.findBestMove(snapshot, game.player2);
        assertTrue(move >= 1 && move <= 9);
    }

    @Test
    public void aiChoosesMoveWhenOpponentThreatensRow() {
        Game game = new Game();
        char[] snapshot = board('O', 'O', ' ', ' ', 'X', ' ', ' ', ' ', ' ');
        game.player2.symbol = 'O';
        int move = game.findBestMove(snapshot, game.player2);
        assertTrue(move >= 1 && move <= 9);
    }

    @Test
    public void aiPicksLastFreeCell() {
        Game game = new Game();
        char[] snapshot = board('X', 'O', 'X', 'X', 'O', 'O', 'O', 'X', ' ');
        game.player2.symbol = 'O';
        assertEquals(9, game.findBestMove(snapshot, game.player2));
    }

    @Test
    public void minTurnReturnsLossOnTerminalBoard() {
        Game game = new Game();
        char[] snapshot = board('X', 'X', 'X', ' ', 'O', ' ', ' ', ' ', ' ');
        game.symbol = 'X';
        assertEquals(-Game.INF, game.minTurn(snapshot, game.player2));
    }

    @Test
    public void maxTurnReturnsWinOnTerminalBoard() {
        Game game = new Game();
        char[] snapshot = board('X', 'X', 'X', ' ', 'O', ' ', ' ', ' ', ' ');
        game.symbol = 'X';
        assertEquals(Game.INF, game.maxTurn(snapshot, game.player1));
    }

    @Test
    public void utilityCanPrintBoardSnapshots() {
        Utility.dumpBoard(board('X', 'O', ' ', ' ', 'X', ' ', ' ', ' ', ' '));
        Utility.dumpBoard(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8});
        ArrayList<Integer> moves = new ArrayList<>();
        moves.add(1);
        moves.add(4);
        Utility.dumpMoves(moves);
        assertTrue(true);
    }

    @Test
    public void cellStoresCoordinatesAndMarker() {
        TicTacToeCell cell = new TicTacToeCell(4, 1, 2);
        assertEquals(4, cell.getNum());
        assertEquals(1, cell.getCol());
        assertEquals(2, cell.getRow());
        assertEquals(' ', cell.getMarker());
        cell.setMarker("O");
        assertEquals('O', cell.getMarker());
        assertFalse(cell.isEnabled());
    }

    @Test
    public void markingCrossDisablesCell() {
        TicTacToeCell cell = new TicTacToeCell(1, 0, 0);
        assertTrue(cell.isEnabled());
        cell.setMarker("X");
        assertEquals('X', cell.getMarker());
        assertFalse(cell.isEnabled());
    }

    @Test(timeout = 5000)
    public void aiMoveIsValidOnEmptyBoard() {
        Game game = new Game();
        int move = game.findBestMove(game.board, game.player2);
        assertTrue(move >= 1 && move <= 9);
    }

    @Test
    public void panelUpdatesBoardAfterClick() throws Exception {
        TicTacToePanel panel = new TicTacToePanel(new GridLayout(3, 3));

        Field cellsField = TicTacToePanel.class.getDeclaredField("cells");
        cellsField.setAccessible(true);
        TicTacToeCell[] cells = (TicTacToeCell[]) cellsField.get(panel);
        cells[4].doClick();

        Field gameField = TicTacToePanel.class.getDeclaredField("game");
        gameField.setAccessible(true);
        Game game = (Game) gameField.get(panel);

        int occupied = 0;
        for (char cell : game.board) {
            if (cell != ' ') {
                occupied++;
            }
        }
        assertTrue(occupied >= 2);
        assertEquals(State.PLAYING, game.state);
    }
}
