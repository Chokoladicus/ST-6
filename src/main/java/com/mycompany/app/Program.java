package com.mycompany.app;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

enum MatchOutcome {
    IN_PROGRESS,
    NOUGHTS_WIN,
    CROSSES_WIN,
    STALEMATE
}

class Participant {
    final char mark;
    int chosenCell;

    Participant(char mark) {
        this.mark = mark;
        chosenCell = -1;
    }
}

class MatchEngine {
    static final int SCORE_WIN = 100;
    static final int UNKNOWN = -1;
    private static final int[][] WIN_LINES = {
        {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
        {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
        {0, 4, 8}, {2, 4, 6}
    };

    MatchOutcome outcome;
    final Participant crosses;
    final Participant noughts;
    Participant turnHolder;
    char lastPlaced;
    char[] grid;

    MatchEngine() {
        crosses = new Participant('X');
        noughts = new Participant('O');
        outcome = MatchOutcome.IN_PROGRESS;
        grid = new char[9];
        clearGrid();
    }

    void clearGrid() {
        for (int i = 0; i < grid.length; i++) {
            grid[i] = ' ';
        }
    }

    MatchOutcome resolveOutcome(char[] snapshot) {
        if (isTriple(snapshot, lastPlaced)) {
            return lastPlaced == 'X' ? MatchOutcome.CROSSES_WIN : MatchOutcome.NOUGHTS_WIN;
        }
        for (char cell : snapshot) {
            if (cell == ' ') {
                return MatchOutcome.IN_PROGRESS;
            }
        }
        return MatchOutcome.STALEMATE;
    }

    private boolean isTriple(char[] snapshot, char mark) {
        for (int[] line : WIN_LINES) {
            if (snapshot[line[0]] == mark
                    && snapshot[line[1]] == mark
                    && snapshot[line[2]] == mark) {
                return true;
            }
        }
        return false;
    }

    List<Integer> vacantIndices(char[] snapshot) {
        List<Integer> free = new ArrayList<>();
        for (int i = 0; i < snapshot.length; i++) {
            if (snapshot[i] == ' ') {
                free.add(i);
            }
        }
        return free;
    }

    int rateTerminal(char[] snapshot, char forMark) {
        MatchOutcome end = resolveOutcome(snapshot);
        switch (end) {
            case CROSSES_WIN:
                return forMark == 'X' ? SCORE_WIN : -SCORE_WIN;
            case NOUGHTS_WIN:
                return forMark == 'O' ? SCORE_WIN : -SCORE_WIN;
            case STALEMATE:
                return 0;
            default:
                return UNKNOWN;
        }
    }

    int pickComputerMove(char[] snapshot, Participant cpu) {
        List<Integer> options = vacantIndices(snapshot);
        int peak = Integer.MIN_VALUE;
        List<Integer> tiedMoves = new ArrayList<>();

        for (int index : options) {
            snapshot[index] = cpu.mark;
            lastPlaced = cpu.mark;
            int value = rateTerminal(snapshot, cpu.mark);
            if (value == UNKNOWN) {
                value = -negamax(snapshot, flipMark(cpu.mark), cpu.mark);
            }
            snapshot[index] = ' ';

            int humanIndex = index + 1;
            if (value > peak) {
                peak = value;
                tiedMoves.clear();
                tiedMoves.add(humanIndex);
            } else if (value == peak) {
                tiedMoves.add(humanIndex);
            }
        }

        return tiedMoves.get(new Random().nextInt(tiedMoves.size()));
    }

    private char flipMark(char mark) {
        return mark == 'X' ? 'O' : 'X';
    }

    private int negamax(char[] snapshot, char sideToMove, char viewpoint) {
        int leaf = rateTerminal(snapshot, viewpoint);
        if (leaf != UNKNOWN) {
            return leaf;
        }

        int best = Integer.MIN_VALUE + 1;
        for (int index : vacantIndices(snapshot)) {
            snapshot[index] = sideToMove;
            lastPlaced = sideToMove;
            int score = -negamax(snapshot, flipMark(sideToMove), viewpoint);
            snapshot[index] = ' ';
            if (score > best) {
                best = score;
            }
        }
        return best;
    }
}

public class Program {
    public static void main(String[] args) throws IOException {
        JFrame window = new JFrame("Человек (X) — компьютер (O)");
        window.add(new BoardView(new GridLayout(3, 3)));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(120, 80, 460, 500);
        window.setVisible(true);
    }
}

class SquareButton extends JButton {
    private final int flatIndex;
    private final int column;
    private final int rowIndex;
    private char occupiedBy;

    SquareButton(int flatIndex, int column, int rowIndex) {
        this.flatIndex = flatIndex;
        this.column = column;
        this.rowIndex = rowIndex;
        occupiedBy = ' ';
        setText("\u00a0");
        setFont(new Font("Dialog", Font.BOLD, 32));
    }

    void placeMark(char mark) {
        occupiedBy = mark;
        setText(String.valueOf(mark));
        setEnabled(false);
    }

    char occupiedBy() {
        return occupiedBy;
    }

    int column() {
        return column;
    }

    int rowIndex() {
        return rowIndex;
    }

    int flatIndex() {
        return flatIndex;
    }
}

class LogHelper {
    private LogHelper() {
    }

    static void traceGrid(char[] cells) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            line.append(cells[i]).append(' ');
        }
        System.out.println(line.toString().trim());
    }

