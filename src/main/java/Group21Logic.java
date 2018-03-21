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

    BDDFactory fact;
    BDD bdd = null;

    public void initializeBoard(int size) {
        this.size = size;
        this.board = new int[size][size];
        fact = JFactory.init(1000000,100000);
        fact.setVarNum(size*size);
        fact.printAll();
    }

    public int[][] getBoard() {
        return board;
    }

    public void insertQueen(int column, int row) {
        int bddId = getVarId(column, row);

        System.out.println(column + " - " + row);
        System.out.println(bddId);
        System.out.println("-----");

        // Place qeen

        if(board[column][row] == -1) {
            return;
        }
        board[column][row] = 1;

        ArrayList<int[]> positions = getInvalidPos(column, row);
        if (bdd == null) {
            bdd = fact.ithVar(bddId);
        } else {
            bdd = bdd.and(fact.ithVar(bddId));
        }

        if (positions.size() != 0) {
            int[] pos = positions.get(0);
            int invalidBdd = getVarId(pos[0], pos[1]);
            System.out.println(pos[0] + " - " + pos[1]);

            bdd = bdd.and(fact.nithVar(invalidBdd));
        }

        System.out.println(bdd.isZero());
        fact.printAll();
    }

    public int getVarId(int column, int row) {
        return (row * size) + column;
    }

    public void restrictId(int id) {

    }

    public ArrayList<int[]> getInvalidPos(int column, int row) {
        ArrayList<int[]> pos = new ArrayList<>();
        if (column + 1 >= size) {
            return pos;
        }

        pos.add(new int[]{column + 1, row});
        return pos;
    }

    // public int[][] getInvalidPositions(int column, int row) {
    //
    // }
}
