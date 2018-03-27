import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class BddQueenUtilsTest{
    
    @Test
    public void newTest1() {
        BddQueenUtils bqu = new BddQueenUtils(4);
        assertEquals(4,bqu.getSize());
    }

    @Test
    public void newTest2() {
        BddQueenUtils bqu = new BddQueenUtils(10);
        assertEquals(10,bqu.getSize());
    }


    @Test
    public void GetVerticalTest1() {
        BddQueenUtils bqu = new BddQueenUtils(10);
        assertEquals(9, bqu.getVerticals(5,2).size());
    }

    @Test
    public void GetVerticalTest2() {
        BddQueenUtils bqu = new BddQueenUtils(10);
        assertEquals(5, bqu.getVerticals(5,2).get(0)[0]);
        assertEquals(0, bqu.getVerticals(5,2).get(0)[1]);
    }

    @Test
    public void GetHorizontalsTest1() {
        BddQueenUtils bqu = new BddQueenUtils(10);
        assertEquals(9, bqu.getHorizontals(5,2).size());
    }

    @Test
    public void GetHorizontalsTest2() {
        BddQueenUtils bqu = new BddQueenUtils(10);
        assertEquals(0, bqu.getHorizontals(5,2).get(0)[0]);
        assertEquals(2, bqu.getHorizontals(5,2).get(0)[1]);
    }
    

    @Test
    public void GetDiagonalsTest1() {
        BddQueenUtils bqu = new BddQueenUtils(10);
        assertEquals(13, bqu.getDiagonals(5,2).size());
    }

    @Test
    public void GetDiagonalsTest2() {
        BddQueenUtils bqu = new BddQueenUtils(10);
        // It adds in all directions one at the time.
        // Up right
        assertEquals(6, bqu.getDiagonals(5,2).get(0)[0]);
        assertEquals(3, bqu.getDiagonals(5,2).get(0)[1]);
        // down right
        assertEquals(6, bqu.getDiagonals(5,2).get(1)[0]);
        assertEquals(1, bqu.getDiagonals(5,2).get(1)[1]);
        // up left
        assertEquals(4, bqu.getDiagonals(5,2).get(2)[0]);
        assertEquals(3, bqu.getDiagonals(5,2).get(2)[1]);
        // down left
        assertEquals(4, bqu.getDiagonals(5,2).get(3)[0]);
        assertEquals(1, bqu.getDiagonals(5,2).get(3)[1]);
    }
    
}