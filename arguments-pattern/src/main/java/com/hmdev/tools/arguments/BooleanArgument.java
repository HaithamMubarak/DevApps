package com.hmdev.tools.arguments;

/**
 * Boolean arguments are like --enable-something,--disable-feature
 * @author Haitham Mubarak
 *
 */
class BooleanArgument implements ArgumentDefinition {

	private String name;

	public BooleanArgument(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}

	@Override
	public String getName() {
		return name.replaceFirst("--", "");
	}

}