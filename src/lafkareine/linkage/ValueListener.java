package lafkareine.linkage;

public interface ValueListener<T> {

	void listen(T value);

	default boolean wantArg(){
		return true;
	}
}
