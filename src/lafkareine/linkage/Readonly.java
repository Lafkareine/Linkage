package lafkareine.linkage;

import java.util.function.Supplier;

public final class Readonly<T> extends Holdable<T> {

	private T value;
	private Supplier<T> initializer;
	private boolean isWrited = false;

	@Override
	protected void action() {
		value = initializer.get();
	}

	public Readonly() {
	}

	public Readonly(T value) {
		set(value);
	}

	public void set(T value) {
		if (isWrited) throw new IllegalStateException("書き込みは一度だけ許可されており、二度以上の書き込みを受け付けません");
		isWrited = true;
		this.value = value;
		launchUpdate();
	}

	public Readonly(Supplier<T> initializer, Readonly<?>... args) {
		set(initializer, args);
	}

	public void set(Supplier<T> initializer, Readonly<?>... args) {
		if (isWrited) throw new IllegalStateException("書き込みは一度だけ許可されており、二度以上の書き込みを受け付けません");
		isWrited = true;
		this.initializer = initializer;
		launchAction(args);
	}

	@Override
	public T get() {
		if(!isReady()) throw new IllegalStateException("It's before a initializing");
		return value;
	}

/*
	public static void main(String[] args) {
		var a = new Readonly<>(1);
		var b = new Readonly<>(1);
		var c = new Readonly<>(() -> a.get() * b.get(),a,b);
		var d = new Readonly<Integer>(() -> a.get() + b.get(),a,b,new Readonly<>());
		System.out.println(c.get());
		System.out.println(d.get());
	}

 */
}
