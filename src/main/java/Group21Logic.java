import java.util.HashSet;
import java.util.Set;

import net.sf.javabdd.*;

/**
 * N-queen logic implementation.
 * @author Group 21
 */
public class Group21Logic implements IQueensLogic {
    private int size; // Board Size
    private int numberOfQueens; // Number of placed queens
    private BDD bdd = null; // Current bdd status.
    private BDDQueenUtils bd; // Utility class mainly constructing new BDDs.

    // Content of the board. Possible values: 0 (empty), 1 (queen), -1 (no queen allowed)
    private int[][] board;

    // Set containing only the positions on the board that is empty.
    // The integer is the Id, for each field on the board.
    private HashSet<Integer> availablePositions;

    /**
     * Initialize game board.
     * @param size The board size
     */
    public void initializeBoard(int size) {
        bd = new BDDQueenUtils(size);
        this.size = size;
        this.numberOfQueens = 0;
        this.board = new int[size][size];

        // Mark all positions as available.
        this.availablePositions = new HashSet<Integer>();
        for (int i = 0; i < (size * size); i++) {
            availablePositions.add(i);
        }

        // On board 6x6 not all starting positions are valid.
        if (size == 6) {
            updateBoard(new HashSet<>());
        }
    }

    /**
     * Try to insert queen on board.
     * @param column The column to place on
     * @param row    The row to place on
     */
    public void insertQueen(int column, int row) {
        // Ignore if field is not available (aka != 0).
        if (board[column][row] != 0) return;

        board[column][row] = 1; // Place the queen
        HashSet<Integer> oldAvailablePositions = new HashSet<>(availablePositions);

        bdd = bd.placeQueen(bd.getVarId(column, row), bdd, availablePositions); // Add queen to BDD
        numberOfQueens++;
        oldAvailablePositions.remove(bd.getVarId(column, row));
        updateBoard(oldAvailablePositions);
        if (size - numberOfQueens == availablePositions.size()) {
            for (int i : availablePositions) {
                int col1 = i % size;
                int row1 = i / size;
                board[col1][row1] = 1;
                numberOfQueens++;
            }
        }
    }

    /**
     * Update board and remove obvious and non-obvious positions.
     * @param oldAvailablePositions The hashset of available positions to place queens
     */
    public void updateBoard(HashSet<Integer> oldAvailablePositions){

        // Remove obvious positions.
        oldAvailablePositions.removeAll(availablePositions);
        for (int i : oldAvailablePositions) {
            int[] colrow = bd.getCollumnRowFromVarId(i);
            board[colrow[0]][colrow[1]] = -1;
        }

        HashSet<Integer> newavailablePositions = new HashSet<>();
        for (int i : availablePositions) {
            Set<Integer> availablePositionsCopy = new HashSet<>();
            availablePositionsCopy.addAll(availablePositions);
            availablePositionsCopy.remove(i);
            if(! bd.testInsertQueen(i, bdd, (size - numberOfQueens) - 1, availablePositionsCopy)){
                int[] colrow = bd.getCollumnRowFromVarId(i);
                board[colrow[0]][colrow[1]] = -1;
            } else {
                newavailablePositions.add(i);
            }
        }
        availablePositions = newavailablePositions;
    }

    /**
     * Get available positions to place queens.
     * @return Set of available positions
     */
    public Set<Integer> getAvailablePositionsCopy() {
        Set<Integer> availablePositionsCopy = new HashSet<>(availablePositions);
        return availablePositionsCopy;
    }

    /**
     * Get current number of placed queens.
     * @return The number of placed queens
     */
    public int getNumberOfQueens() {
        return numberOfQueens;
    }

    /**
     * Get current game board.
     * @return The current board
     */
    public int[][] getBoard() {
        return board;
    }
}
