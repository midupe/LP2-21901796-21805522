package pt.ulusofona.lp2.fandeisiaGame;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestTesouro {

    @Test
    public void getId() throws Exception {
        Tesouro t = new Tesouro(10,1,2,"gold");
        assertEquals(20,t.getId());
    }

    public void getPontos() throws Exception{
        Tesouro x = new Tesouro(20,2,1,"silver");
        assertEquals(0,x.getPontos());
    }
}