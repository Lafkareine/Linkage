package lafkareine.util.linkage;

import java.util.function.Function;
import java.util.function.Supplier;

public class LinkableFunction<T> extends Variable<T> {

	private T cache;
	private Supplier<T> action;

	public final <R>void typed(Holdable<? extends R> from, Function<? super R, ? extends T> action, LinkableObject... concerns){
		this.action = () -> action.apply(from.get());
		launchAction(arrayadd(concerns,from));
	};

	public final <R extends LinkableObject>void raw(R from, Function<? super R, ? extends T> action, LinkableObject... concerns){
		this.action = () -> action.apply(from);
		launchAction(arrayadd(concerns,from));
	};

	@Override
	protected T getSafely() {
		return cache;
	}

	@Override
	protected void discard() {
		cache = null;
		action = null;
	}

	@Override
	protected void action() {
		cache = action.get();
	}

	public <R>LinkableFunction(Holdable<? extends R> from, Function<? super R, ? extends T> action, LinkableObject... concerns){
		typed(from,action,concerns);
	};

	public <R extends LinkableObject>LinkableFunction(R from, Function<? super R, ? extends T> action, LinkableObject... concerns){
		raw(from,action,concerns);
	};

	public LinkableFunction() {
	}

	public static <R extends LinkableObject,T>LinkableFunction newAsRaw(R from, Function<? super R, ? extends T> action, LinkableObject... concerns){
		return new LinkableFunction(from,action,concerns);
	}

	public static <R,T>LinkableFunction<T> newAsTyped(Holdable<? extends R> from, Function<? super R, ? extends T> action, LinkableObject... concerns){
		return new LinkableFunction(from,action,concerns);
	}
}
