package lafkareine.linkage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class LinkableList<T> extends Variable<List<T>> {

	private class Junction extends LinkableObject {

		private Holdable<? extends Collection<? extends Holdable<? extends T>>> from;

		private void set(Holdable<? extends Collection<? extends Holdable<? extends T>>> from){
			if(from == null)throw new NullPointerException("from is null");
			this.from = from;
			launchAction(from);
		}

		@Override
		protected void action() {
			Collection<? extends Holdable<? extends T>> list = from.get();
			if(list != null) {
				holdables = list;
				LinkableObject[] array = new LinkableObject[list.size() + 1];
				list.toArray(array);
				array[list.size()] = this;
				launchAction(array);
			}else {
				holdables = null;
				cache = null;
				launchUpdate(this);
			}
		}
	}

	private Collection<? extends Holdable<? extends T>> holdables;

	private List<T> cache;

	private final Junction junction = new Junction();


	@Override
	protected List<T> getSafely() {
		return cache;
	}

	@Override
	protected void discard() {
		cache = null;
		junction.reset();
	}

	public final void set(Holdable<? extends Collection<? extends Holdable<? extends T>>> from){
		junction.set(from);
	}

	public LinkableList() {
	}

	public LinkableList(Holdable<? extends Collection<? extends Holdable<? extends T>>> from) {
		set(from);
	}

	@Override
	protected void action() {
		cache = holdables.stream().map(Holdable::get).collect(Collectors.toList());
	}
}
