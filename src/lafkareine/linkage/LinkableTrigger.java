
package lafkareine.linkage;


public class LinkableTrigger extends Actable {

	public LinkableTrigger() {
		// TODO 自動生成されたコンストラクター・スタブ
		super();
		launchUpdate();
	}

	public LinkableTrigger(LinkableObject... concerns) {
		// TODO 自動生成されたコンストラクター・スタブ
		super();
		launchUpdate(concerns);
	}

	@Override
	protected void action() {
		// TODO 自動生成されたメソッド・スタブ
	}

	public final void start(){
		launchUpdate();
	}
}
