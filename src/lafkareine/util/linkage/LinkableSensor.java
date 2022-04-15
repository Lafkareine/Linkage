
package lafkareine.util.linkage;


public class LinkableSensor extends LinkableObject {
	
	private boolean isActed = true;

	public LinkableSensor(boolean init, LinkableObject... concern) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(init, concern);
	}

	public LinkableSensor(LinkableObject... concern) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(concern);
	}

	public LinkableSensor(){}


	public void set(boolean init, LinkableObject... concern) {
		isActed = init;
		launchUpdate(concern);
	}

	public void set(LinkableObject... concern) {
		isActed = true;
		launchUpdate(concern);
	}
	
	@Override
	protected void action() {
		// TODO 自動生成されたメソッド・スタブ
		isActed = true;
	}
	
	public final boolean get() {
		// TODO 自動生成されたメソッド・スタブ
		if(!isReady())throw new IllegalStateException("It is unready now");
		boolean b = isActed;
		isActed = !b;
		return b;
	}
}
