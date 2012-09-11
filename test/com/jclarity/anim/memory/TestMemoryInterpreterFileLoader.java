package com.jclarity.anim.memory;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class TestMemoryInterpreterFileLoader {

    ExecutorService srv;

    @Before
    public void setup() {
        if (srv != null) {
            srv.shutdownNow();
        }
        srv = Executors.newSingleThreadExecutor();
    }

    @Test
    public void lowLevelTestFMIFL() throws InterruptedException {
        String[] c = {"ALLOC", "ALLOC", "KILL 0", "NOP  25", "KILL 1", "ALLOC", "TENLOC"};

        MemoryInterpreterFileLoader myFl = new MemoryInterpreterFileLoader(Arrays.asList(c));
        MemoryInstruction mi = myFl.getNextStep();
        assertEquals(OpCode.ALLOC, mi.getOp());
        mi = myFl.getNextStep();
        assertEquals(OpCode.ALLOC, mi.getOp());
        mi = myFl.getNextStep();
        assertEquals(OpCode.KILL, mi.getOp());
        assertEquals(0, mi.getParam());
        mi = myFl.getNextStep();
        assertEquals(OpCode.NOP, mi.getOp());
        assertEquals(25, mi.getParam());
        mi = myFl.getNextStep();
        assertEquals(OpCode.KILL, mi.getOp());
        assertEquals(1, mi.getParam());
        mi = myFl.getNextStep();
        assertEquals(OpCode.ALLOC, mi.getOp());
        mi = myFl.getNextStep();
        assertEquals(OpCode.LARGE_ALLOC, mi.getOp());
    }

    @Test
    public void testFMIFLbyStr() throws InterruptedException {
        MemoryModel model = new MemoryModel(2, 1, 2, 4);
        String[] c = {"ALLOC", "ALLOC", "KILL 0"};
        List<String> commands = new ArrayList<>();
        commands.addAll(Arrays.asList(c));

        executeScript(model, commands);

        assertTrue(true);
    }

    private void executeScript(MemoryModel model, List<String> commands) throws InterruptedException {
        MemoryInterpreterFileLoader myFl = new MemoryInterpreterFileLoader(commands);
        AllocatingThread at = new AllocatingThread(myFl, model);
        srv.submit(at);
        while (!at.isShutdown()) {
            Thread.sleep(250);
        }
    }
}