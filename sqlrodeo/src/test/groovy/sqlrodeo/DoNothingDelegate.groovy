package sqlrodeo

import sqlrodeo.IDelegate
import sqlrodeo.ISqlRodeoContext

class DoNothingDelegate implements IDelegate {

    public DoNothingDelegate() {
        println "Created DoNothingDelegate"
    }

    @Override
    public void execute(ISqlRodeoContext context, String text) {
        println "Running test delegate with text=$text"
        //        throw new RuntimeException("Testing out the line number reporting!")
    }
}
