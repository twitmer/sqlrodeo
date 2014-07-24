package sqlrodeo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sqlrodeo.utility.FileUtility;
import sqlrodeo.utility.SqlUtility;

public class TestSqlRodeo_SchemaUpgradeDottedVersion {

	Logger log = LoggerFactory
			.getLogger(TestSqlRodeo_SchemaUpgradeDottedVersion.class);

	@Before
	public void setup() {
		FileUtility.recursiveDelete(new File(
				"target/testDbs/schema_upgrade_dottedVersion"));
	}

	@Test
	public void test() throws IOException {

		// Then: Connect to generated database to verify results.
		String propsPath = "schema_upgrade_dottedVersion/datasource.properties";
		InputStream isr = getClass().getClassLoader().getResourceAsStream(
				propsPath);
		Properties props = new Properties();
		props.load(isr);
		log.debug("Props: " + props);

		// Autoclose the DB connection when done.
		try (Connection connection = DriverManager.getConnection(
				props.getProperty("db_url"), props.getProperty("db_username"),
				props.getProperty("db_password"))) {

			// When: Execute the SqlRodeo XML tree.
			URL resourceUrl = this.getClass().getResource(
					"/schema_upgrade_dottedVersion/master-script.xml");
			SqlRodeo sqlRodeo = new SqlRodeo();
			sqlRodeo.execute(resourceUrl);

			// Verify expected results.

			// Then: Verify we have the expected number of schema versions.
			assertEquals(4, SqlUtility.getFirstLong(connection,
					"select count (major) from schema_version"));

			// Then: Verify each schema version is what we expect.
			String query = "SELECT major, minor, patch FROM schema_version ORDER BY major, minor, patch";
			try (Statement stmt = connection.createStatement();
					ResultSet versions = stmt.executeQuery(query)) {

				SqlUtility.verifyResultRow(versions, "0", "0", "0");
				SqlUtility.verifyResultRow(versions, "1", "0", "0");
				SqlUtility.verifyResultRow(versions, "1", "0", "1");
				SqlUtility.verifyResultRow(versions, "1", "1", "3");
			}

			// Then: Verify the entry count in USER.
			assertEquals(1, SqlUtility.getFirstLong(connection,
					"select count (*) from user"));

			// Then: Verify each User is what we expect.
			query = "SELECT id, name, email, nickname FROM user ORDER BY id";
			try (Statement stmt = connection.createStatement();
					ResultSet users = stmt.executeQuery(query)) {

				SqlUtility.verifyResultRow(users, "1", "Fred",
						"fred@example.com", "Freddy");
			}

			// Then: Verify the entry count in Department.
			assertEquals(0, SqlUtility.getFirstLong(connection,
					"select count (*) from department"));

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
