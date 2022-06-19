package lafkareine.linkage;

public class Shift extends Actable {

	public interface ConcernSetter{

		void setConcerns(LinkableObject... concerns);
	}

	public interface ConcernSelector{
		void select(ConcernSetter setter);
	}

	private class Gear extends LinkableObject implements ConcernSetter{

		private ConcernSelector selector;

		private void set(ConcernSelector selector, LinkableObject[] roots){
			this.selector = selector;
			launchAction(roots);
		}

		@Override
		protected void action() {
			selector.select(this);
		}

		@Override
		public void setConcerns(LinkableObject... concerns) {
			Shift.this.launchUpdate(arrayadd(concerns, this));
		}
	}

	public Shift(){
		super();
	}

	public Shift(ConcernSelector selector, LinkableObject... roots){
		set(selector,roots);
	}

	private Gear gear = new Gear();

	public void set(ConcernSelector selector, LinkableObject... roots){
		gear.set(selector, roots);
	}

	@Override
	protected void action() {
	}
}
