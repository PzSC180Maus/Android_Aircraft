package edu.hitsz.application;

import org.junit.Test;
import static org.junit.Assert.*;

public class GameRulesTest {
    @Test public void touchCoordinatesStayInsideGameBounds() {
        assertEquals(0, Game.clamp(-10, 0, 512));
        assertEquals(512, Game.clamp(600, 0, 512));
        assertEquals(120, Game.clamp(120, 0, 512));
    }
}
