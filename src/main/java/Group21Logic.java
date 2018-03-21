import net.sf.javabdd.*;
import java.util.*;

/**
 *
 * @author
 * @version
 */

public class Group21Logic implements IQueensLogic {
    private int size;		// Size of quadratic game board (i.e. size = #rows = #columns)
    private int[][] board;	// Content of the board. Possible values: 0 (empty), 1 (queen), -1 (no queen allowed)

    private BDDFactory fact;
    private BDD bdd = null;
    private BddUtils bd;

    public void initializeBoard(int size) {
        bd = new BddUtils(size, JFactory.init(1000000,100000));
        this.size = size;
        this.board = new int[size][size];

    }

    public int[][] getBoard() {
        return board;
    }

    public void insertQueen(int column, int row) {
        // If the field is available (aka == 0) then place.
        if(board[column][row] == 0) {
            board[column][row] = 1;     // place the queen
            setBdd(bd.placeQueen(column, row, getBdd()));          // Add queen to BDD
            updateBoard();
        }
    }

    public BDD getBdd() {
        return bdd;
    }
    public void setBdd(BDD _bdd) {
        bdd = _bdd;
    }
    public void updateBoard(){
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(board[i][j] == 0 && bd.testInsertQueen(i,j,getBdd())){
                    board[i][j] = -1;
                }
            }
        }
    }
}

class BddUtils {
    private BDDFactory fact;
    private int size;

    public BddUtils(int size, BDDFactory fact){
        this.size = size;
        this.fact = fact;
        fact.setVarNum(size*size);
        fact.printAll();
    }

    public boolean testInsertQueen(int column, int row, BDD curbdd){
        return (placeQueen(column, row, curbdd)).isZero();
    }

    public BDD placeQueen(int column, int row, BDD curBDD) {
        int bddId = getVarId(column, row);
        if (curBDD == null) {
            curBDD = fact.ithVar(bddId);
        } else {
            curBDD = curBDD.and(fact.ithVar(bddId));
        }
        // add invalids
        curBDD = placeInvalid(column,row,curBDD);
        return curBDD;
    }

    public BDD placeInvalid(int column, int row, BDD curBDD) {
        ArrayList<int[]> positions = getInvalidPos(column, row);

        for (int i = 0; i < positions.size(); i++) {
            int[] pos = positions.get(i);
            int invalidBdd = getVarId(pos[0], pos[1]);
            //System.out.println(pos[0] + " - " + pos[1]);

            curBDD = curBDD.and(fact.nithVar(invalidBdd));
        }

        return curBDD;
    }

    public int getVarId(int column, int row) {
        return (row * size) + column;
    }



    public ArrayList<int[]> getInvalidPos(int column, int row) {
        ArrayList<int[]> pos = new ArrayList<>();
        pos.addAll(getVerticals(column,row));
        pos.addAll(getHorizontals(column,row));
        pos.addAll(getDiagonals(column,row));

        return pos;
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
}
