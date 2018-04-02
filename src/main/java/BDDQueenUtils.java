import net.sf.javabdd.*;
import java.util.*;

class BDDQueenUtils {
    private BDDFactory fact;
    private int size;
    public BDDQueenUtils(int size){
        this.size = size;
        this.fact = JFactory.init(1000000, 100000); // Magic numbers
        fact.setVarNum(size * size); // Need variables for each place on board
    }

    /**
     * Try to evaluate the board, to determine if the game is win-able, with the given parameters.
     * @param  varId              The position to place a queen
     * @param  curbdd             The current bdd of the board
     * @param  missingQueens      The number of queens that needs to be placed
     * @param  availablePositions The available positions where a queen could be placed
     * @return                    Returns true if the game is win-able otherwise return false
     */
    public boolean testInsertQueen(int varId, BDD curbdd, int missingQueens, Set<Integer> avialablePositions) {
        if (missingQueens == 0) return true; // Check if all queens are placed
        placeQueen(varId, curbdd, avialablePositions);

        // Un-satisfiability if we need to place more queens than there is available positions
        if (missingQueens > avialablePositions.size()) return false;

        for (int i : avialablePositions) {
            Set<Integer> avialablePositionsCopy = new HashSet<>(avialablePositions);
            avialablePositionsCopy.remove(i);
            if (testInsertQueen(i, curbdd, missingQueens - 1, avialablePositionsCopy)) return true;
        }

        return false;
    }

    public BDD placeQueen(int varId, BDD curBDD, Set<Integer> avialablePositions) {
        avialablePositions.remove(varId);
        if (curBDD == null) {
            curBDD = fact.ithVar(varId);
        } else {
            curBDD = curBDD.and(fact.ithVar(varId));
        }
        // Add invalids
        curBDD = placeInvalid(varId,curBDD, avialablePositions);
        return curBDD;
    }

    public BDD placeInvalid(int varId, BDD curBDD, Set<Integer> avialablePositions) {
        int[] colrow = getCollumnRowFromVarId(varId);
        ArrayList<Integer> positions = getInvalidPos(colrow[0], colrow[1]);

        for (int i : positions){
            avialablePositions.remove(i);
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
