package lafkareine.linkage;

import java.util.function.Consumer;
import java.util.function.Supplier;

/** Linkableを内側に持ち、すべての操作を内部に移譲する
 *
 * 異なるLinkableに対する操作を、外部には同じLinkableへの操作に見せかけることができる
 * */
public class LinkableWrap<T> extends Linkable<T>{

	private Linkable<T> inner;
	private final Junction junction = new Junction();

	private class Junction extends LinkableObject {
		private Supplier<Linkable<T>> from;

		private void set(Supplier<Linkable<T>> from, LinkableObject... concern){
			this.from = from;
			launchAction(concern);
		}

		@Override
		protected void action() {
			inner = from.get();
			LinkableWrap.this.launchUpdate(this,inner);
		}
	}

	@Override
	protected void setEvalator(T init, BasicAction<T> evalater, LazyOption option, LinkableObject... concerns) {
		inner.setEvalator(init,evalater,option,concerns);
	}

	@Override
	protected void setValue(T value) {
		inner.set(value);
	}

	@Override
	protected T getSafely() {
		return inner.get();
	}

	@Override
	protected void action() {
	}

	public LinkableWrap(Linkable<T> inner) {
		setInner(inner);
	}

	public LinkableWrap(Holdable<Linkable<T>> navigator){
		setInner(navigator);
	}

	public LinkableWrap(Supplier<Linkable<T>> navigator, LinkableObject... concern){
		setInner(navigator,concern);
	}

	public LinkableWrap() {
	}

	public void setInner(Linkable<T> inner) {
		junction.reset();
		this.inner = inner;
		launchUpdate(inner);
	}

	public void setInner(Holdable<Linkable<T>> navigator){
		Supplier<Linkable<T>> s = navigator::get;
		setInner(s, navigator);
	}

	public void setInner(Supplier<Linkable<T>> supplier, LinkableObject... concerns) {
		chklock();
		junction.set(supplier, concerns);
	}

	public Linkable<T> getInner() {
		return inner;
	}

	@Override
	protected void discard() {
		chklock();
		junction.reset();
		inner = null;
	}

	public void lock(Object key) {
		inner.lock(key);
	}

	public Object lock() {
		return inner.lock();
	}

	public Linkable<T> unlock(Object key) {
		inner.unlock(key);
		return this;
	}

	public final void unlock(Object key, Consumer<? super Linkable<T>> action) {
		action.accept(unlock(key));
	}

	public final void lockThis(Object key) {
		super.lock(key);
	}

	public final Object lockThis() {
		return super.lock();
	}

	public final Linkable<T> unlockThis(Object key) {
		return super.unlock(key);
	}

	public final void unlockThis(Object key, Consumer<? super Linkable<T>> action) {
		super.unlock(key);
	}
}
