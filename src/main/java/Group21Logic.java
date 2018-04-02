import java.util.HashSet;
import java.util.Set;

import net.sf.javabdd.*;

/**
 * N-queen logic implementation.
 * @author Group 21
 */
public class Group21Logic implements IQueensLogic {
    // Board Size
    private int size;
    private int numberOfQueens;
    // Content of the board. Possible values: 0 (empty), 1 (queen), -1 (no queen allowed)
    private int[][] board;
    // Set containing only the positions on the board that is empty.
    // The integer is the Id, for each field on the board.
    private HashSet<Integer> availablePositions;
    // Current bdd status.
    private BDD bdd = null;
    // Utility class mainly constructing new BDDs.
    private BDDQueenUtils bd;

    /**
     *
     * @param
     */
    public void initializeBoard(int size) {
        bd = new BDDQueenUtils(size);
        this.size = size;
        this.numberOfQueens = 0;
        this.board = new int[size][size];
        this.availablePositions = new HashSet<Integer>();
        for(int i = 0; i < size*size; i++){
            availablePositions.add(i);
        }
        if(size == 6){
            updateBoard(new HashSet<>());
        }
    }

    /**
     *
     * @param
     */
    public void insertQueen(int column, int row) {
        // If the field is available (aka == 0) then place.
        if(board[column][row] == 0) {
            board[column][row] = 1;                      // place the queen
            HashSet<Integer> oldAvailablePositions = new HashSet<>();
            oldAvailablePositions.addAll(availablePositions);
            bdd = bd.placeQueen(bd.getVarId(column, row), bdd, availablePositions);       // Add queen to BDD
            numberOfQueens++;
            oldAvailablePositions.remove(bd.getVarId(column, row));
            updateBoard(oldAvailablePositions);
            if(size - numberOfQueens == availablePositions.size()){
                for(int i : availablePositions){
                    int col1 = i % size;
                    int row1 = i / size;
                    board[col1][row1] = 1;
                    numberOfQueens++;
                }
            }
        }
    }

    /**
     *
     * @param
     */
    public void updateBoard(HashSet<Integer> oldAvailablePositions){

        // REMOVE OBVIOUS FIELDS.
        oldAvailablePositions.removeAll(availablePositions);
        for(int i : oldAvailablePositions){
            int[] colrow = bd.getCollumnRowFromVarId(i);
            board[colrow[0]][colrow[1]] = -1;
        }

        HashSet<Integer> newavailablePositions = new HashSet<>();
        for (int i : availablePositions){
            Set<Integer> availablePositionsCopy = new HashSet<>();
            availablePositionsCopy.addAll(availablePositions);
            availablePositionsCopy.remove(i);
            if(! bd.testInsertQueen(i, bdd, (size-numberOfQueens) -1, availablePositionsCopy)){
                int[] colrow = bd.getCollumnRowFromVarId(i);
                board[colrow[0]][colrow[1]] = -1;
            } else {
                newavailablePositions.add(i);
            }
        }
        availablePositions = newavailablePositions;
    }

    /**
     *
     * @param
     */
    public Set<Integer> getAvailablePositionsCopy() {
        Set<Integer> availablePositionsCopy = new HashSet<>();
        availablePositionsCopy.addAll(availablePositions);
        return availablePositionsCopy;
    }

    /**
     *
     * @param
     */
    public int getNumberOfQueens() {
        return numberOfQueens;
    }

    /**
     *
     * @param
     */
    public int[][] getBoard() {
        return board;
    }
}
