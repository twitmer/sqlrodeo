package org.sqlrodeo.utility;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlUtility {

	private static final Logger log = LoggerFactory.getLogger(SqlUtility.class);

	private SqlUtility() {
	}

	public static long getFirstLong(Connection connection, String query)
			throws SQLException {
		long longValue;

		// Autoclose the Statement and ResultSet.
		try (Statement s = connection.createStatement();
				ResultSet rs = s.executeQuery(query)) {

			rs.next();
			longValue = rs.getLong(1);
		}
		log.debug("Result is " + longValue + " for " + query);
		return longValue;
	}

	public static void verifyResultRow(ResultSet rs, String... expectedValues)
			throws SQLException {

		assertTrue(rs.next());

		for (int i = 0; i < expectedValues.length; ++i) {
			Object value = rs.getObject(i + 1);
			log.debug("Comparing " + i + ": " + expectedValues[i] + " against "
					+ value + " (" + value.getClass().getName() + ")");
			assertEquals(expectedValues[i], value.toString());
		}
	}
}
