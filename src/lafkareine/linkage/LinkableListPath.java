package lafkareine.linkage;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LinkableListPath<S> extends LinkablePathBase<Collection<? extends Holdable<? extends S>>, List<? extends S>>{
	@Override
	protected LinkableObject[] make_concerns(LinkableObject junction, Collection<? extends Holdable<? extends S>> medium) {
		LinkableObject[] ret = new LinkableObject[medium.size() + 1];
		medium.toArray(ret);
		ret[medium.size()] = junction;
		return ret;
	}

	@Override
	protected List<S> toValue(Collection<? extends Holdable<? extends S>> medium) {
		return medium.stream().map(Holdable::get).collect(Collectors.toList());
	}




	public LinkableListPath() {
	}


	public <A>LinkableListPath(Holdable<? extends A> from, Function<? super A,? extends Collection<? extends Holdable<? extends S>>> navigator, LinkableObject... concerns) {
		super(from, navigator, concerns);
	}
}
