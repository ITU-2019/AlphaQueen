
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.Set;

import org.junit.Test;

public class RandomPlacementTest{
    
    private int getRandomIndexFromAvailableSet(Set<Integer> set){
        int availableSize = set.size();
        int randomIndex = new Random().nextInt(availableSize);
        int i = 0;
        for(int indexVal : set){
            if(i == randomIndex){
                return indexVal;
            }
            i++;
        }
        return 0;
    }

    private void testRandomPlacement(int size){
        Group21Logic logic = new Group21Logic();
        BDDQueenUtils bd = new BDDQueenUtils(size);
        logic.initializeBoard(size);

        while(logic.getNumberOfQueens() < size){
            Set<Integer> availablePositions = logic.getAvailablePositionsCopy();
            assertTrue(availablePositions.size() > 0);
            int[] colRow = bd.getCollumnRowFromVarId(getRandomIndexFromAvailableSet(availablePositions));
            logic.insertQueen(colRow[0], colRow[1]);
        }
        assertEquals(size,logic.getNumberOfQueens());
    }

    @Test
    public void RandomPlacement5() {
        for(int i = 0; i < 10; i++){
            testRandomPlacement(5);
        }
    }
    @Test
    public void RandomPlacement6() {
        for(int i = 0; i < 10; i++){
            testRandomPlacement(6);
        }
    }
    @Test
    public void RandomPlacement7() {
        for(int i = 0; i < 10; i++){
            testRandomPlacement(7);
        }
    }
    @Test
    public void RandomPlacement8() {
        for(int i = 0; i < 10; i++){
            testRandomPlacement(8);
        }
    }
    @Test
    public void RandomPlacement9() {
        for(int i = 0; i < 10; i++){
            testRandomPlacement(8);
        }
    }

    
}