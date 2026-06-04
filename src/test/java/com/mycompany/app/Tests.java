package com.mycompany.app;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.GridLayout;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

public class Tests {

    private static char[] grid(String row0, String row1, String row2) {
        String flat = row0 + row1 + row2;
        if (flat.length() != 9) {
            throw new IllegalArgumentException("grid must have 9 cells");
        }
        return flat.toCharArray();
    }

    private MatchEngine seeded(char lastMark, char[] snapshot) {
        MatchEngine engine = new MatchEngine();
        engine.lastPlaced = lastMark;
        System.arraycopy(snapshot, 0, engine.grid, 0, 9);
        return engine;
    }

    @Test
    public void blankMatchHasNineSpacesAndNoWinnerYet() {
        MatchEngine engine = new MatchEngine();
        assertEquals(MatchOutcome.IN_PROGRESS, engine.outcome);
        assertArrayEquals(grid("   ", "   ", "   "), engine.grid);
    }

    @Test
    public void humanSideIsCrossComputerSideIsNought() {
        MatchEngine engine = new MatchEngine();
        assertEquals('X', engine.crosses.mark);
        assertEquals('O', engine.noughts.mark);
        assertFalse(engine.crosses.mark == engine.noughts.mark);
    }

    @Test
    public void topRowCrossesWinAfterThirdMarkInRow() {
        MatchEngine engine = seeded('X', grid("XXX", "O  ", "  O"));
        assertEquals(MatchOutcome.CROSSES_WIN, engine.resolveOutcome(engine.grid));
    }

    @Test
    public void leftColumnNoughtsWinWhenColumnIsFilled() {
        MatchEngine engine = seeded('O', grid("O  ", "O X", "O X"));
        assertEquals(MatchOutcome.NOUGHTS_WIN, engine.resolveOutcome(engine.grid));
    }

    @Test
    public void packedGridWithoutLineEndsInStalemate() {
        MatchEngine engine = seeded('X', grid("OXO", "XXO", "XOX"));
        assertEquals(MatchOutcome.STALEMATE, engine.resolveOutcome(engine.grid));
    }

    @Test
    public void cornerMarksLeaveGameOpen() {
        MatchEngine engine = seeded('O', grid("X  ", "   ", "  O"));
        assertEquals(MatchOutcome.IN_PROGRESS, engine.resolveOutcome(engine.grid));
    }

    @Test
    public void vacantIndicesExcludeTakenSlots() {
        MatchEngine engine = new MatchEngine();
        char[] snapshot = grid("  O", "X X", " O ");
        List<Integer> free = engine.vacantIndices(snapshot);
        assertEquals(5, free.size());
        Set<Integer> blocked = new HashSet<>(Arrays.asList(2, 3, 5, 7));
        for (Integer slot : blocked) {
            assertFalse(free.contains(slot));
        }
    }

