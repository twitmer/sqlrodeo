package sqlrodeo.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TestVersion {

    @Test
    public void testHashCode() {
        assertNotEquals(new Version("1.2").hashCode(), new Version("1.3").hashCode());

        assertEquals(new Version("1.2").hashCode(), new Version("1.2").hashCode());
    }

    // @Test
    public void testVersion_varargs() {
        assertEquals("1", new Version(1).toDottedString());
        assertEquals("1.2", new Version(1, 2).toDottedString());
        assertEquals("1.2.0", new Version(1, 2, 0).toDottedString());
        assertEquals("1.2.0.4", new Version(1, 2, 0, 4).toDottedString());
    }

    @Test
    public void testVersion_String() {
        assertEquals("1", new Version("1").toDottedString());
        assertEquals("1.2", new Version("1.2").toDottedString());
        assertEquals("1.2.0", new Version("1.2.0").toDottedString());
        assertEquals("1.2.0.4", new Version("1.2.0.4").toDottedString());
    }

    @Test
    public void testConstructorWithInvalidValues() {

        // Test the constructor that accepts a variable number of Integers with invalid values.
        try {
            Version v = new Version((Integer)null);
            fail("Null should not be accepted by the Integer-based constructor.");
        } catch(IllegalArgumentException e) {
            // Success!
        }

        // Test the constructor that accepts a String with invalid values.
        String[] invalidStringCtorValues = new String[] {null, "", "b", "1.0b", "1.0.0-SNAPSHOT"};
        for(String value : invalidStringCtorValues) {
            try {
                Version v = new Version(value);
                fail("String-based constructor should not accept this value: " + value);
            } catch(IllegalArgumentException | NullPointerException e) {
                // Success!
                System.out.println("String-based constructor correctly rejected argument: " + value);
            }
        }
    }

    @Test
    public void testDisparateVersions() {

        Version base = new Version("1.2.0");
        Version v1 = new Version("1.2.0.0");
        Version v2 = new Version("1.2.0.1");
        
        assertTrue( base.lessThan(v1));
        assertTrue( base.lessThan(v2));

    }

    @Test
    public void testLessThan() {

        Version v1 = new Version("1.2.0.3");
        Version v2 = new Version("1.2.0.4");
        Version v3 = new Version("1.2.1.0");

        // Test v1
        assertFalse(v1.lessThan(v1));
        assertTrue(v1.lessThan(v2));
        assertTrue(v1.lessThan(v3));

        // assertFalse(v1.lt(v1));
        // assertTrue(v1.lt(v2));
        // assertTrue(v1.lt(v3));
        //
        // Test v2
        assertFalse(v2.lessThan(v1));
        assertFalse(v2.lessThan(v2));
        assertTrue(v2.lessThan(v3));

        // assertFalse(v2.lt(v1));
        // assertFalse(v2.lt(v2));
        // assertTrue(v2.lt(v3));

        // Test v3
        assertFalse(v3.lessThan(v1));
        assertFalse(v3.lessThan(v2));
        assertFalse(v3.lessThan(v3));

        // assertFalse(v3.lt(v1));
        // assertFalse(v3.lt(v2));
        // assertFalse(v3.lt(v3));
    }

    @Test
    public void testLessThanOrEqualTo() {

        Version v1 = new Version("1.2.0.3");
        Version v2 = new Version("1.2.0.4");
        Version v3 = new Version("1.2.1.0");

        // Test v1
        assertTrue(v1.lessThanOrEqualTo(v1));
        assertTrue(v1.lessThanOrEqualTo(v2));
        assertTrue(v1.lessThanOrEqualTo(v3));

        // Test v2
        assertFalse(v2.lessThanOrEqualTo(v1));
        assertTrue(v2.lessThanOrEqualTo(v2));
        assertTrue(v2.lessThanOrEqualTo(v3));

        // Test v3
        assertFalse(v3.lessThanOrEqualTo(v1));
        assertFalse(v3.lessThanOrEqualTo(v2));
        assertTrue(v3.lessThanOrEqualTo(v3));
    }

    @Test
    public void testGreaterThan() {

        Version v1 = new Version("1.2.0.3");
        Version v2 = new Version("1.2.0.4");
        Version v3 = new Version("1.2.1.0");

        // Test v1
        assertFalse(v1.greaterThan(v1));
        assertFalse(v1.greaterThan(v2));
        assertFalse(v1.greaterThan(v3));

        // Test v2
        assertTrue(v2.greaterThan(v1));
        assertFalse(v2.greaterThan(v2));
        assertFalse(v2.greaterThan(v3));

        // Test v3
        assertTrue(v3.greaterThan(v1));
        assertTrue(v3.greaterThan(v2));
        assertFalse(v3.greaterThan(v3));
    }

    @Test
    public void testGreaterThanOrEqualTo() {

        Version v1 = new Version("1.2.0.3");
        Version v2 = new Version("1.2.0.4");
        Version v3 = new Version("1.2.1.0");

        // Test v1
        assertTrue(v1.greaterThanOrEqualTo(v1));
        assertFalse(v1.greaterThanOrEqualTo(v2));
        assertFalse(v1.greaterThanOrEqualTo(v3));

        // Test v2
        assertTrue(v2.greaterThanOrEqualTo(v1));
        assertTrue(v2.greaterThanOrEqualTo(v2));
        assertFalse(v2.greaterThanOrEqualTo(v3));

        // Test v3
        assertTrue(v3.greaterThanOrEqualTo(v1));
        assertTrue(v3.greaterThanOrEqualTo(v2));
        assertTrue(v3.greaterThanOrEqualTo(v3));
    }

    @Test
    public void testEqualsObject() {
        Version v1 = new Version("1.2.0.3");
        Version v2 = new Version("1.2.0.4");

        assertTrue(v1.equals(v1));
        assertFalse(v1.equals(v2));
        assertFalse(v1.equals(null));
        assertFalse(v1.equals("Some String"));
    }
}
