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

public class TestSqlRodeo_BigExample {

	Logger log = LoggerFactory.getLogger(TestSqlRodeo_BigExample.class);

	@Before
	public void setup() {
		FileUtility.recursiveDelete(new File("target/testDbs/example1"));
	}

	@Test
	public void test() throws IOException {

		// Then: Connect to generated database to verify results.
		String propsPath = "big_example/datasource.properties";
		InputStream isr = getClass().getClassLoader().getResourceAsStream(
				propsPath);
		Properties props = new Properties();
		props.load(isr);
		log.debug("Props: " + props);

		// Autoclose the DB connection when done.
		try (Connection connection = DriverManager.getConnection(
				props.getProperty("hsql_db_url"),
				props.getProperty("hsql_db_username"),
				props.getProperty("hsql_db_password"))) {

			// When: Execute the SqlRodeo XML tree.
			URL resourceUrl = this.getClass().getResource(
					"/big_example/master-script.xml");
			SqlRodeo sqlRodeo = new SqlRodeo();
			sqlRodeo.execute(resourceUrl);

			// Verify expected results.

			// Then: Verify we have the expected number of schema versions.
			assertEquals(17, SqlUtility.getFirstLong(connection,
					"select count (major) from schema_version"));

			// Then: Verify each schema version is what we expect.
			String query = "SELECT major, minor, patch FROM schema_version ORDER BY major, minor, patch";
			try (Statement stmt = connection.createStatement();
					ResultSet versions = stmt.executeQuery(query)) {

				SqlUtility.verifyResultRow(versions, "0", "0", "0");
				SqlUtility.verifyResultRow(versions, "1", "0", "0");
				SqlUtility.verifyResultRow(versions, "1", "0", "1");
				SqlUtility.verifyResultRow(versions, "1", "0", "2");
				SqlUtility.verifyResultRow(versions, "1", "0", "3");
				SqlUtility.verifyResultRow(versions, "1", "1", "0");
				SqlUtility.verifyResultRow(versions, "1", "1", "1");
				SqlUtility.verifyResultRow(versions, "1", "1", "2");
				SqlUtility.verifyResultRow(versions, "1", "1", "3");
				SqlUtility.verifyResultRow(versions, "2", "0", "0");
				SqlUtility.verifyResultRow(versions, "2", "0", "1");
				SqlUtility.verifyResultRow(versions, "2", "0", "2");
				SqlUtility.verifyResultRow(versions, "2", "0", "3");
				SqlUtility.verifyResultRow(versions, "2", "1", "0");
				SqlUtility.verifyResultRow(versions, "2", "1", "1");
				SqlUtility.verifyResultRow(versions, "2", "1", "2");
				SqlUtility.verifyResultRow(versions, "2", "1", "3");
			}

			// Then: Verify the entry count in DEPARTMENT.
			assertEquals(4, SqlUtility.getFirstLong(connection,
					"select count (*) from department"));

			// Then: Verify each Department is what we expect.
			query = "SELECT id, description, paycode FROM department ORDER BY id";
			try (Statement stmt = connection.createStatement();
					ResultSet depts = stmt.executeQuery(query)) {

				SqlUtility.verifyResultRow(depts, "0", "Department One", "100");
				SqlUtility.verifyResultRow(depts, "1", "Department Two", "100");
				SqlUtility.verifyResultRow(depts, "2", "Department Three",
						"100");
				SqlUtility.verifyResultRow(depts, "3", "Department Four", "80");
			}

			// Then: Verify the entry count in APP_USER.
			assertEquals(0, SqlUtility.getFirstLong(connection,
					"select count (*) from app_user"));

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}