package scripts.state;

import scripts.farming.requirements.EmptyReq;
import scripts.farming.requirements.Requirement;

public class Module {
	State initial, success, critical;
	
	String description;
	Requirement<?> req;
	
	public Module(String description_, State initial_, State success_, State critical_) {
		this(description_, initial_, success_, critical_, new EmptyReq());
	}
	
	public Module(String description_, State initial_, State success_, State critical_, Requirement<?> req_) {
		description = description_;
		initial =  initial_;
		success = success_;
		critical = critical_;
		req = req_;
	}
	
	public State getInitialState() { return initial; }
	public State getSuccessState() { return success; }
	public State getCriticalState() { return critical; }
	public String toString() { return description; }
	public Requirement<?> getRequirement() { return req; }
}
