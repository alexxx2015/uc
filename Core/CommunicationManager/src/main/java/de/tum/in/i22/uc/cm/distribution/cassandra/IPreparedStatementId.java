package de.tum.in.i22.uc.cm.distribution.cassandra;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

interface IPreparedStatementId {
	void prepare(Session session);
	PreparedStatement get();
}
