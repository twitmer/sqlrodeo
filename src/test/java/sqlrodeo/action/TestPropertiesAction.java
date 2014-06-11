/**
 * 
 */
package sqlrodeo.action;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import sqlrodeo.Action;
import sqlrodeo.ExecutionContext;
import sqlrodeo.implementation.ExecutionContextImplementation;
import sqlrodeo.implementation.ExecutionException;
import sqlrodeo.implementation.ValidationException;

/**
 * @author twitmer
 * 
 */
public class TestPropertiesAction {

    /**
     * Test method for {@link sqlrodeo.action.PropertiesAction#validate()}.
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    // @Test
    public void testValidate() throws SAXException, IOException,
	    ParserConfigurationException {

	// Happy path, properties content.
	Element node = XmlFactory.build("<properties>a=10</properties>")
		.getDocumentElement();
	new PropertiesAction(node).validate();

	// Happy path, empty properties content.
	node = XmlFactory.build("<properties></properties>")
		.getDocumentElement();
	new PropertiesAction(node).validate();

	// Happy path, href only, no content.
	node = XmlFactory.build("<properties href=\"stuff.properties\"/>")
		.getDocumentElement();
	new PropertiesAction(node).validate();

	// Sad path: Combining Href & Content is not allowed.
	try {
	    node = XmlFactory
		    .build("<properties href=\"stuff.properties\">invalid=content</properties>")
		    .getDocumentElement();

	    PropertiesAction action = new PropertiesAction(node);
	    action.validate();
	    fail("This should have failed with a validation exception");

	} catch (ValidationException e) {
	    // This is what we want to happen.
	    assertTrue(e.getMessage().contains(
		    "cannot have content and an href"));
	    assertTrue(e.getMessage().contains("node=properties"));
	    assertTrue(e.getMessage().contains("href=stuff.properties"));
	}
    }

    @Test
    public void testValidate_sad_path_WithHrefAndContent() {

	try {
	    Element node = XmlFactory
		    .build("<properties href=\"stuff.properties\">invalid=content</properties>")
		    .getDocumentElement();

	    PropertiesAction action = new PropertiesAction(node);
	    action.validate();
	    fail("This should have failed with a validation exception");

	} catch (ValidationException e) {
	    // Happy path
	    assertTrue(e.getMessage().contains(
		    "cannot have content and an href"));
	    assertTrue(e.getMessage().contains("node=properties"));
	    assertTrue(e.getMessage().contains("href=stuff.properties"));
	} catch (Exception e) {
	    e.printStackTrace();
	    fail(e.getMessage() + ":" + e.getClass().getName());
	}
    }

    /**
     * Test method for
     * {@link sqlrodeo.action.PropertiesAction#execute(sqlrodeo.ExecutionContext)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void testExecute_inlineContent() throws Exception {
	// Happy path, properties content.
	Element node = XmlFactory.build("<properties>a=10</properties>")
		.getDocumentElement();
	Action action = new PropertiesAction(node);

	action.validate();

	ExecutionContext context = new ExecutionContextImplementation();
	action.execute(context);
    }

    /**
     * Test method for
     * {@link sqlrodeo.action.PropertiesAction#execute(sqlrodeo.ExecutionContext)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void testExecute_withValidHref() throws Exception {
	try {
	    // Happy path, properties content. Note that this
	    // refers to a properties file that lives in the same
	    // package as this unit test file.
	    Element node = XmlFactory.build(
		    "<properties href=\"empty.properties\"/>")
		    .getDocumentElement();
	    Action action = new PropertiesAction(node);

	    action.validate();

	    ExecutionContext context = new ExecutionContextImplementation();
	    action.execute(context);
	} catch (Exception e) {
	    e.printStackTrace();
	    fail(e.getMessage() + ":" + e.getClass().getName());
	}
    }

    /**
     * Test method for
     * {@link sqlrodeo.action.PropertiesAction#execute(sqlrodeo.ExecutionContext)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void testExecute_withInvalidHref() throws Exception {
	try {
	    // Happy path, properties content. Note that this
	    // refers to a properties file that lives in the same
	    // package as this unit test file.
	    Element node = XmlFactory.build(
		    "<properties href=\"invalid.properties\"/>")
		    .getDocumentElement();
	    Action action = new PropertiesAction(node);

	    action.validate();

	    ExecutionContext context = new ExecutionContextImplementation();
	    action.execute(context);

	    fail("This should have failed because it referred to a nonexistent properties file!");
	} catch (ExecutionException e) {

	    assertNotNull(e.getCause());
	    assertTrue(e.getCause() instanceof FileNotFoundException);
	    assertTrue(e.getCause().getMessage().contains("invalid.properties"));
	}
    }
}