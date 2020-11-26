package util.state_machine;

import java.util.*;

public class StateMachineBuilder<I, O> {

    private State<I, O> initialState;
    final private Map<State<I, O>, List<Action<I, O>>> states;

    public StateMachineBuilder() {
        initialState = null;
        states = new HashMap<>();
    }

    @SafeVarargs
    public final StateMachineBuilder<I, O> addState(State<I, O> behaviour, Action<I, O>... actions) {
        if(initialState == null) {
            initialState = behaviour;
        }

        states.put(behaviour, Arrays.asList(actions));

        return this;
    }

    public StateMachine<I, O> build() {
        return new StateMachine<>(states, initialState);
    }
}
