package co.pishfa.accelerate.initializer.model;

import co.pishfa.accelerate.initializer.api.Initializer;

@InitEntity(properties = @InitProperty(name = Initializer.ATTR_IN_PARENT, value = "tags"))
public enum Tag {

	SCIENTIFIC,
	LITRETURE;

}
