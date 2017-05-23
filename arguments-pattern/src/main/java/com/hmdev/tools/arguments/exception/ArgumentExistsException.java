package com.hmdev.tools.arguments.exception;

/**
 * 
 * @author Haitham Mubarak
 *
 */
public class ArgumentExistsException extends ArgumentPatternException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2113569763879157996L;

	public ArgumentExistsException(String name){
		super("Argument "+name+" is already defined");
	}
	
	public ArgumentExistsException(){
		this("");
	}
}
