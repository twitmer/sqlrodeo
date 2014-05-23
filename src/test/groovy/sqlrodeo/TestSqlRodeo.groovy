package sqlrodeo

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotEquals
import spock.lang.Ignore
import spock.lang.Specification
import sqlrodeo.implementation.SqlRodeo

class TestSqlRodeo extends spock.lang.Specification {

    
    def "Test an XML file"(){

        given: "An XML file to process"

        URL resourceUrl = this.getClass().getResource("/schema-rodeo-test.xml")
        ISqlRodeo sqlRodeo = new SqlRodeo()

        when: "The file is processed"
        sqlRodeo.execute(resourceUrl)

        then: "All is well."
        1 == 1
        println "Success!"
    }
}
