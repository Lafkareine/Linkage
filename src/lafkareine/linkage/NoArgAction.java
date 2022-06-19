package lafkareine.linkage;

public interface NoArgAction<T> extends BasicAction<T>{
	default T action(T arg){
		return create();
	};

	T create();
}
