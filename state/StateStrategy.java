package scripts.state;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.strategy.Strategy;

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
		lock = true;
		currentState = currentState.run();
		lock = false;
	}
	
	boolean lock = false;

	public Condition getLock() {
		return new Condition() {
			public boolean validate() {
				return !lock;
			}
		};
	}
	
	public State getCurrentState() { return currentState; }
	
	public boolean validate() { 
		if(currentState == null) {
			if(startCondition.validate()) {
				currentState = initialState;
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

}
