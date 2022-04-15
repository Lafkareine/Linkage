package lafkareine.util.linkage.decoration;

import lafkareine.util.linkage.Holdable;
import lafkareine.util.linkage.Variable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Decorator<T> extends Variable<List<T>> {

	private Supplier<List<T>> core;
	private final String key;

	private List<T> cache;

	@Override
	protected List<T> getSafely() {
		return cache;
	}

	@Override
	protected void discard() {
		core = null;
		cache = null;
	}

	public <R> void set(Holdable<? extends Collection<? extends Decoratable<? extends R>>> target, Function<? super R,? extends T> generator){
		core = () -> target.get().stream().map(x -> x.decoration().get(key,generator)).collect(Collectors.toList());
		launchAction(target);
	}

	@Override
	protected void action() {
		cache = core.get();
	}

	public Decorator(String key) {
		this.key = key;
	}

	public <R> Decorator(String key, Holdable<? extends Collection<? extends Decoratable<? extends R>>> collection, Function<? super R, ? extends T> generator) {
		this(key);
		set(collection, generator);
	}
}
