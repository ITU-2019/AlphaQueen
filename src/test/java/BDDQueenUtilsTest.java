import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BDDQueenUtilsTest{
    
    @Test
    public void newTest1() {
        BDDQueenUtils bqu = new BDDQueenUtils(4);
        assertEquals(4,bqu.getSize());
    }

    @Test
    public void newTest2() {
        BDDQueenUtils bqu = new BDDQueenUtils(10);
        assertEquals(10,bqu.getSize());
    }

    @Test
    public void GetVerticalTest1() {
        BDDQueenUtils bqu = new BDDQueenUtils(10);
        assertEquals(9, bqu.getVerticals(5,2).size());
    }

    @Test
    public void GetVerticalTest2() {
        BDDQueenUtils bqu = new BDDQueenUtils(10);
        assertEquals(5, bqu.getVerticals(5,2).get(0)[0]);
        assertEquals(0, bqu.getVerticals(5,2).get(0)[1]);
    }

    @Test
    public void GetHorizontalsTest1() {
        BDDQueenUtils bqu = new BDDQueenUtils(10);
        assertEquals(9, bqu.getHorizontals(5,2).size());
    }

    @Test
    public void GetHorizontalsTest2() {
        BDDQueenUtils bqu = new BDDQueenUtils(10);
        assertEquals(0, bqu.getHorizontals(5,2).get(0)[0]);
        assertEquals(2, bqu.getHorizontals(5,2).get(0)[1]);
    }
    

    @Test
    public void GetDiagonalsTest1() {
        BDDQueenUtils bqu = new BDDQueenUtils(10);
        assertEquals(13, bqu.getDiagonals(5,2).size());
    }

    @Test
    public void GetDiagonalsTest2() {
        BDDQueenUtils bqu = new BDDQueenUtils(10);
        // It adds in all directions one at the time.
        // Up right
        assertEquals(6, bqu.getDiagonals(5,2).get(0)[0]);
        assertEquals(3, bqu.getDiagonals(5,2).get(0)[1]);
        // Down right
        assertEquals(6, bqu.getDiagonals(5,2).get(1)[0]);
        assertEquals(1, bqu.getDiagonals(5,2).get(1)[1]);
        // Up left
        assertEquals(4, bqu.getDiagonals(5,2).get(2)[0]);
        assertEquals(3, bqu.getDiagonals(5,2).get(2)[1]);
        // Down left
        assertEquals(4, bqu.getDiagonals(5,2).get(3)[0]);
        assertEquals(1, bqu.getDiagonals(5,2).get(3)[1]);
    }
    
    @Test
    public void getVarId1() {
        BDDQueenUtils bqu = new BDDQueenUtils(4);
        assertEquals(3,bqu.getVarId(3,0));
    }

    @Test
    public void getVarId2() {
        BDDQueenUtils bqu = new BDDQueenUtils(4);
        assertEquals(3*4,bqu.getVarId(0,3));
    }

    @Test
    public void getVarId3() {
        BDDQueenUtils bqu = new BDDQueenUtils(4);
        assertEquals(3*4 + 2,bqu.getVarId(2,3));
    }

    @Test
    public void getVarId4() {
        BDDQueenUtils bqu = new BDDQueenUtils(8);
        assertEquals(3*8 + 2,bqu.getVarId(2,3));
    }

    @Test
    public void getVarIdFromColumndID() {
        BDDQueenUtils bqu = new BDDQueenUtils(8);
        int[] a = bqu.getCollumnRowFromVarId(10);

        assertEquals(bqu.getVarId(2,1),bqu.getVarId(a[0], a[1]));
    }
}