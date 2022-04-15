package lafkareine.util.linkage.decoration;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class Decoration<T> {

	private final HashMap<String, Object> map = new HashMap<>();

	private final T target;

	public <R> R get(String key, Function<? super T,? extends R> generator){
		Object o = map.get(key);
		if(o != null){
			return (R) o;
		}else {
			R r = generator.apply(target);
			if(r == null) throw new NullPointerException("デコレーションが生成されませんでした");
			map.put(key,r);
			return r;
		}
	}

	public Decoration(T target) {
		this.target = target;
	}
}