    @Test
    public void vacantIndicesOnNewGridAreZeroThroughEight() {
        MatchEngine engine = new MatchEngine();
        int[] indexes = engine.vacantIndices(engine.grid).stream().mapToInt(Integer::intValue).toArray();
        assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}, indexes);
    }

    @Test
    public void rateTerminalGivesPlusHundredForOwnDiagonalWin() {
        MatchEngine engine = seeded('X', grid("X  ", " X ", "  X"));
        assertEquals(MatchEngine.SCORE_WIN, engine.rateTerminal(engine.grid, 'X'));
    }

    @Test
    public void rateTerminalGivesMinusHundredWhenOpponentWonColumn() {
        MatchEngine engine = seeded('O', grid("  O", "X O", "  O"));
        assertEquals(-MatchEngine.SCORE_WIN, engine.rateTerminal(engine.grid, 'X'));
    }

    @Test
    public void rateTerminalIsZeroOnPackedStalemate() {
        MatchEngine engine = seeded('X', grid("OXO", "XXO", "XOX"));
        assertEquals(0, engine.rateTerminal(engine.grid, 'O'));
    }

    @Test
    public void rateTerminalStaysUnknownOnWideOpenGrid() {
        MatchEngine engine = seeded('X', grid("X  ", "   ", "   "));
        assertEquals(MatchEngine.UNKNOWN, engine.rateTerminal(engine.grid, 'O'));
    }

    @Test
    public void cpuMoveIsLegalOnScatteredPosition() {
        MatchEngine engine = new MatchEngine();
        char[] snapshot = grid("X O", " OX", "X  ");
        engine.lastPlaced = 'X';
        int move = engine.pickComputerMove(snapshot, engine.noughts);
        assertTrue(move >= 1 && move <= 9);
    }

    @Test
    public void cpuReplyKeepsCrossesFromInstantWin() {
        MatchEngine engine = new MatchEngine();
        char[] snapshot = grid("   ", "   ", "XX ");
        engine.lastPlaced = 'X';
        int move = engine.pickComputerMove(snapshot, engine.noughts);
        snapshot[move - 1] = 'O';
        engine.lastPlaced = 'O';
        assertEquals(MatchOutcome.IN_PROGRESS, engine.resolveOutcome(snapshot));
    }

    @Test
    public void cpuTakesCenterWhenItIsOnlyHole() {
        MatchEngine engine = new MatchEngine();
        char[] snapshot = grid("OXO", "X X", "OXO");
        engine.lastPlaced = 'X';
        assertEquals(5, engine.pickComputerMove(snapshot, engine.noughts));
    }

    @Test
    public void cpuCompletesTopRowWhenTwoNoughtsReady() {
        MatchEngine engine = new MatchEngine();
        char[] snapshot = grid("OO ", "XX ", "   ");
        engine.lastPlaced = 'X';
        assertEquals(3, engine.pickComputerMove(snapshot, engine.noughts));
    }

    @Test
    public void logHelperAcceptsAlternateTracePayloads() {
        LogHelper.traceGrid(grid(" OX", "X  ", "  X"));
        LogHelper.traceGrid(new int[] {4, 3, 2, 1, 0});
        LogHelper.traceIndexList(Arrays.asList(1, 3, 7));
        assertTrue(true);
    }

    @Test
    public void squareButtonExposesFlatIndexAndCoordinates() {
        SquareButton square = new SquareButton(8, 2, 2);
        assertEquals(8, square.flatIndex());
        assertEquals(2, square.column());
        assertEquals(2, square.rowIndex());
        assertEquals(' ', square.occupiedBy());
    }

    @Test
    public void squareButtonLocksAfterCrossPlayed() {
        SquareButton square = new SquareButton(3, 0, 1);
        square.placeMark('X');
        assertEquals('X', square.occupiedBy());
        assertFalse(square.isEnabled());
    }

    @Test(timeout = 5000)
    public void cpuReturnsInRangeOnBrandNewGrid() {
        MatchEngine engine = new MatchEngine();
        engine.lastPlaced = ' ';
        int move = engine.pickComputerMove(engine.grid, engine.noughts);
        assertTrue(move >= 1 && move <= 9);
    }

    @Test
    public void cornerClickStartsHumanAndCpuReplies() throws Exception {
        BoardView view = new BoardView(new GridLayout(3, 3));

        Field squaresField = BoardView.class.getDeclaredField("squares");
        squaresField.setAccessible(true);
        SquareButton[] squares = (SquareButton[]) squaresField.get(view);
        squares[0].doClick();

        Field engineField = BoardView.class.getDeclaredField("engine");
        engineField.setAccessible(true);
        MatchEngine engine = (MatchEngine) engineField.get(view);

        assertEquals('X', engine.grid[0]);
        int filled = 0;
        for (char cell : engine.grid) {
            if (cell != ' ') {
                filled++;
            }
        }
        assertEquals(2, filled);
        assertEquals(MatchOutcome.IN_PROGRESS, engine.outcome);
    }
}
