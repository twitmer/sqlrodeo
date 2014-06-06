package sqlrodeo.implementation;

import java.util.regex.Matcher

import spock.lang.Specification
import sqlrodeo.IExecutionContext

public class TestJexlService extends Specification {

	def "test parameters"(){

		given:
		IExecutionContext context = new ExecutionContext()
		JexlService service =new JexlService()

		expect:
		// Invalid identifiers
		!JexlService.VARIABLE_PATTERN.matcher('').matches()
		!JexlService.VARIABLE_PATTERN.matcher('$').matches()
		!JexlService.VARIABLE_PATTERN.matcher('${').matches()
		!JexlService.VARIABLE_PATTERN.matcher('${}').matches()
		//        !ParameterSubstitutor.VARIABLE_PATTERN.matcher('${12}').matches()
		//        !ParameterSubstitutor.VARIABLE_PATTERN.matcher('${a-a}').matches()
		//        !ParameterSubstitutor.VARIABLE_PATTERN.matcher('${a@a}').matches()
		//        !ParameterSubstitutor.VARIABLE_PATTERN.matcher('${a&a}').matches()
		//        !ParameterSubstitutor.VARIABLE_PATTERN.matcher('${a"a}').matches()
		//        !ParameterSubstitutor.VARIABLE_PATTERN.matcher('${a[a}').matches()
		//        !ParameterSubstitutor.VARIABLE_PATTERN.matcher('${a]a}').matches()

		// Valid identifiers
		JexlService.VARIABLE_PATTERN.matcher('${a}').matches()
		JexlService.VARIABLE_PATTERN.matcher('${aa}').matches()
		JexlService.VARIABLE_PATTERN.matcher('${a1}').matches()
		JexlService.VARIABLE_PATTERN.matcher('${aa1}').matches()
		JexlService.VARIABLE_PATTERN.matcher('${a1a}').matches()
		JexlService.VARIABLE_PATTERN.matcher('${a_a}').matches()

		// Typical identifiers
		JexlService.VARIABLE_PATTERN.matcher('${somethi3ng}').matches()
		JexlService.VARIABLE_PATTERN.matcher('${release}').matches()
		JexlService.VARIABLE_PATTERN.matcher('${build}').matches()
		JexlService.VARIABLE_PATTERN.matcher('${version}').matches()

		when: "Target string is in double quotes"
		// WARNING: Use single quotes here to prevent groovy from doing substitution!
		JexlService.VARIABLE_PATTERN.matcher("${somethi3ng}").matches()

		then: "Groovy throws exception because it tried to do variable substitution"
		thrown(MissingPropertyException)

		when: "An expression with things to substitute"
		String value= 'Some string with ${count} variables'
		Matcher matcher = JexlService.VARIABLE_PATTERN.matcher(value)
		while( matcher.find() ){
			println "Found: " + matcher.group(0) + " in $value"
			String term = matcher.group(0)
			term = term.substring(2, term.length() - 1);
			println "Shrank to $term"
		}

		value= 'Another ${expletive} string with ${count} ${count}  ${freaking} variables'
		String
		matcher = JexlService.VARIABLE_PATTERN.matcher(value)
		while( matcher.find() ){
			println "Found: " + matcher.group(0) + " in $value"
		}

		then:
		1==1
	}

	//    def "test for real"(){
	//
	//        given:
	//        def source = 'Another ${expletive} ${sysProps.get(\"os.arch\")} string with ${count} ${count} ${freaking} variables'
	//        DbSlurperContext context = new DbSlurperContext()
	//        context.put("count", "3")
	//        context.put("expletive", Long.valueOf(5))
	//        context.put("freaking", [
	//            "gosh",
	//            "darned",
	//            "diddly",
	//            "darned"
	//        ])
	//
	//        System.properties.get("")
	//        def result = JexlService.substitute(source, context)
	//
	//        println "result is now $result"
	//    }

}

