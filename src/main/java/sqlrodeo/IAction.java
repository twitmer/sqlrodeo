package sqlrodeo;

import java.net.URL;
import java.sql.Connection;

public interface IAction {

    void execute(ISqlRodeoContext context) throws Exception;

    Connection getConnection(ISqlRodeoContext context);

    boolean isIfConditionTrue(ISqlRodeoContext context);

    long resolveLineNumber();

    URL resolveResourceUrl();

    /**
     * Evaluate whether the given node contains valid attributes and values for the intended action.
     */
    void validate() throws Exception;

}
