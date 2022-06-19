package lafkareine.linkage;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Holdable<T> extends LinkableObject {
	public abstract T get();

	/** get()で得られるものを引数のConsumerに渡します
	 * この要素に集中した処理を行いたいときに有用です
	 * */
	public final void focus(Consumer<? super T> work) {
		work.accept(get());
	}

	/** get()で得られるものを引数のFunctionに渡します
	 * この要素に集中した処理を行いたいときに有用です
	 * */
	public final <U> U focus(Function<? super T, U> work) {
		return work.apply(get());
	}



	public final <R> LinkablePath<R> path(Function<? super T, ? extends Holdable<? extends R>> navigator, LinkableObject... concerns){
		return new LinkablePath<>(this, navigator, concerns);
	}


	public final <R extends LinkableObject> LinkableListPath<R> listPath(Function<? super T, ? extends Collection<? extends Holdable<? extends R>>> navigator, LinkableObject... concerns){
		return new LinkableListPath<R>(this, navigator, concerns);
	}

	public final <R> Variable<R> map(Function<? super T, ? extends R> action){
		return new Map<T,R>(this,action);
	}

	public final <R> Variable<R> map(Function<? super T, ? extends R> action,LinkableObject... concerns){
		return new Map<T,R>(this,action,concerns);
	}

	private static class Map<T,R> extends Variable<R> {

		private R cache;
		private Function<? super T, ? extends R> action;

		private Holdable<? extends T> from;

		@Override
		protected R getSafely() {
			return cache;
		}

		@Override
		protected void discard() {
			cache = null;
			from = null;
			action = null;
		}

		@Override
		protected void action() {
			cache = action.apply(from.get());
		}


		public Map(Holdable<? extends T> from, Function<? super T, ? extends R> action){
			this.from = from;
			this.action = action;
			launchAction(from);
		};

		public Map(Holdable<? extends T> from, Function<? super T, ? extends R> action, LinkableObject... concerns){
			this.from = from;
			this.action = action;
			launchAction(arrayadd(concerns,from));
		};
	}
}