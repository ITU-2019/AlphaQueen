import java.util.HashSet;
import java.util.Set;

import net.sf.javabdd.*;

import java.util.*;

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
    private HashSet<Integer> availablePositions;

    // Current bdd status.
    private BDD bdd = null;

    // Utility class mainly constructing new BDDs.
    private BDDQueenUtils bd;


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
            if(bd.testInsertQueen(i, bdd, (size-numberOfQueens) -1, availablePositionsCopy , 0)){
                int[] colrow = bd.getCollumnRowFromVarId(i);
                board[colrow[0]][colrow[1]] = -1;
            } else {
                newavailablePositions.add(i);
            }
        }
        availablePositions = newavailablePositions;
    }

    /* GETTERS ! */
    
    public Set<Integer> getAvailablePositionsCopy(){
        Set<Integer> availablePositionsCopy = new HashSet<>();
        availablePositionsCopy.addAll(availablePositions);
        return availablePositionsCopy;
    }
    
    public int getNumberOfQueens(){
        return numberOfQueens;
    }

    public int[][] getBoard() {
        return board;
    }
}

class BDDQueenUtils {
    private BDDFactory fact;
    private int size;
    public BDDQueenUtils(int size){
        this.size = size;
        this.fact = JFactory.init(1000000,100000);
        fact.setVarNum(size*size);
    }

    // returns true if the varId is imposible to put a queen in.
    public boolean testInsertQueen(int varId, BDD curbdd, int missingQueens, Set<Integer> availablePositions, int depth){
        if( ! placeQueen(varId, curbdd, availablePositions).isZero()) {
            if (missingQueens == 0) return false;
            if (missingQueens > availablePositions.size()) return true;
            //System.out.println("varid: " + varId + " \t depth: " + depth + " \t Missing Queens:  " + missingQueens + " \t availablePositions: " + availablePositions );
            for(int i : availablePositions){
                Set<Integer> availablePositionsCopy = new HashSet<>();
                availablePositionsCopy.addAll(availablePositions);
                availablePositionsCopy.remove(i);
                if (! testInsertQueen(i, curbdd, missingQueens - 1, availablePositionsCopy, depth+1)) return false;
            }
            return true;
            
        } else {
            return false;
        } 
    }

    public BDD placeQueen(int varId, BDD curBDD, Set<Integer> availablePositions) {
        availablePositions.remove(varId);
        if (curBDD == null) {
            curBDD = fact.ithVar(varId);
        } else {
            curBDD = curBDD.and(fact.ithVar(varId));
        }
        // add invalids
        curBDD = placeInvalid(varId,curBDD, availablePositions);
        return curBDD;
    }

    public BDD placeInvalid(int varId, BDD curBDD, Set<Integer> availablePositions) {
        int[] colrow = getCollumnRowFromVarId(varId);
        ArrayList<Integer> positions = getInvalidPos(colrow[0], colrow[1]);

        for (int i : positions){
            availablePositions.remove(i);
            curBDD = curBDD.and(fact.nithVar(i));
        }
            
        
        return curBDD;
    }

    public int getVarId(int column, int row) {
        return (row * size) + column;
    }
    public int[] getCollumnRowFromVarId(int varId){
        return new int[]{varId % size, varId / size};
    }

    public ArrayList<Integer> getInvalidPos(int column, int row) {
        ArrayList<int[]> pos = new ArrayList<>();
        pos.addAll(getVerticals(column,row));
        pos.addAll(getHorizontals(column,row));
        pos.addAll(getDiagonals(column,row));

        ArrayList<Integer> posIdArray = new ArrayList<>();
        for(int[] ia: pos) posIdArray.add(getVarId(ia[0], ia[1]));
        
        return posIdArray;
    }
    public ArrayList<int[]> getVerticals(int column, int row) {
        ArrayList<int[]> pos = new ArrayList<>();
        for(int i = 0; i < size; i++){
            if(i != row) pos.add(new int[]{column,i});
        }
        return pos;
    }
    public ArrayList<int[]> getHorizontals(int column, int row) {
        ArrayList<int[]> pos = new ArrayList<>();
        for(int i = 0; i < size; i++){
            if(i != column) pos.add(new int[]{i,row});
        }
        return pos;
    }
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
    public int getSize(){
        return size;
    }

}