package scripts.state;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.game.api.methods.interactive.Players;

public class StateStrategy extends Strategy 
	implements Task, org.powerbot.concurrent.strategy.Condition {
	State currentState, initialState;
	Condition startCondition;
	
	public StateStrategy(State initial) {
		initialState = initial;
		final Constant<Boolean> b = new Constant<Boolean>(false);
		startCondition = new Condition() {
			public boolean validate() {
				if(!b.get()) {
					b.set(true);
					return true;
				} else {
					return false;
				}
			}
		};
	}
	
	public StateStrategy(State initial, Condition c) {
		initialState = initial;
		startCondition = c;
	}
	
	public void run() {
		if(currentState == null) return;
		currentState = currentState.run();
	}

	
	public State getCurrentState() { return currentState; }
	
	public boolean validate() { 
		if(currentState == null) {
			if(startCondition.validate()) {
				currentState = initialState;
				return Players.getLocal().validate();
			} else {
				return false;
			}
		} else {
			return Players.getLocal().validate();
		}
	}

}
