package lafkareine.linkage;

import java.util.function.Consumer;

public interface ArgAction<T> extends BasicAction<T>{
	default T action(T arg){
		return eval(arg);
	};

	T eval(T arg);

	static <T> ArgAction<T> wrap(Consumer<T> consumer){
		return x -> {consumer.accept(x);return x;};
	}
}
