package net.minecraftforge.client.gui.config;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestChangeList {

    @Test
    public void single() {
        ChangeList changeList = new ChangeList();
        List<String> list = new ArrayList<>();
        changeList.recordChange(
                () -> list.add("foo"),
                () -> list.remove("foo"),
                () -> {
                },
                () -> {
                }
        );
        assertTrue(list.contains("foo"));
        changeList.undo();
        assertFalse(list.contains("foo"));
    }

    @Test
    public void multipleLinear() {
        ChangeList changeList = new ChangeList();
        List<String> list = new ArrayList<>();
        changeList.recordChange(
                () -> list.add("foo"),
                () -> list.remove("foo"),
                () -> {
                },
                () -> {
                }
        );
        assertTrue(list.contains("foo"));
        assertFalse(list.contains("bar"));
        changeList.recordChange(
                () -> list.add("bar"),
                () -> list.remove("bar"),
                () -> {
                },
                () -> {
                }
        );
        assertTrue(list.contains("foo"));
        assertTrue(list.contains("bar"));
        changeList.undo();
        assertTrue(list.contains("foo"));
        assertFalse(list.contains("bar"));
        changeList.undo();
        assertFalse(list.contains("foo"));
        assertFalse(list.contains("bar"));
        changeList.redo();
        assertTrue(list.contains("foo"));
        assertFalse(list.contains("bar"));
        changeList.redo();
        assertTrue(list.contains("foo"));
        assertTrue(list.contains("bar"));
    }

    @Test
    public void singleDivergent() {
        ChangeList changeList = new ChangeList();
        List<String> list = new ArrayList<>();
        changeList.recordChange(
                () -> list.add("foo"),
                () -> list.remove("foo"),
                () -> {
                },
                () -> {
                }
        );
        assertTrue(list.contains("foo"));
        assertFalse(list.contains("bar"));
        changeList.undo();
        assertFalse(list.contains("foo"));
        assertFalse(list.contains("bar"));
        changeList.recordChange(
                () -> list.add("bar"),
                () -> list.remove("bar"),
                () -> {
                },
                () -> {
                }
        );
        assertFalse(list.contains("foo"));
        assertTrue(list.contains("bar"));
    }

    @Test
    public void singleCommit() {
        ChangeList changeList = new ChangeList();
        List<String> intermediate = new ArrayList<>();
        List<String> result = new ArrayList<>();
        changeList.recordChange(
                () -> intermediate.add("foo"),
                () -> intermediate.remove("foo"),
                () -> result.add("foo"),
                () -> result.remove("foo")
        );
        assertTrue(intermediate.contains("foo"));
        changeList.undo();
        assertFalse(intermediate.contains("foo"));
        changeList.commitChanged();
        assertFalse(result.contains("foo"));
        changeList.redo();
        assertTrue(intermediate.contains("foo"));
        changeList.commitChanged();
        assertTrue(result.contains("foo"));
    }

    @Test
    public void commitErrorErrorRecovery() {
        ChangeList changeList = new ChangeList();
        changeList.recordChange(
                () -> {
                },
                () -> {
                },
                () -> {
                    throw new RuntimeException("add foo");
                },
                () -> {
                }
        );
        try {
            changeList.commitChanged();
        } catch (ChangeList.CommitException e) {
            assertTrue(e.getMessage().contains("Dialog: Failed committing"));
        }
    }

    @Test
    public void commitAndRollbackErrorErrorRecovery() {
        ChangeList changeList = new ChangeList();
        changeList.recordChange(
                () -> {
                },
                () -> {
                },
                () -> {
                    throw new RuntimeException("add foo");
                },
                () -> {
                    throw new RuntimeException("remove foo");
                }
        );
        try {
            changeList.commitChanged();
        } catch (ChangeList.CommitException.RollbackException e) {
            assertTrue(e.getMessage().contains("Failed + an error occurred while rolling back"));
        }
    }

}
