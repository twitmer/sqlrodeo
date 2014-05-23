package sqlrodeo;

public interface IDelegate {

    void execute(ISqlRodeoContext context, String text);

}
