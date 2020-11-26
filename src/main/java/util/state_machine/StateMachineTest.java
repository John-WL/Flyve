package util.state_machine;

public class StateMachineTest {

    private final StateMachine<Void, Void> stateMachine;

    public StateMachineTest() {
        StateMachineBuilder<Void, Void> stateMachineBuilder = new StateMachineBuilder<>();

        State<Void, Void> aerial = (Void) -> {
            System.out.println("do aerial");
            return null;
        };
        State<Void, Void> setupAerial = (Void) -> {
            System.out.println("no aerial but");
            return null;
        };

        stateMachineBuilder.addState(
                setupAerial,
                (Void v) -> {
                    return aerial;
                });

        stateMachineBuilder.addState(
                aerial,
                (Void v) -> {
                    return setupAerial;
                });

        stateMachine = stateMachineBuilder.build();
    }

    public void update() {
        stateMachine.update(null);
    }
}
