package scripts.state;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import scripts.state.edge.Edge;

public class QueuedState extends State {
	public Iterator<Edge> iterator;
	int i;

	public QueuedState() {
	}

	public QueuedState(State other) {
		edges.addAll(other.edges);
		name = other.name;
		id = other.id;
	}

	public QueuedState(String name_) {
		super(name_);
	}

	public QueuedState(QueuedState other, String name_) {
		this(name_);
		edges = other.edges;
	}

	public State run() {

		if (iterator == null) {
			iterator = edges.iterator();
			i = 0;
		}
		System.out.print("[" + name + "," + id + "," + i + "]");
		if (iterator.hasNext()) {
			try {
				Edge edge = iterator.next();
				i++;
				State s = edge.validate();
				if (s != null) {
					edge.cleanup();
					return s;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			iterator = null;
			i = 0;
		}
		return this;
	}

}
