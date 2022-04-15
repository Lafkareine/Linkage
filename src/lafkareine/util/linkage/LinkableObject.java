package lafkareine.util.linkage;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public abstract class LinkableObject {

	/**
	 * 「キャンセルの可能性があるひずみがある」 →　負の数
	 * 「確実に再計算が必要な(親の変更が確定している)ひずみがある」 →　正の数
	 * 「ひずみがない」　→　0
	 * MAX_VALUE...action()呼び出しの直前
	 * MIN_VALUE...cancel()呼出し後
	 */
	private int mark = 1;
	private static final int SEND_DIFFER = Integer.MIN_VALUE + 1;
	private static final int SEND_READY = SEND_DIFFER + 1;
	private static final int SEND_CANCEL = SEND_READY + 1;
	private static final int FREEZE = Integer.MAX_VALUE - 1;

	private WeakReference<LinkableObject> weakref = new WeakReference<>(this);
	private static final WeakReference<LinkableObject> nullref = new WeakReference<>(null);
	private Object operation_slot;
	private LinkableObject[] concerns = BEFORE_INITIALIZING;
	public static final LinkableObject[] BEFORE_INITIALIZING = new LinkableObject[]{};
	public static final LinkableObject[] NOTHING_CONCERNS = new LinkableObject[]{};
	private WeakReference<LinkableObject>[] observers = NOTHING_OBSERVERS;
	private static final WeakReference<LinkableObject>[] NOTHING_OBSERVERS = new WeakReference[]{};


	/**
	 * isReadyはaction()の中ではfalseを返し、isReadyToActionはaction()の中でtrueを返します
	 */
	public final boolean isActing() {
		return (mark == Integer.MAX_VALUE) || (mark == Integer.MIN_VALUE);
	}

	/**
	 * このオブジェクトの状態が整合性において信頼できる状態の時、trueを返します
	 * ひずみが発生してからActionが終了するまでの間、falseを返します
	 */
	public final boolean isReady() {
		return (mark == 0) || (mark == SEND_READY) || (mark == SEND_CANCEL);
	}

	/**
	 * オブジェクトがFreezeされた状態のとき、trueを返します
	 */
	public final boolean isFroze() {
		return mark == FREEZE;
	}

	/**
	 * markが0である場合にtrueを返します
	 */
	private boolean isZero() {
		return mark == 0;
	}

	/**
	 * このオブジェクトが子オブジェクトに何らかの
	 * オブジェクトがこの状態にあるとき、観察者を追加することはできますが、削除を行った際の結果は保証されません
	 * <p>
	 * launch***のメソッドを利用した場合、観察者の状態は整合性を保つよう保証されます
	 */
	public final boolean isSending() {
		return (mark == SEND_DIFFER) || (mark == SEND_READY) || (mark == SEND_CANCEL);
	}

	private final boolean isInSpecialState() {
		switch (mark) {
			case Integer.MAX_VALUE:
			case Integer.MIN_VALUE:
			case SEND_DIFFER:
			case SEND_READY:
			case SEND_CANCEL:
			case FREEZE:
				return true;
			default:
				return false;
		}
	}


	/**
	 * 関心ごとの状態が変化したときか、またはlaunchActionが実行されたときに、LinkableBaseの機能によって自動的に実行されます
	 */
	protected abstract void action();

	/**
	 * send_readyの直後に呼び出される、ダミー関数です。リスナを駆動したい場合、これをオーバーライドするといいでしょう。
	 */
	protected void runListener() {
	}

	;

	/**
	 * Action()の結果、その前後の状態に変化がなかったとき、観察者の更新をキャンセルするために呼び出すことができます
	 * 観察者は、その観察者の関心ごとがすべてキャンセルを報告したとき、自身のAction()をキャンセルし、自身の観察者にキャンセルを報告します
	 */
	public final void RESULT_OPTION_CANCEL() {
		assert (mark != Integer.MAX_VALUE) : "the operation is called outside this::action()";
		mark = Integer.MIN_VALUE;
	}


	private enum NOTIFICATION {DIFFER, READY, CANCEL}

	/**
	 * 　観察者になんらかの通知を送ります。送る通知の種類は引数によって指定されます
	 * 通知を送信しているさいに観察者リストの状態が変更される状況を想定し、観察者リストを一時的にスタック上に退避します
	 * <p>
	 * 観察者は、関心ごとが何かの通知を送っている間に自分を観察者リストから除外しようと思った場合、removeObserverを使わずに、自身のweakrefをclearしてください。
	 */
	private void send_observers(int notification) {
		WeakReference<LinkableObject>[] ol = observers;
		observers = NOTHING_OBSERVERS;
		int temp_mark = mark;
		mark = notification;
		switch (notification) {
			case SEND_DIFFER:
				s_send_differ(ol);
				break;
			case SEND_READY:
				s_send_ready(ol);
				break;
			case SEND_CANCEL:
				s_send_cancel(ol);
				break;
			default:
				throw new IllegalArgumentException();
		}
		if (observers != NOTHING_OBSERVERS) {
			ol = optimize_observers(ol, observers.length);
			int index_to = 0;
			for (WeakReference<LinkableObject> e : observers) {
				ol[index_to++] = e;
			}
		}
		observers = ol;
		mark = temp_mark;
	}

	private void send_differ() {
		send_observers(SEND_DIFFER);
	}

	private void send_ready() {
		send_observers(SEND_READY);
		runListener();
	}

	private void send_cancel() {
		send_observers(SEND_CANCEL);
	}

	private static void s_send_differ(WeakReference<LinkableObject>[] observers) {
		for (WeakReference<LinkableObject> e : observers) {
			LinkableObject to = e.get();
			if (to != null) to.receive_differ();
		}
	}

	private static void s_send_ready(WeakReference<LinkableObject>[] observers) {
		for (WeakReference<LinkableObject> e : observers) {
			LinkableObject to = e.get();
			if (to != null) to.receive_ready();
		}
	}

	private static void s_send_cancel(WeakReference<LinkableObject>[] observers) {
		for (WeakReference<LinkableObject> e : observers) {
			LinkableObject to = e.get();
			if (to != null) to.receive_cancel();
		}
	}

	/**
	 * ///////
	 * /////////
	 * ////////
	 */
	private void receive_differ() {
		if (0 < mark) {
			mark++;
		} else {
			if (mark-- == 0) send_differ();
		}
		if (isInSpecialState()) {
			throw new IllegalStateException("too many many differs");
		}
	}

	private void receive_ready() {
		assert (mark != 0) : this + ":: the mark is 0, receive_ready isn't called anything";

		//もし「キャンセル可能なひずみ」が登録されているなら、キャンセル不可能なものに変更する。
		if (mark < 0) mark *= -1;
		//ひずみが全部消えたら更新を始められる
		if (--mark == 0) {
			//更新処理中のマークはInteger.MAX_VALUEで統一する。
			mark = Integer.MAX_VALUE;
			//事前の準備は完了したので更新を始める
			action();
			//リターンに何かしらのオプションが選択された可能性がある
			//ただし子の更新処理を始める前にマークを0にしておく必要があるので、オプションを他の変数に移す。
			int option = mark;
			//オプションによって処理を分岐させる。
			//オプションは、Integer.MAX_VALUE：普通の更新、Integer.MIN_VALUE:キャンセル、の二つを今のところ想定している
			switch (option) {
				case Integer.MAX_VALUE:
					mark = 0;
					send_ready();
					break;
				case Integer.MIN_VALUE:
					mark = 0;
					send_cancel();
					break;
				default:
					/**
					 //親情報を更新されたときなんかは、ひずみの解消されていない親が設定されるかもしれないので、receive_differが発せられた状態になっているかもしれない。
					 //その場合は、0になっていたときは更新処理ができるけど、それ以外ではとくに何もしない。
					 if (option == 0) send_ready();
					 **/
					/** setConcernInSecretlyをケジメし仕様を変更したことで、actionの中で親情報が書き換わることはなくなった
					 * よって、optionが上記二つ以外の何かになったらエラーである
					 * */
					throw new IllegalStateException("you can't make the mark to any in action()");
			}
		}
	}

	private void receive_cancel() {
		if (mark > 0) {
			receive_ready();
		} else {
			if (++mark == 0) send_cancel();
		}
	}

	/**
	 * ・action()関数を実行している間、そのインスタンスのこの関数は使用できません
	 * ・もともとの関心ごとの観察者リストから自分を除外します。このとき、関心ごとが通知処理中か考慮されます
	 * ・必要ならひずみを送信します
	 * ・関心ごとを新しいものに入れ替えます
	 * ・可能であればreadyを送信します
	 */
	protected final void launchUpdate(LinkableObject... new_concerns) {
		launchAny(false, new_concerns);
	}

	/**
	 * ・action()関数を実行している間、そのインスタンスのこの関数は使用できません
	 * ・もともとの関心ごとの観察者リストから自分を除外します。このとき、関心ごとが通知処理中か考慮されます
	 * ・必要ならひずみを送信します
	 * ・関心ごとをからっぽにします
	 * ・readyを送信します
	 */
	protected final void launchUpdate() {
		launchUpdate(NOTHING_CONCERNS);
	}

	/**
	 * ・action()関数を実行している間、そのインスタンスのこの関数は使用できません
	 * ・もともとの関心ごとの観察者リストから自分を除外します。このとき、関心ごとが通知処理中か考慮されます
	 * ・必要ならひずみを送信します
	 * ・関心ごとを新しいものに入れ替えます
	 * ・可能であればactionを呼び出します
	 * ・結果に応じて、readyかcancelを送信します
	 */
	protected final void launchAction(LinkableObject... new_concerns) {
		launchAny(true, new_concerns);
	}

	/**
	 * ・action()関数を実行している間、そのインスタンスのこの関数は使用できません
	 * ・もともとの関心ごとの観察者リストから自分を除外します。このとき、関心ごとが通知処理中か考慮されます
	 * ・必要ならひずみを送信します
	 * ・関心ごとをからっぽにします
	 * ・actionを呼び出します
	 * ・結果に応じて、readyかcancelを送信します
	 */
	protected final void launchAction() {
		launchAction(NOTHING_CONCERNS);
	}


	private void launchAny(boolean action, LinkableObject... new_concerns) {
		if (isActing()||isSending()) {
				throw new IllegalStateException("you can't launch any in action() and sending any");
		}


		// 関心ごとのうち、ひとつでも送信中だったら、観察者リストの整合性を保つため、弱参照キャッシュを入れ替える
		boolean in_sending = false;
		for (LinkableObject e : concerns) {
			in_sending |= e.isSending();
		}
		if (in_sending) {
			weakref.clear();
			weakref = new WeakReference<>(this);
		}

		// これから更新が始まるので、すでにひずんだ状態であった場合を除き、ひずみ増加の通知を行う
		// ただしすでに何かを送信中であった場合、明らかに整合性が取れなくなるのでその場合は考えない
		if (isZero()) {
			send_differ();
		}
		swapConcerns(new_concerns);

		//新しく設定された親がすでに信頼できる状態なら
		if (isZero()) {
			//actionを行うかで分岐して
			if (action) {
				mark = Integer.MAX_VALUE;
				action();
				int option = mark;
				mark = 0;
				switch (option) {
					case Integer.MAX_VALUE:
						send_ready();
						break;
					case Integer.MIN_VALUE:
						send_cancel();
						break;
					default:
						throw new IllegalStateException("you can't make the mark to any in action()");
				}
			} else {
				//actionしないならそのまま子の更新を始める
				send_ready();
			}
		}
	}

	/*
	public final void setConcernsInSecretly(LinkableBase... new_concerns) {
		// assert (mark != Integer.MIN_VALUE) : "the accepting was canceled, the operation cause a risk to brake integrity";
		assert (mark == Integer.MAX_VALUE) : "it is not refresh-event now, the operation can call in refresh-event only";

		if(concerns.length != 0) {
			weakref.clear();
			weakref = new WeakReference<>(this);
		}
		swapConcerns(new_concerns);
	}

	 */

	private void swapConcerns(LinkableObject[] new_concerns) {
		for (LinkableObject e : concerns) {
			e.remove_observer(this.weakref);
		}
		if (new_concerns == BEFORE_INITIALIZING) {
			mark = 1;
			concerns = new_concerns;
		} else {
			mark = 0;
			LinkableObject[] opt_concerns = optimize_concerns(new_concerns);
			for (LinkableObject e : opt_concerns) {
				e.add_observer(this.weakref);
				if (!e.isReady()) {
					mark += 1;
				}
			}
			concerns = opt_concerns;
		}
		if (isInSpecialState()) {
			throw new IllegalStateException("too many many differs");
		}
	}

	public final void freeze() {
		launchUpdate(BEFORE_INITIALIZING);
		mark = FREEZE;
	}

	public void reset(){
		launchUpdate(BEFORE_INITIALIZING);
	}

	public static void batch(LinkableObject... list) {
		for (LinkableObject e : list) {
			e.freeze();
		}
	}

	private void add_observer(WeakReference<LinkableObject> addthing) {
		observers = optimize_observers(observers, 1);
		observers[0] = addthing;
	}

	private void remove_observer(WeakReference<LinkableObject> remove_thing) {
		if (isSending()) throw new IllegalStateException("It is sending any message, It can't remove observer");
		for (int i = 0; i < observers.length; i++) {
			if (observers[i] == remove_thing) observers[i] = nullref;
		}
		observers = optimize_observers(observers, 0);
	}

	private static WeakReference<LinkableObject>[] optimize_observers(WeakReference<LinkableObject>[] old_array, int extra_space) {
		int retain_num = 0;
		for (WeakReference<LinkableObject> e : old_array) {
			if (e.get() != null) retain_num += 1;
		}
		if (retain_num + extra_space == old_array.length) {
			return old_array;
		} else {
			WeakReference<LinkableObject>[] new_array = new WeakReference[retain_num + extra_space];
			//ケツから詰めたほうが、空きスペースを前から探索するとき早くなる
			int index_to = new_array.length;
			for (WeakReference<LinkableObject> e : old_array) {
				if (e.get() != null) new_array[--index_to] = e;
			}
			return new_array;
		}
	}

	private static LinkableObject[] optimize_concerns(LinkableObject[] raw_concerns) {
		if (raw_concerns.length == 0) return raw_concerns;
		int nullnum = 0;
		//nullになっている奴をはじく
		//重複があった場合は、ひとつだけを残してあと全部nullにする
		for (int i = raw_concerns.length - 1; i > 0; i--) {
			LinkableObject slot = raw_concerns[i];
			if (slot == null) {
				nullnum += 1;
			} else {
				for (int j = 0; j < i; j++) {
					if (raw_concerns[j] == slot) {
						raw_concerns[i] = null;
						nullnum += 1;
						break;
					}
				}
			}
		}
		if (nullnum == 0) {
			return raw_concerns;
		} else {
			LinkableObject[] optimized_concerns = new LinkableObject[raw_concerns.length - nullnum];
			int index_to = 0;
			for (LinkableObject e : raw_concerns) {
				optimized_concerns[index_to++] = e;
			}
			return optimized_concerns;
		}
	}

	protected static LinkableObject[] inferConcerns(Object worker) {
		if (worker != null) {
			Class evalclass = worker.getClass();
			try {
				Field[] fields = evalclass.getDeclaredFields();
				LinkableObject[] buffer = new LinkableObject[fields.length];
				int i = 0;
				for (Field fld : evalclass.getDeclaredFields()) {
					if (LinkableObject.class.isAssignableFrom(fld.getType())) {
						fld.setAccessible(true);
						buffer[i++] = (LinkableObject) fld.get(worker);
					}
				}

				LinkableObject[] array = new LinkableObject[i];
				for (int j = 0; j < i; j++) {
					array[j] = buffer[j];
				}
				return array;
			} catch (SecurityException e) {
				// TODO: handle exception
				System.err.println("セキュリティエラー：リアクティブシステム内で依存親の自動推測を行おうとしたが、リフレクションを行えなかった");
				System.err.println("設定は行われなかった");
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				// TODO 自動生成された catch ブロック
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				// TODO 自動生成された catch ブロック
				throw new RuntimeException(e);
			}

		}
		return null;
	}

	protected static LinkableObject[] arrayadd(LinkableObject[] array, LinkableObject one){
		LinkableObject[] to = new LinkableObject[array.length+1];
		int i = 0;
		while(i<array.length){
			to[i] = array[i];
			i+=1;
		}
		to[i] = one;
		return to;
	}
}
