package lafkareine.linkage;

/** Kotlinとかで継承して使うときにやりやすい
 *
 *
 * */
@Deprecated
public abstract class LinkableClassBase extends LinkableObject {

	public LinkableClassBase(LinkableObject... concerns){
		super();
		setConcerns(concerns);
	}

	public LinkableClassBase(){
		super();
	}

	public final void setConcerns(LinkableObject... concerns){
		launchAction(concerns);
	}
}
