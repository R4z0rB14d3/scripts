package scripts.farming.requirements;

public class Optional<Req extends Requirement<Req>> implements Requirement<Optional<Req>> {
	Req req;
	public Optional(Req req_) {
		req = req_;
	}
	
	public boolean validate() {
		return true;
	}

	@Override
	public void handle() {
		req.handle();
	}
	

}
