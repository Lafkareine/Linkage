
package lafkareine.util.linkage;

@Deprecated
public class LinkableQuick<T> extends Linkable<T> {
	

	/*
	public BasicListener<? super T> addListener(ActionListener<? super T> listener) {
		return addListener((BasicListener<Object>) listener);
	}
	
	public BasicListener<? super T> addListener(NoOldListener<? super T> listener) {
		return addListener((BasicListener<Object>) listener);
	}
	*/

	private BasicAction<T> evalater;

	private T cache;


	public LinkableQuick() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public LinkableQuick(T value) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(value);
	}
	
	public LinkableQuick(T init, ArgAction<T> evalater, LinkableObject... concerns) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(init, evalater, concerns);
	}
	
	public LinkableQuick(NoArgAction<T> evalater, LinkableObject... concerns) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(evalater, concerns);
	}

	@Deprecated
	public LinkableQuick(T init, ArgAction<T> evalater) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(init, evalater);
	}

	@Deprecated
	public LinkableQuick(NoArgAction<T> evalater) {
		// TODO 自動生成されたコンストラクター・スタブ
		set(evalater);
	}

	/*
	public final void set(T init, ArgAction<T> evalater){
		set(init, evalater, inferConcerns(evalater));
	};

	 */

	@Override
	protected void setEvalator(T init, BasicAction<T> evalater, LazyOption option, LinkableObject... concerns) {
		this.evalater = evalater;
		if(concerns == null) throw new NullPointerException(" null concerns was denied");
		launchAction(concerns);
	}

	@Override
	protected final void setValue(T value) {
		cache = value;
		evalater = null;
		launchUpdate();
	}
	
	
	protected final T getSafely() {
		return cache;
	}
	
	@Override
	protected final void action() {
		// TODO 自動生成されたメソッド・スタブ
		eval(cache);
	}

	private void eval(T arg){
		T neocache = evalater.action(arg);
		cache = neocache;
	}

	@Override
	protected void discard() {
		chklock();
		cache = null;
		evalater = null;
	}
}
