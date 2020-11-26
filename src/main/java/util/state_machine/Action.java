package util.state_machine;

@FunctionalInterface
public interface Action<I, O> {
    State<I, O> execute(I input);
}
