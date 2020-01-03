package pt.ulusofona.lp2.fandeisiaGame;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TestCreature {

    @Test
    public void getTipo() throws Exception {
        Creature criatura = new Creature(10, "Dragão", 2,1,10,"Este");
        assertEquals("Dragão",criatura.getTipo());
    }
}