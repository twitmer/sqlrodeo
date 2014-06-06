package sqlrodeo;

import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;

public class TestSqlRodeo_withHsqlDb {

    @Test
    public void test() {

	URL resourceUrl = this.getClass().getResource(
		"/hsql/example1/master-script.xml");
	SqlRodeo sqlRodeo = new SqlRodeo();

	try {
	    sqlRodeo.execute(resourceUrl);
	} catch (Throwable e) {
	    e.printStackTrace();
	    fail(e.getMessage());
	}

    }

}
