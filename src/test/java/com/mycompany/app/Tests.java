package com.mycompany.app;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Tests {

    @Test
    public void testGameInitialization() {
        Game game = new Game();
        assertEquals(State.PLAYING, game.state);
        for (int i = 0; i < 9; i++) {
            assertEquals(' ', game.board[i]);
        }
        assertEquals('X', game.player1.symbol);
        assertEquals('O', game.player2.symbol);
    }

    @Test
    public void testCheckStateXWinRow() {
        Game game = new Game();
        game.board = new char[]{
            ' ',' ',' ',
            'X','X','X',
            ' ',' ',' '
        };
        game.symbol = 'X';
        assertEquals(State.XWIN, game.checkState(game.board));
    }

    @Test
    public void testCheckStateOWinColumn() {
        Game game = new Game();
        game.board = new char[]{
            ' ','O',' ',
            ' ','O',' ',
            ' ','O',' '
        };
        game.symbol = 'O';
        assertEquals(State.OWIN, game.checkState(game.board));
    }

    @Test
    public void testCheckStateDraw() {
        Game game = new Game();
        game.board = new char[]{
            'X','O','X',
            'X','O','O',
            'O','X','X'
        };
        game.symbol = 'X';
        assertEquals(State.DRAW, game.checkState(game.board));
    }

    @Test
    public void testCheckStatePlaying() {
        Game game = new Game();
        game.board = new char[]{
            'X','O','X',
            ' ','O',' ',
            ' ',' ',' '
        };
        game.symbol = 'X';
        assertEquals(State.PLAYING, game.checkState(game.board));
    }



    @Test
    public void testGenerateMovesPartialBoard() {
        Game game = new Game();
        game.board = new char[]{
            'X',' ','O',
            ' ','X',' ',
            ' ',' ',' '
        };
        ArrayList<Integer> moves = new ArrayList<>();
        game.generateMoves(game.board, moves);
        assertEquals(6, moves.size());
        assertFalse(moves.contains(0));
        assertFalse(moves.contains(2));
        assertFalse(moves.contains(4));
    }

    @Test
    public void testEvaluatePositionXWinForX() {
        Game game = new Game();
        game.board = new char[]{
            'X',' ','X',
            'O','X',' ',
            'O','O','X'
        };
        game.symbol = 'X';
        game.player1.symbol = 'X';
        assertEquals(+Game.INF, game.evaluatePosition(game.board, game.player1));
    }

    @Test
    public void testEvaluatePositionXWinForO() {
        Game game = new Game();
        game.board = new char[]{
            'X','X','X',
            ' ',' ',' ',
            ' ',' ',' '
        };
        game.symbol = 'X';
        game.player2.symbol = 'O';
        assertEquals(-Game.INF, game.evaluatePosition(game.board, game.player2));
    }

    @Test
    public void testEvaluatePositionDraw() {
        Game game = new Game();
        game.board = new char[]{
            'X','O','X',
            'X','O','O',
            'O','X','X'
        };
        game.symbol = 'X';
        assertEquals(0, game.evaluatePosition(game.board, game.player1));
    }

    @Test
    public void testEvaluatePositionNonTerminal() {
        Game game = new Game();
        game.board = new char[]{
            'X','O',' ',
            ' ',' ',' ',
            ' ',' ',' '
        };
        game.symbol = 'X';
        assertEquals(-1, game.evaluatePosition(game.board, game.player1));
    }

    @Test
    public void testMiniMaxOnPartialBoard() {
        Game game = new Game();
        game.board = new char[]{
            'X','O','X',
            'X','O','O',
            ' ','X',' '
        };
        game.player2.symbol = 'O';
        int move = game.MiniMax(game.board, game.player2);
        assertTrue(move >= 1 && move <= 9);
    }

    @Test
    public void testMiniMaxWithThreat() {
        Game game = new Game();
        game.board = new char[]{
            'X','X',' ',
            ' ','O',' ',
            ' ',' ',' '
        };
        game.player2.symbol = 'O';
        int move = game.MiniMax(game.board, game.player2);
        assertTrue(move >= 1 && move <= 9);
    }

    @Test
    public void testMiniMaxLastCell() {
        Game game = new Game();
        game.board = new char[]{
            'X','O','X',
            'X','O','O',
            'O','X',' '
        };
        game.player2.symbol = 'O';
        int move = game.MiniMax(game.board, game.player2);
        assertEquals(9, move);
    }

    @Test
    public void testMinMoveTerminal() {
        Game game = new Game();
        game.board = new char[]{
            'X','X','X',
            ' ','O',' ',
            ' ',' ',' '
        };
        game.symbol = 'X';
        int val = game.MinMove(game.board, game.player2);
        assertEquals(-Game.INF, val);
    }

    @Test
    public void testMaxMoveTerminal() {
        Game game = new Game();
        game.board = new char[]{
            'X','X','X',
            ' ','O',' ',
            ' ',' ',' '
        };
        game.symbol = 'X';
        int val = game.MaxMove(game.board, game.player1);
        assertEquals(+Game.INF, val);
    }

    @Test
    public void testUtilityPrintCharArray() {
        char[] board = {
            'X','O',' ',
            ' ','X',' ',
            ' ',' ',' '
        };
        Utility.print(board);
    }

    @Test
    public void testUtilityPrintIntArray() {
        int[] moves = {
            0,1,2,
            3,4,5,
            6,7,8
        };
        Utility.print(moves);
    }

    @Test
    public void testGenerateMovesEmptyBoard() {
        Game game = new Game();
        ArrayList<Integer> moves = new ArrayList<>();
        game.generateMoves(game.board, moves);
        assertEquals(9, moves.size());
        for (int i = 0; i < 9; i++) {
            assertEquals(Integer.valueOf(i), moves.get(i));
        }
    }

    @Test
    public void testMiniMaxReturnsValidMove() {
        Game game = new Game();
        int move = game.MiniMax(game.board, game.player2);
        assertTrue(move >= 1 && move <= 9);
    }    

    @Test(timeout = 5000)
    public void testMiniMaxOnEmptyBoard() {
        Game game = new Game();
        int move = game.MiniMax(game.board, game.player2);
        assertTrue(move >= 1 && move <= 9);
    }

    @Test
    public void testCellConstructorAndGetters() {
        TicTacToeCell cell = new TicTacToeCell(5, 2, 1);
        assertEquals(5, cell.getNum());
        assertEquals(2, cell.getCol());
        assertEquals(1, cell.getRow());
        assertEquals(' ', cell.getMarker());
    }

    @Test
    public void testSetMarkerChangesTextAndDisables() {
        TicTacToeCell cell = new TicTacToeCell(1, 0, 0);
        assertTrue(cell.isEnabled());
        cell.setMarker("X");
        assertEquals('X', cell.getMarker());
        assertFalse(cell.isEnabled());
    }

    @Test
    public void testUtilityPrintArrayList() {
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(0, 4, 8));
        Utility.print(list);
    }
}
