package util.state_machine;

@FunctionalInterface
public interface State<I, O> {
    O behaviour(I input);
}
