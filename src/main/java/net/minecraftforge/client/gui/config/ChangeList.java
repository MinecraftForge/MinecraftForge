package net.minecraftforge.client.gui.config;

import java.util.ArrayList;

public class ChangeList {

    final ArrayList<Change> changes = new ArrayList<>();
    int currentChange = -1;

    public void recordChange(Runnable perform, Runnable undo, Runnable commit, Runnable rollback) {
        removeChangesAfterCurrent();
        Change change = new Change(perform, undo, commit, rollback);
        changes.add(change);
        ++currentChange;
        change.perform.run();
    }

    private void removeChangesAfterCurrent() {
        // Remove everything following currentChange + 1
        final int currentChange = this.currentChange;
        for (int i = changes.size() - 1; i > currentChange; i--)
            changes.remove(i);
    }

    public void undo() {
        if (changes.isEmpty())
            return;
        changes.get(currentChange).undo.run();
        --currentChange;
    }

    public void redo() {
        if (currentChange == changes.size() - 1)
            return;
        ++currentChange;
        changes.get(currentChange).perform.run();
    }

    public void commitChanged() throws CommitException {
        for (int committing = 0; committing <= currentChange; committing++) {
            try {
                changes.get(committing).commit.run();
            } catch (Exception commitFailed) {
                for (int rollingBack = committing; rollingBack >= 0; rollingBack--) {
                    try {
                        changes.get(rollingBack).rollback.run();
                    } catch (Exception rollbackFailed) {
                        rollbackFailed.addSuppressed(commitFailed);
                        throw new CommitException.RollbackException("Dialog: Failed + an error occurred while rolling back", rollbackFailed);
                    }
                }
                throw new CommitException("Dialog: Failed committing", commitFailed);
            }
        }
    }

    static class Change {
        /** Perform on gui version */
        final Runnable perform;
        /** Undo on gui version */
        final Runnable undo;
        /** Perform on actual config */
        final Runnable commit;
        /** Perform on actual config, rollback in case an error occurred performing a following Change */
        final Runnable rollback;

        public Change(Runnable perform, Runnable undo, Runnable commit, Runnable rollback) {
            this.perform = perform;
            this.undo = undo;
            this.commit = commit;
            this.rollback = rollback;
        }
    }

    static class CommitException extends RuntimeException {
        public CommitException(String message, Throwable cause) {
            super(message, cause);
        }

        static class RollbackException extends CommitException {
            public RollbackException(String message, Throwable cause) {
                super(message, cause);
            }
        }
    }
}
