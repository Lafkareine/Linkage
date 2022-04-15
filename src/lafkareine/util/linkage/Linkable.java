package lafkareine.util.linkage;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class Linkable<T> extends Variable<T> {

	private Object key;
	private byte lockstate = 0;


	public void lock(Object key) {
		chklock();
		if (key != null) {
			this.key = key;
			lockstate = 2;
		} else {
			this.key = null;
			lockstate = 0;
		}
	}

	public Object lock() {
		lock(new Object());
		return key;
	}

	public Linkable<T> unlock(Object key) {
		if (lockstate == 0) {
			throw new NullPointerException("キーは設定されていません");
		}
		if (key.equals(this.key)) {
			lockstate = 1;
			return this;
		} else {
			throw new IllegalArgumentException("不正なキーです");
		}
	}

	public void unlock(Object key, Consumer<? super Linkable<T>> action) {
		action.accept(unlock(key));
		lockstate = 2;
	}

	protected final void chklock() {
		switch (lockstate) {
			case 1:
				lockstate = 2;
				break;
			case 2:
				throw new IllegalStateException("このLinkableはロックされています");
		}
	}

	//setWithOption
	protected abstract void setEvalator(T init, BasicAction<T> evalater, LazyOption option, LinkableObject... concerns);

	private void _set(T init, BasicAction evalater, LazyOption option, LinkableObject... concerns){
		chklock();
		setEvalator(init, evalater, option, concerns);
	}

	public final void set(T init, ArgAction<T> evalater, LazyOption option, LinkableObject... concerns) {
		_set(init, evalater, option, concerns);
	}

	public final void set(NoArgAction<T> evalater, LazyOption option, LinkableObject... concerns) {
		_set(null, evalater, option, concerns);
	}



	public final void set(T init, ArgAction<T> evalater, LinkableObject... concerns) {
		set(init, evalater, LazyOption.QUICK, concerns);
	}

	public final void set(NoArgAction<T> evalater, LinkableObject... concerns) {
		set(evalater, LazyOption.QUICK, concerns);
	}



	//setInLazy
	public final void setInLazy(T init, ArgAction<T> evalater, LinkableObject... concerns) {
		set(init, evalater, LazyOption.LAZY, concerns);
	}

	public final void setInLazy(NoArgAction<T> evalater, LinkableObject... concerns) {
		set(evalater, LazyOption.LAZY, concerns);
	}


	@Deprecated
	public final void set(T init, ArgAction<T> evalater) {
		throw new IllegalArgumentException("Concernsを指定してください");
	}

	;

	@Deprecated
	public final void set(NoArgAction<T> evalater) {
		throw new IllegalArgumentException("Concernsを指定してください");
	}

	;

	//setWithOption
	@Deprecated
	public final void set(T init, ArgAction<T> evalater, LazyOption option) {
		throw new IllegalArgumentException("Concernsを指定してください");
	}

	;

	@Deprecated
	public final void set(NoArgAction<T> evalater, LazyOption option) {
		throw new IllegalArgumentException("Concernsを指定してください");
	}
	//setInLazy
	@Deprecated
	public final void setInLazy(T init, ArgAction<T> evalater) {
		throw new IllegalArgumentException("Concernsを指定してください");
	}

	;

	@Deprecated
	public final void setInLazy(NoArgAction<T> evalater) {
		throw new IllegalArgumentException("Concernsを指定してください");
	}

	;


	;

	protected abstract void setValue(T value);

	public final void set(T value){
		chklock();
		setValue(value);
	}

	public final void transactionU(UnaryOperator<T> transaction) {
		set(transaction.apply(get()));
	}

	public final void transactionC(Consumer<T> transaction) {
		T cache = get();
		transaction.accept(cache);
		set(cache);
	}

	public final void transaction(Supplier<T> transaction) {
		set(transaction.get());
	}
}
