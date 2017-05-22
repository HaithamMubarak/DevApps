package com.hmdev.tools.arguments;

/**
 * Property arguments like -property="the value"
 * @author Haitham Mubarak
 *
 */
class PropertyArgument implements ArgumentDefinition {

	public String value;
	private String key;

	public PropertyArgument(String key, String value) {
		this.key = key;
		value = value == null ? "" : value.trim();
		if ((value.startsWith("'") && value.endsWith("'"))
				|| (value.startsWith("\"") && value.endsWith("\""))) {
			value = value.substring(1, value.length() - 1);
		}

		this.value = value;

	}

	public String toString() {
		return key + "=" + value;
	}

	@Override
	public String getName() {
		return key.replaceFirst("-", "");
	}
}
