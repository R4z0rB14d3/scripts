package scripts.state;

import scripts.farming.requirements.EmptyReq;
import scripts.farming.requirements.Requirement;
import scripts.state.edge.Edge;
import scripts.state.edge.Task;

public class SharedModule<T> extends Module {
	State entered = null;
	protected T intermediateValue = null;

	public SharedModule(String description_, State initial_, State success_,
			State critical_) {
		this(description_, initial_, success_, critical_, new EmptyReq());
	}

	public SharedModule(String description_, State initial_, State success_,
			State critical_, Requirement<?> req) {
		super(description_, initial_, success_, critical_, req);
	}

	public State addSharedStates(final State init,State succ, final T val, final T reset) {
		init.add(new Task(Condition.TRUE, initial) {
			public void run() {
				entered = init;
				intermediateValue = val;
			}
		});
		success.add(new Edge(new Condition() {
			public boolean validate() {
				intermediateValue = reset;
				return entered == init;
			}
		}, succ));
		return init;
	}
}
