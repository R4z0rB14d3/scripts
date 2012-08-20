package scripts.state.edge;

import scripts.state.Condition;
import scripts.state.State;
import scripts.state.Value;

public abstract class ValuedEither<T> extends Edge {
	Value<T> value;
	
	public ValuedEither(Condition c_, Value<T> value_) {
		super(c_,null);
		value = value_;
	}
	
	public abstract boolean validateValue(T val);
	public abstract State getFirstState(T val);// if validateValue = true
	public abstract State getSecondState(T val); // if validtateValue = false
	


	public State validate() {
		T val = value.get();
		return condition.validate() && validateValue(val) ? getFirstState(val) : getSecondState(val);
	}
}
