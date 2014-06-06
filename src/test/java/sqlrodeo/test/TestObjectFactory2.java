package sqlrodeo.test;

import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

import org.junit.Test;

public class TestObjectFactory2 {

    @Test
    public void testHsqlObjectFactory() {

	try {
	    String dsFactoryClassname = "org.hsqldb.jdbc.JDBCDataSourceFactory";
	    String dsClassname = "org.hsqldb.jdbc.JDBCDataSource";

	    ObjectFactory of = (ObjectFactory) Class
		    .forName(dsFactoryClassname).newInstance();

	    System.out.println("Building initial context");
	    InitialContext context = new InitialContext();

	    Reference ref = new Reference(dsClassname);
	    ref.add(new StringRefAddr("database",
		    "jdbc:hsqldb:file:throwaway/myDb"));
	    ref.add(new StringRefAddr("user", "scott"));
	    ref.add(new StringRefAddr("password", "tiger"));

	    Name name = null;
	    Hashtable<?, ?> env = new Hashtable<>();
	    Object product = of.getObjectInstance(ref, name, context, env);
	    System.out.println("Product: " + product);

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

}
