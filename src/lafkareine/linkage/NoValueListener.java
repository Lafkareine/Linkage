package lafkareine.linkage;

public interface NoValueListener<T> extends ValueListener<T> {

	@Override
	default void listen(T value) {
		// TODO 自動生成されたメソッド・スタブ
		listen();
	}

	void listen();

	@Override
	default boolean wantArg() {
		return false;
	}
}
