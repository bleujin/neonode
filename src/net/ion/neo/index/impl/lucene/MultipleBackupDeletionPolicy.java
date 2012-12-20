package net.ion.neo.index.impl.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.index.SnapshotDeletionPolicy;

class MultipleBackupDeletionPolicy extends SnapshotDeletionPolicy {
	static final String SNAPSHOT_ID = "backup";

	private IndexCommit snapshot;
	private int snapshotUsers;

	MultipleBackupDeletionPolicy() {
		super(new KeepOnlyLastCommitDeletionPolicy());
	}

	@Override
	public synchronized IndexCommit snapshot(String id) throws IOException {
		if (snapshotUsers == 0) {
			snapshot = super.snapshot(id);
		}
		// Incremented after the call to super.snapshot() so that it wont get incremented
		// if an exception (IllegalStateException if empty index) is thrown
		snapshotUsers++;
		return snapshot;
	}

	@Override
	public synchronized void release(String id) throws IOException {
		if ((--snapshotUsers) > 0)
			return;
		super.release(id);
		snapshot = null;
		if (snapshotUsers < 0) {
			snapshotUsers = 0;
			throw new IllegalStateException("Cannot release snapshot, no snapshot held");
		}
	}
}
