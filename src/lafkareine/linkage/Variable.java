package lafkareine.linkage;

import java.util.Arrays;

public abstract class Variable<T> extends Holdable<T> {

	private boolean has_value = false;

	public final boolean hasValue(){
		return has_value;
	}

	/**このLinkableが持つ状態を読み取ります
	 * */
	@Override
	public final T get(){
		if(isFroze()){
			if(has_value && (getLazyFlag() != 2)){
				return getSafely();
			}else{
				throw new IllegalStateException("It doesn't have any cache and can't do evaluation");
			}
		}else{
			if(isReady()){
				return getSafely();
			}else{
				throw new IllegalStateException("It is unready to provide any value");
			}
		}
	}

	protected abstract T getSafely();

	@Override
	public final void reset() {
		super.reset();
		discard();
	}

	protected abstract void discard();


	/**読み取れるんだからListenerが必要だろ、みたいな*/


	private ValueListener<? super T>[] listeners = NOTHING_LISTENER;

	protected final ValueListener<? super T>[] getListeners(){return  listeners;}

	private static final ValueListener[] NOTHING_LISTENER = new ValueListener[] {};

	private void add_listener(ValueListener<? super T> listener, boolean with_running){
		listeners = Arrays.copyOf(listeners, listeners.length + 1);
		listeners[listeners.length - 1] = listener;
		if(with_running&&isReady()){
			if(listener.wantArg()){
				listener.listen(get());
			}else{
				listener.listen(null);
			}
		}
	}

	public final ValueListener addListener(ValueListener<? super T> listener, boolean with_running) {
		add_listener(listener, with_running);
		return listener;
	}

	public final NoValueListener addListener(NoValueListener<? super T> listener, boolean with_running){
		add_listener(listener, with_running);
		return listener;
	};

	private final ValueListener addListener(ChangeListener<? super T> listener, T init_obj, boolean init_flag, boolean with_running){
		return addListener(new ValueListener<T>() {
			private T cache = init_obj;

			private boolean isCached = init_flag;

			@Override
			public void listen(T value) {
				if(isCached) {
					listener.listen(cache, value);
				}else{
					isCached = true;
				}cache = value;
			}
		}, with_running);
	}


	public final ValueListener addListener(ChangeListener<? super T> listener, boolean with_running){
		return addListener(listener,null,false,with_running);
	}

	public final ValueListener addListener(ChangeListener<? super T> listener,T init, boolean with_running){
		return addListener(listener, init, true, with_running);
	}

	public final boolean removeListener(Object listener) {
		if (listener == null)
			return false;
		int rmvnum = 0;
		for (int i = 0; i < listeners.length; i++) {
			if (listener.equals(listeners[i]) && i + ++rmvnum < listeners.length) {
				listeners[i--] = listeners[rmvnum];
			}
		}
		if (rmvnum == 0)
			return false;
		listeners = Arrays.copyOf(listeners, listeners.length - rmvnum);
		return true;
	}

	private final void test_and_telling(){
		boolean need = false;
		for (ValueListener e : listeners) {
			need = need | e.wantArg();
			if (need) break;
		}
		telling((need) ? get() : null);
	}

	/** このオブジェクトの遅延評価の可能性を返します。
	 * -1 ・・・ このオブジェクトは遅延評価に対応していません
	 * 0 ・・・　このオブジェクトは現在即時評価するように設定されています
	 * 1　・・・　このオブジェクトは現在遅延評価するように設定されていますが、値は計算済みです
	 * 2　・・・　このオブジェクトは現在遅延評価をするように設定されており、値は計算されていません
	 * */
	public int getLazyFlag(){
		return -1;
	}

	private final void telling(T value){
		for(ValueListener e:listeners){
			e.listen(value);
		}
	}

	@Override
	protected final void runListener() {
		has_value = true;
		if(getLazyFlag() == 2){
			test_and_telling();
		}else{
			telling(get());
		}
	}
}
