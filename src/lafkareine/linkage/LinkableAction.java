
package lafkareine.linkage;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LinkableAction extends Actable {

	private Runnable action;

	public LinkableAction(Runnable action, boolean with_running, LinkableObject... concerns) {
		// TODO 自動生成されたメソッド・スタブ
		set(action, with_running, concerns);
	}

	public <U>LinkableAction(Consumer<List<U>> action, boolean with_running, LinkableObject... concerns) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(action, with_running, concerns);
	}

	public void set(Runnable action, boolean with_running, LinkableObject... concerns) {
		// TODO 自動生成されたメソッド・スタブ
		this.action = action;
		if (with_running) {
			launchAction(concerns);
		} else {
			launchUpdate(concerns);
		}
	}

	public <U>void set(Consumer<List<U>> action, boolean with_running, LinkableObject... concerns) {
		// TODO 自動生成されたメソッド・スタブ
		final ArrayList<U> list = new ArrayList<>();
		set(()->action.accept(list), with_running, concerns);
	}

	@Deprecated
	public void set(Runnable action, boolean with_running) {
		// TODO 自動生成されたメソッド・スタブ
		throw new IllegalArgumentException("concernを指定してください");
	}

	@Deprecated
	public <U>void set(Consumer<List<U>> action, boolean with_running) {
		// TODO 自動生成されたメソッド・スタブ
		throw new IllegalArgumentException("concernを指定してください");
	}

	public LinkableAction() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	protected void action() {
		// TODO 自動生成されたメソッド・スタブ
		action.run();
	}
}
