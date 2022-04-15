package lafkareine.util.linkage;

import java.util.Arrays;

public abstract class Actable extends LinkableObject {

		private Runnable[] reactors = new Runnable[]{};

	public final void addListener(Runnable added){
		reactors = Arrays.copyOf(reactors, reactors.length+1);
		reactors[reactors.length-1] = added;
	}

	public final void removeListener(Runnable removed){
		final Runnable[] reactors = this.reactors;
		for(int i = 0;i < reactors.length;i++){
			if(reactors[i] == removed){
				reactors[i] = reactors[reactors.length-1];
			}
		}
		this.reactors = Arrays.copyOf(reactors, reactors.length-1);
	}

	@Override
	protected void runListener() {
		for(Runnable e:reactors){
			e.run();
		}
	}
}
