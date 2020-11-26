package util.state_machine;

import java.util.List;
import java.util.Map;

public class StateMachine<I, O> {

    private State<I, O> currentState;
    private final Map<State<I, O>, List<Action<I, O>>> states;

    public StateMachine(Map<State<I, O>, List<Action<I, O>>> states, State<I, O> startingState) {
        this.states = states;
        this.currentState = startingState;
    }

    public O update(I input) {
        O output = currentState.behaviour(input);

        for(Action<I, O> action: states.get(currentState)) {
            State<I, O> nextState = action.execute(input);
            if(currentState != nextState) {
                currentState = nextState;
                return output;
            }
        }

        return output;
    }
}
