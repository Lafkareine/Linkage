package lafkareine.util.linkage.decoration;

public abstract class Decoratee<T> implements Decoratable<T>{
	public Decoration<T> decoration;

	@Override
	public Decoration<T> decoration() {
		return decoration;
	}

	protected void setup(T target){this.decoration = new Decoration<>(target);};
}
