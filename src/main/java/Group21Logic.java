import java.util.HashSet;
import java.util.Set;

import net.sf.javabdd.*;

import java.util.*;

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

/**
 * N-queen logic utilities implementation.
 * @author Group 21
 */
class BDDQueenUtils {
    private BDDFactory fact;
    private int size;
    private BDD restictionBDD;

    /**
     * Constructor.
     * @param size The board size
     */
    public BDDQueenUtils(int size){
        this.size = size;
        this.fact = JFactory.init(1000000 * size * 2, 100000 * size * 2); // Magic numbers
        fact.setVarNum(size * size); // Need variables for each place on board
        generaterestrictionBDD();
    }

    public void generaterestrictionBDD(){
        // must have one queen at each row:
        restictionBDD = fact.ithVar(0);
        for(int i = 1; i < size*size; i++){
            if(i % size == 0) restictionBDD = restictionBDD.and(fact.ithVar(i));
            else restictionBDD = restictionBDD.or(fact.ithVar(i));
        }
        restictionBDD = restictionBDD.and(fact.ithVar(0));
        for(int i = 1; i < size*size; i++){
            if(i % size == 0) restictionBDD = restictionBDD.or(fact.ithVar(i));
            else restictionBDD = restictionBDD.and(fact.ithVar(i));
        }

    }

    /**
     * Try to evaluate the board, to determine if the game is win-able, with the given parameters.
     * @param  varId              The position to place a queen
     * @param  curbdd             The current bdd of the board
     * @param  missingQueens      The number of queens that needs to be placed
     * @param  availablePositions The available positions where a queen could be placed
     * @return                    Returns true if the game is win-able otherwise return false
     */
    public boolean testInsertQueen(int varId, BDD curbdd, int missingQueens, Set<Integer> availablePositions) {
        if (curbdd == null) {
            curbdd = fact.ithVar(varId);
        }
        if (missingQueens == 0) return true;
        curbdd = placeQueen(varId, curbdd, availablePositions);
        if (restictionBDD.restrict(curbdd).isZero()){
            return false;
        }

        // Check if all queens are placed

        // Un-satisfiability if we need to place more queens than there is available positions
        if (missingQueens > availablePositions.size()) return false;

        for (int i : availablePositions) {
            Set<Integer> availablePositionsCopy = new HashSet<>(availablePositions);
            availablePositionsCopy.remove(i);
            if (testInsertQueen(i, curbdd, missingQueens - 1, availablePositionsCopy)) return true;
        }

        return false;
    }

    /**
     * Place queen in bdd.
     * @param  varId              The position to place a queen
     * @param  curBDD             The current bdd of the board
     * @param  availablePositions The available positions where a queen could be placed
     * @return                    The new bdd state
     */
    public BDD placeQueen(int varId, BDD curBDD, Set<Integer> availablePositions) {
        availablePositions.remove(varId);
        if (curBDD == null) {
            curBDD = fact.ithVar(varId);
        } else {
            curBDD = curBDD.and(fact.ithVar(varId));
        }
        curBDD = placeInvalid(varId, curBDD, availablePositions);
        return curBDD;
    }

    /**
     * Mark invalid positions on the board.
     * @param  varId              The position to place
     * @param  curBDD             The current bdd state
     * @param  availablePositions The available positions to place queens
     * @return                    The new bdd state
     */
    public BDD placeInvalid(int varId, BDD curBDD, Set<Integer> availablePositions) {
        int[] colrow = getCollumnRowFromVarId(varId);
        ArrayList<Integer> positions = getInvalidPos(colrow[0], colrow[1]);

        for (int i : positions){
            availablePositions.remove(i);
            curBDD = curBDD.and(fact.nithVar(i));
        }

        return curBDD;
    }

    /**
     * Get the variable id based on column and row.
     * @param  column Column position on board
     * @param  row    Row position on board
     * @return        The variable id in bdd on the board
     */
    public int getVarId(int column, int row) {
        return (row * size) + column;
    }

    /**
     * Get the column and row based on variable id.
     * @param  varId The variable id
     * @return       The [column, row] on the board
     */
    public int[] getCollumnRowFromVarId(int varId){
        return new int[]{varId % size, varId / size};
    }

    /**
     * Get invalid positions based on queens possible moves.
     * @param  column Column position on board
     * @param  row    Row position on board
     * @return        ArrayList of invalid [column, row] positions on the board
     */
    public ArrayList<Integer> getInvalidPos(int column, int row) {
        ArrayList<int[]> pos = new ArrayList<>();
        pos.addAll(getVerticals(column,row));
        pos.addAll(getHorizontals(column,row));
        pos.addAll(getDiagonals(column,row));

        ArrayList<Integer> posIdArray = new ArrayList<>();
        for(int[] ia: pos) posIdArray.add(getVarId(ia[0], ia[1]));

        return posIdArray;
    }

    /**
     * Get the vertical positions from given placement on board.
     * @param  column Column position on board
     * @param  row    Row position on board
     * @return        ArrayList of invalid [column, row] positions on the board
     */
    public ArrayList<int[]> getVerticals(int column, int row) {
        ArrayList<int[]> pos = new ArrayList<>();
        for(int i = 0; i < size; i++){
            if(i != row) pos.add(new int[]{column,i});
        }
        return pos;
    }

    /**
     * Get the horizontal positions from given placement on board.
     * @param  column Column position on board
     * @param  row    Row position on board
     * @return        ArrayList of invalid [column, row] positions on the board
     */
    public ArrayList<int[]> getHorizontals(int column, int row) {
        ArrayList<int[]> pos = new ArrayList<>();
        for(int i = 0; i < size; i++){
            if(i != column) pos.add(new int[]{i,row});
        }
        return pos;
    }

    /**
     * Get the diagonal positions from given placement on board.
     * @param  column Column position on board
     * @param  row    Row position on board
     * @return        ArrayList of invalid [column, row] positions on the board
     */
    public ArrayList<int[]> getDiagonals(int column, int row) {
        ArrayList<int[]> pos = new ArrayList<>();
        for(int i = 1; i < size; i++) {
            int x1 = column + i;
            int x2 = column - i;
            int y1 = row + i;
            int y2 = row - i;

            if (x1 < size && y1 < size) {
                pos.add(new int[]{x1,y1});
            }
            if (x1 < size && y2 >= 0){
                pos.add(new int[]{x1,y2});
            }
            if (x2 >= 0 && y1 < size) {
                pos.add(new int[]{x2,y1});
            }
            if (x2 >= 0 && y2 >= 0){
                pos.add(new int[]{x2,y2});
            }
        }
        return pos;
    }

    /**
     * Get size of board.
     * @return The vertical/horizontal size of the board
     */
    public int getSize(){
        return size;
    }

}
