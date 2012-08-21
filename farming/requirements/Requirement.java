package scripts.farming.requirements;


public interface Requirement<Req> {
	public boolean validate();
	public void handle();
}
