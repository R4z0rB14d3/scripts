package scripts.farming.requirements;


public class Both<Req extends Requirement<Req>,
					Req2 extends Requirement<Req2>> implements Requirement<Both<Req,Req2>> {
	Req requirement;
	Req2 requirement2;
	ReqHandler<Both<Req,Req2>> handler;
	
	public Both(Req requirement_, Req2 requirement2_) {
		requirement = requirement_;
		requirement2 = requirement2_;
		handler = DefaultHandler.get(this);	}
	
	public Both(Req requirement_, Req2 requirement2_, ReqHandler<Both<Req,Req2>> handler_) {
		requirement = requirement_;
		requirement2 = requirement2_;
		handler = handler_;
	}
	
	
	public boolean validate() {
		if(!requirement.validate()) return false;
		if(!requirement2.validate()) return false;
		return true;
	}

	@Override
	public void handle() {
		handler.handle(this);
	}

	
}
