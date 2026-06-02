package com.mycompany.app;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

enum State {
    PLAYING,
    OWIN,
    XWIN,
    DRAW
}

class Player {
    char symbol;
    int move;
}

class Game {
    static final int INF = 100;

    State state;
    Player player1;
    Player player2;
    Player cplayer;
    int nmove;
    char symbol;
    int q;
    char[] board;

    Game() {
        player1 = new Player();
        player2 = new Player();
        player1.symbol = 'X';
        player2.symbol = 'O';
        state = State.PLAYING;
        board = new char[9];
        resetBoard();
    }

    void resetBoard() {
        for (int i = 0; i < board.length; i++) {
            board[i] = ' ';
        }
    }

    State checkState(char[] boardSnapshot) {
        State result = State.PLAYING;
        if (hasLine(boardSnapshot, symbol)) {
            result = symbol == 'X' ? State.XWIN : State.OWIN;
        } else {
            result = State.DRAW;
            for (char cell : boardSnapshot) {
                if (cell == ' ') {
                    result = State.PLAYING;
                    break;
                }
            }
        }
        return result;
    }

    private boolean hasLine(char[] boardSnapshot, char mark) {
        int[][] lines = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
        };
        for (int[] line : lines) {
            if (boardSnapshot[line[0]] == mark
                    && boardSnapshot[line[1]] == mark
                    && boardSnapshot[line[2]] == mark) {
                return true;
            }
        }
        return false;
    }

    void collectEmptyCells(char[] boardSnapshot, ArrayList<Integer> emptyCells) {
        for (int i = 0; i < boardSnapshot.length; i++) {
            if (boardSnapshot[i] == ' ') {
                emptyCells.add(i);
            }
        }
    }

    int scorePosition(char[] boardSnapshot, Player forPlayer) {
        State positionState = checkState(boardSnapshot);
        if (positionState == State.XWIN || positionState == State.OWIN || positionState == State.DRAW) {
            if ((positionState == State.XWIN && forPlayer.symbol == 'X')
                    || (positionState == State.OWIN && forPlayer.symbol == 'O')) {
                return INF;
            }
            if ((positionState == State.XWIN && forPlayer.symbol == 'O')
                    || (positionState == State.OWIN && forPlayer.symbol == 'X')) {
                return -INF;
            }
            if (positionState == State.DRAW) {
                return 0;
            }
        }
        return -1;
    }

    int findBestMove(char[] boardSnapshot, Player aiPlayer) {
        int bestScore = -INF;
        int tieBreakerIndex = 0;
        ArrayList<Integer> candidates = new ArrayList<>();
        int[] equallyGood = new int[9];

        collectEmptyCells(boardSnapshot, candidates);
        while (!candidates.isEmpty()) {
            int cell = candidates.remove(0);
            boardSnapshot[cell] = aiPlayer.symbol;
            symbol = aiPlayer.symbol;

            int score = minTurn(boardSnapshot, aiPlayer);
            if (score > bestScore) {
                bestScore = score;
                tieBreakerIndex = 0;
                equallyGood[tieBreakerIndex] = cell + 1;
            } else if (score == bestScore) {
                equallyGood[++tieBreakerIndex] = cell + 1;
            }

            boardSnapshot[cell] = ' ';
        }

        if (tieBreakerIndex > 0) {
            tieBreakerIndex = new Random().nextInt(tieBreakerIndex + 1);
        }
        q = 0;
        return equallyGood[tieBreakerIndex];
    }

    int minTurn(char[] boardSnapshot, Player aiPlayer) {
        int terminal = scorePosition(boardSnapshot, aiPlayer);
        if (terminal != -1) {
            return terminal;
        }
        q++;

        int bestScore = INF;
        ArrayList<Integer> candidates = new ArrayList<>();
        collectEmptyCells(boardSnapshot, candidates);

        while (!candidates.isEmpty()) {
            int cell = candidates.remove(0);
            symbol = aiPlayer.symbol == 'X' ? 'O' : 'X';
            boardSnapshot[cell] = symbol;

            int score = maxTurn(boardSnapshot, aiPlayer);
            if (score < bestScore) {
                bestScore = score;
            }

            boardSnapshot[cell] = ' ';
        }
        return bestScore;
    }

    int maxTurn(char[] boardSnapshot, Player aiPlayer) {
        int terminal = scorePosition(boardSnapshot, aiPlayer);
        if (terminal != -1) {
            return terminal;
        }
        q++;

        int bestScore = -INF;
        ArrayList<Integer> candidates = new ArrayList<>();
        collectEmptyCells(boardSnapshot, candidates);

        while (!candidates.isEmpty()) {
            int cell = candidates.remove(0);
            symbol = aiPlayer.symbol;
            boardSnapshot[cell] = symbol;

            int score = minTurn(boardSnapshot, aiPlayer);
            if (score > bestScore) {
                bestScore = score;
            }

            boardSnapshot[cell] = ' ';
        }
        return bestScore;
    }
}

