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

    private BDD bdd = null;
    private BddQueenUtils bd;

    public void initializeBoard(int size) {
        bd = new BddQueenUtils(size);
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
            bdd = bd.placeQueen(column, row, bdd);          // Add queen to BDD
            updateBoard();
        }
    }

    public void updateBoard(){
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(board[i][j] == 0 && bd.testInsertQueen(i,j,bdd)){
                    board[i][j] = -1;
                }
            }
        }
    }
}
