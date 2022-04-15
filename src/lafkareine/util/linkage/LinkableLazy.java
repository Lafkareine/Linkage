
package lafkareine.util.linkage;


public class LinkableLazy<T> extends Linkable<T> {

	private int lazyflag = 0;


	private BasicAction<T> evalater;


	public LinkableLazy() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public LinkableLazy(T value) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(value);
	}

	public LinkableLazy(T init, ArgAction<T> evalater, LinkableObject... concerns) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(init, evalater, concerns);
	}

	public LinkableLazy(NoArgAction<T> evalater, LinkableObject... concerns) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(evalater, concerns);
	}


	@Deprecated
	public LinkableLazy(T init, ArgAction<T> evalater) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(init, evalater);
	}

	@Deprecated
	public LinkableLazy(NoArgAction<T> evalater) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(evalater);
	}


	public LinkableLazy(T init, ArgAction<T> evalater, LazyOption option, LinkableObject... concerns) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(init, evalater, option, concerns);
	}

	public LinkableLazy(NoArgAction<T> evalater, LazyOption option, LinkableObject... concerns) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(evalater, option, concerns);
	}

	@Deprecated
	public LinkableLazy(T init, ArgAction<T> evalater, LazyOption option) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(init, evalater, option);
	}

	@Deprecated
	public LinkableLazy(NoArgAction<T> evalater, LazyOption option) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(evalater, option);
	}

	
	
	/*
	public void set(T init, BasicAction<T> evalater) {
		set(init, evalater, LazyOption.QUICK, inferConcerns(evalater));
	}
	
	public void set(NoArgAction<T> evalater) {
		set(evalater, LazyOption.QUICK, inferConcerns(evalater));
	}
	
	public void set(T init, NoRetAction<T> evalater) {
		set(init, evalater, LazyOption.QUICK, inferConcerns(evalater));
	}
*/

	//setWithOption
	@Override
	protected void setEvalator(T init, BasicAction<T> evalater, LazyOption option, LinkableObject... concerns) {

		if (concerns == null) throw new NullPointerException("Null concerns was denied");
		this.evalater = evalater;
		if (option == LazyOption.QUICK) {
			lazyflag = 0;
		} else {
			lazyflag = 2;
		}
		cache = init;

		launchAction(concerns);
	}
	
	/*
	public void set(T init, BasicAction<T> evalater, LazyOption option) {
		set(init, evalater, option, inferConcerns(evalater));
	}
	
	public void set(NoArgAction<T> evalater, LazyOption option) {
		set(evalater, option, inferConcerns(evalater));
	}
	
	public void set(T init, NoRetAction<T> evalater, LazyOption option) {
		set(init, evalater, option, inferConcerns(evalater));
	}
	 */
	
	/*
	public void setInLazy(T init, BasicAction<T> evalater) {
		setInLazy(init, evalater, inferConcerns(evalater));
	}
	
	public void setInLazy(NoArgAction<T> evalater) {
		setInLazy(evalater, inferConcerns(evalater));
	}
	
	public void setInLazy(T init, NoRetAction<T> evalater) {
		setInLazy(init, evalater, inferConcerns(evalater));
	}
	 */

	private T cache;

	@Override
	protected final void setValue(T value) {
		cache = value;
		evalater = null;
		lazyflag = 0;

		launchUpdate();
	}


	@Override
	protected final T getSafely() {
		if (lazyflag == 2) {
			cache = evalater.action(cache);
			lazyflag = 1;
		}
		return cache;
	}

	@Override
	protected final void action() {
		// TODO 自動生成されたメソッド・スタブ
		if (lazyflag == 0) {
			cache = evalater.action(cache);
		} else {
			lazyflag = 2;
		}
	}

	public int getLazyFlag() {
		return lazyflag;
	}

	@Override
	protected void discard() {
		chklock();
		cache = null;
		evalater = null;
		lazyflag = 0;
	}
}
