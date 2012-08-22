package scripts.state;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.powerbot.game.api.methods.interactive.Players;

import scripts.state.edge.Edge;

public class State {
	ConcurrentLinkedQueue<Edge> edges = new ConcurrentLinkedQueue<Edge>();
	public static int i = 0;
	public String name;
	public int id;

	public State() {
		this("");
	}

	public State(State other) {
		this();
		edges.addAll(other.edges);
		name = other.name;
		id = other.id;
	}

	public State(String name_) {
		name = name_;
		id = i;
		i++;
	}

	public State(State other, String name_) {
		this(name_);
		edges = other.edges;
	}

	public State add(Edge e) {
		edges.add(e);
		return this;
	}

	public State run() {
		System.out.print("[" + name + "," + id + "]");
		for (Edge edge : edges) {
			try {
				State s = edge.validate();
				if (s != null) {
					cleanEdges();
					return s;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this;
	}
	
	public void cleanEdges() {
		for (Edge edge_ : edges)
			edge_.cleanup();
	}

	public void removeAllEdges() {
		edges.clear();
	}
}