    static void traceGrid(int[] values) {
        StringBuilder line = new StringBuilder();
        for (int value : values) {
            line.append(value).append(' ');
        }
        System.out.println(line.toString().trim());
    }

    static void traceIndexList(List<Integer> indexes) {
        StringBuilder line = new StringBuilder();
        for (Integer index : indexes) {
            line.append(index).append(' ');
        }
        System.out.println(line.toString().trim());
    }
}

class BoardView extends JPanel implements ActionListener {
    private final MatchEngine engine;
    private final SquareButton[] squares = new SquareButton[9];

    BoardView(GridLayout layout) {
        super(layout);
        engine = new MatchEngine();
        engine.turnHolder = engine.crosses;
        for (int i = 0; i < 9; i++) {
            int col = i % 3;
            int row = i / 3;
            squares[i] = new SquareButton(i, col, row);
            squares[i].addActionListener(this);
            add(squares[i]);
        }
    }

    private void syncGridFromButtons() {
        for (int i = 0; i < squares.length; i++) {
            engine.grid[i] = squares[i].occupiedBy();
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        engine.crosses.chosenCell = -1;
        engine.noughts.chosenCell = -1;

        for (SquareButton square : squares) {
            if (event.getSource() == square) {
                square.placeMark(engine.turnHolder.mark);
            }
        }
        syncGridFromButtons();

        if (engine.turnHolder == engine.crosses) {
            runComputerTurn();
        } else {
            engine.turnHolder = engine.crosses;
            engine.lastPlaced = engine.crosses.mark;
        }

        engine.outcome = engine.resolveOutcome(engine.grid);
        finishIfNeeded();
    }

    private void runComputerTurn() {
        engine.noughts.chosenCell = engine.pickComputerMove(engine.grid, engine.noughts);
        engine.lastPlaced = engine.noughts.mark;
        engine.turnHolder = engine.noughts;
        if (engine.noughts.chosenCell > 0) {
            squares[engine.noughts.chosenCell - 1].doClick();
        }
    }

    private void finishIfNeeded() {
        switch (engine.outcome) {
            case CROSSES_WIN:
                JOptionPane.showMessageDialog(
                        this, "Выиграли крестики (X)", "Конец партии", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
                break;
            case NOUGHTS_WIN:
                JOptionPane.showMessageDialog(
                        this, "Выиграли нолики (O)", "Конец партии", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
                break;
            case STALEMATE:
                JOptionPane.showMessageDialog(
                        this, "Свободных клеток нет — ничья", "Конец партии", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
                break;
            default:
                break;
        }
    }
}
