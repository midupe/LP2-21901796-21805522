package pt.ulusofona.lp2.fandeisiaGame;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestBuraco {

    @Test
    public void getId() {
        Buraco b = new Buraco(10,2,1);
        assertEquals(10,b.getId());
    }

    @Test
    public void getX() {
        Buraco c = new Buraco(20,1,2);
        assertEquals(1,c.getX());
    }

    @Test
    public void getY() {
        Buraco d = new Buraco(10,3,5);
        assertEquals(5,d.getY());
    }
}