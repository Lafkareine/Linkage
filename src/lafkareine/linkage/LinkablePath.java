package lafkareine.linkage;

import java.util.function.Function;

public class LinkablePath<T> extends LinkablePathBase<Holdable<? extends T>, T> {

	@Override
	protected LinkableObject[] make_concerns(LinkableObject junction, Holdable<? extends T> output) {
		return new LinkableObject[]{junction, output};
	}

	public <A>LinkablePath(Holdable<? extends A> from, Function<? super A, ? extends Holdable<? extends T>> navigator, LinkableObject... concerns) {
		super(from, navigator, concerns);
	}

	public LinkablePath() {
	}

	@Override
	protected T toValue(Holdable<? extends T> medium) {
		return medium.get();
	}
}