public class Program {
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Крестики-нолики");
        frame.add(new TicTacToePanel(new GridLayout(3, 3)));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100, 480, 520);
        frame.setVisible(true);
    }
}

class TicTacToeCell extends JButton {
    private final int num;
    private final int row;
    private final int col;
    private char marker;

    TicTacToeCell(int num, int x, int y) {
        this.num = num;
        row = y;
        col = x;
        marker = ' ';
        setText(" ");
        setFont(new Font("Segoe UI", Font.BOLD, 36));
    }

    void setMarker(String markerText) {
        marker = markerText.charAt(0);
        setText(markerText);
        setEnabled(false);
    }

    char getMarker() {
        return marker;
    }

    int getRow() {
        return row;
    }

    int getCol() {
        return col;
    }

    int getNum() {
        return num;
    }
}

class Utility {
    private Utility() {
    }

    static void dumpBoard(char[] board) {
        System.out.println();
        for (char cell : board) {
            System.out.print(cell + "|");
        }
        System.out.println();
    }

    static void dumpBoard(int[] values) {
        System.out.println();
        for (int value : values) {
            System.out.print(value + "|");
        }
        System.out.println();
    }

    static void dumpMoves(ArrayList<Integer> moves) {
        System.out.println();
        for (Integer move : moves) {
            System.out.print(move + "|");
        }
        System.out.println();
    }
}

class TicTacToePanel extends JPanel implements ActionListener {
    private final Game game;
    private final TicTacToeCell[] cells = new TicTacToeCell[9];

    TicTacToePanel(GridLayout layout) {
        super(layout);
        addCell(0, 0, 0);
        addCell(1, 1, 0);
        addCell(2, 2, 0);
        addCell(3, 0, 1);
        addCell(4, 1, 1);
        addCell(5, 2, 1);
        addCell(6, 0, 2);
        addCell(7, 1, 2);
        addCell(8, 2, 2);
        game = new Game();
        game.cplayer = game.player1;
    }

    private void addCell(int num, int x, int y) {
        cells[num] = new TicTacToeCell(num, x, y);
        cells[num].addActionListener(this);
        add(cells[num]);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        game.player1.move = -1;
        game.player2.move = -1;

        int index = 0;
        for (TicTacToeCell cell : cells) {
            if (event.getSource() == cell) {
                cell.setMarker(String.valueOf(game.cplayer.symbol));
            }
            game.board[index++] = cell.getMarker();
        }

        if (game.cplayer == game.player1) {
            game.player2.move = game.findBestMove(game.board, game.player2);
            game.nmove = game.player2.move;
            game.symbol = game.player2.symbol;
            game.cplayer = game.player2;
            if (game.player2.move > 0) {
                cells[game.player2.move - 1].doClick();
            }
        } else {
            game.nmove = game.player1.move;
            game.symbol = game.player1.symbol;
            game.cplayer = game.player1;
        }

        game.state = game.checkState(game.board);
        if (game.state == State.XWIN) {
            JOptionPane.showMessageDialog(this, "Победа X", "Игра окончена", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } else if (game.state == State.OWIN) {
            JOptionPane.showMessageDialog(this, "Победа O", "Игра окончена", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } else if (game.state == State.DRAW) {
            JOptionPane.showMessageDialog(this, "Ничья", "Игра окончена", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
}
