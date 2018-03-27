import java.util.HashSet;
import java.util.Set;

import net.sf.javabdd.*;

/**
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
    private HashSet<Integer> avialablePositions;

    // Current bdd status.
    private BDD bdd = null;

    // Utility class mainly constructing new BDDs.
    private BDDQueenUtils bd;


    public void initializeBoard(int size) {
        bd = new BDDQueenUtils(size);
        this.size = size;
        this.numberOfQueens = 0;
        this.board = new int[size][size];
        this.avialablePositions = new HashSet<Integer>();
        for(int i = 0; i < size*size; i++){
            avialablePositions.add(i);
        }
    }

    
    public void insertQueen(int column, int row) {
        // If the field is available (aka == 0) then place.
        if(board[column][row] == 0) {
            board[column][row] = 1;                      // place the queen
            HashSet<Integer> oldAvailablePositions = new HashSet<>();
            oldAvailablePositions.addAll(avialablePositions);
            bdd = bd.placeQueen(bd.getVarId(column, row), bdd, avialablePositions);       // Add queen to BDD
            numberOfQueens++;
            oldAvailablePositions.remove(bd.getVarId(column, row));
            updateBoard(oldAvailablePositions);
            if(size - numberOfQueens == avialablePositions.size()){
                for(int i : avialablePositions){
                    int col1 = i % size;
                    int row1 = i / size;
                    board[col1][row1] = 1;
                    numberOfQueens++;
                }
            }
        }
    }
    
    public void updateBoard(HashSet<Integer> oldAvailablePositions){
        
        // REMOVE OBVIOUS FIELDS.
        oldAvailablePositions.removeAll(avialablePositions);
        for(int i : oldAvailablePositions){
            int[] colrow = bd.getCollumnRowFromVarId(i);
           // if(board[colrow[0]][colrow[1]] != 1){
                board[colrow[0]][colrow[1]] = -1;
            //}
        }
        
        HashSet<Integer> newAvialablePositions = new HashSet<>();
        for (int i : avialablePositions){
            Set<Integer> avialablePositionsCopy = new HashSet<>();
            avialablePositionsCopy.addAll(avialablePositions);
            avialablePositionsCopy.remove(i);
            if(bd.testInsertQueen(i, bdd, (size-numberOfQueens) -1, avialablePositionsCopy , 0)){
                int[] colrow = bd.getCollumnRowFromVarId(i);
                board[colrow[0]][colrow[1]] = -1;
            } else {
                newAvialablePositions.add(i);
            }
        }
        avialablePositions = newAvialablePositions;
    }

    /* GETTERS ! */
    
    public Set<Integer> getAvailablePositionsCopy(){
        Set<Integer> avialablePositionsCopy = new HashSet<>();
        avialablePositionsCopy.addAll(avialablePositions);
        return avialablePositionsCopy;
    }
    
    public int getNumberOfQueens(){
        return numberOfQueens;
    }

    public int[][] getBoard() {
        return board;
    }
}
