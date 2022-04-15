package lafkareine.util.linkage;

import java.util.function.Function;
import java.util.function.Supplier;

/**Holdable<R>の表すR型の値から、あるHoldable<S>な変数を取り出す
 *
 * fromが更新されたとき、道は変わる
 * 道に変化があったときも、updateが走る
 * **/
abstract class LinkablePathBase<R, S> extends Variable<S> {

	private class Junction extends LinkableObject {

		private Supplier<R> supplier;

		public <A>void set(Holdable<? extends A> from, Function<? super A,? extends R> navigator, LinkableObject... concerns) {
			if(from == null)throw new NullPointerException("from is null");
			supplier = ()->{
				A value = from.get();
				return (value!=null)?navigator.apply(value):null;
			};
			launchAction(arrayadd(concerns,from));
		}

		@Override
		protected void action(){
			connect(supplier.get());
		}
	}

	private void connect(R output){
		if(output != null) {
			this.medium = output;
			launchAction(make_concerns(junction, output));
		}else {
			medium = null;
			cache = null;
			launchUpdate(junction);
		}
	}

	protected abstract LinkableObject[] make_concerns(LinkableObject junction, R medium);

	private S cache;
	private R medium;

	private final Junction junction = new Junction();

	public LinkablePathBase() {
	}


	public <A>LinkablePathBase(Holdable<? extends A> from, Function<? super A,? extends R> navigator, LinkableObject... concerns) {
		set(from, navigator, concerns);
	}


	public <A>void set(Holdable<? extends A> from, Function<? super A,? extends R> navigator, LinkableObject... concerns) {
		junction.set(from, navigator, concerns);
	}


	@Override
	protected final S getSafely() {
		return cache;
	}

	@Override
	protected void discard() {
		medium = null;
		cache = null;
		junction.reset();
	}

	protected abstract S toValue(R medium);

	@Override
	protected void action() {
		if(medium != null) {
			cache = toValue(medium);
		}else {
			cache = null;
		}
	}
}