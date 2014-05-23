package sqlrodeo

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotEquals

import java.net.URL;

import spock.lang.Ignore
import spock.lang.Specification
import sqlrodeo.implementation.SqlRodeo

class TestSqlRodeoHsqldb extends spock.lang.Specification {


    def "Test a typical example file"(){

        given: "An XML file to process"

        URL resourceUrl = this.getClass().getResource("/hsql/example1/hsql-test.xml")
        ISqlRodeo sqlRodeo = new SqlRodeo()

        when: "The file is processed"
        sqlRodeo.execute(resourceUrl)

        then: "All is well."
        1 == 1
        println "Success!"
    }
}
