package scripts.farming.requirements;


public class EmptyReq implements Requirement<EmptyReq> {


	@Override
	public boolean validate() {
		return true;
	}


	@Override
	public void handle() {
	}

}
