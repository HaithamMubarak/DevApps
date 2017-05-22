package com.hmdev.tools.arguments;

/**
 * 
 * @author Haitham Mubarak
 *
 */
class ArgumentDescriptor {

	public String[] choices;
	public String defaultValue;
	public boolean optional;
	public boolean isBoolean;
	
	public ArgumentDescriptor(String[] choices, String defaultValue,
			boolean optional,boolean isBoolean) {
		this.choices = choices;
		this.defaultValue = defaultValue;
		this.optional = optional;
		this.isBoolean = isBoolean;
	}
}