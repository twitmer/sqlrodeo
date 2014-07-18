package sqlrodeo;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;

public class TestSqlRodeo_SchemaUpgradeVersion {

	@Test
	public void test() {

		URL resourceUrl = this.getClass().getResource(
				"/schema_upgrade_versioned/master-script.xml");
		SqlRodeo sqlRodeo = new SqlRodeo();

		try {
			sqlRodeo.execute(resourceUrl);
		} catch (Throwable e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
