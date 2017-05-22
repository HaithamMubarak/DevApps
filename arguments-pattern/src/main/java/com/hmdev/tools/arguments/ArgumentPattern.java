package com.hmdev.tools.arguments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * 
 * @author Haitham Mubarak
 *
 *
 */
public class ArgumentPattern {
	

	private Map<String, ArgumentDescriptor> argumentsDescriptionMap;
	private String commandName;
	private List<ArgumentDefinition> parsedArguments;
	private Map<String, String[]> argumentNamesGroupMap;

	public ArgumentPattern(String commandName) {
		this.argumentsDescriptionMap = new HashMap<String, ArgumentDescriptor>();
		this.argumentNamesGroupMap = new HashMap<String, String[]>();
		this.commandName = commandName;
		this.parsedArguments = new ArrayList<ArgumentDefinition>();
	}

	public ArgumentPattern propertyArgument(String key) throws Exception {
		return argument(new String[]{key}, null, null, false,false);
	}
	public ArgumentPattern propertyArgument(String[] keys) throws Exception {
		return argument(keys, null, null, false,false);
	}
	
	public ArgumentPattern propertyArgument(String key,boolean optional) throws Exception {
		return argument(new String[]{key}, null, null, optional,false);
	}	
	public ArgumentPattern propertyArgument(String[] keys,boolean optional) throws Exception {
		return argument(keys, null, null, optional,false);
	}	
	
	public ArgumentPattern propertyArgument(String key,String defaultValue) throws Exception {
		return argument(new String[]{key}, null, defaultValue, true,false);
	}	
	
	public ArgumentPattern propertyArgument(String[] keys,String defaultValue) throws Exception {
		return argument(keys, null, defaultValue, true,false);
	}	
	
	public ArgumentPattern propertyArgument(String key,String defaultValue,String[] choices) throws Exception {
		return argument(new String[]{key}, choices, defaultValue, true,false);
	}
	
	public ArgumentPattern propertyArgument(String[] keys,String defaultValue,String[] choices) throws Exception {
		return argument(keys, choices, defaultValue, true,false);
	}

	public ArgumentPattern propertyArgument(String key,boolean optional,String[] choices) throws Exception {
		return argument(new String[]{key}, choices, null, optional,false);
	}	
	public ArgumentPattern propertyArgument(String[] keys,boolean optional,String[] choices) throws Exception {
		return argument(keys, choices, null, optional,false);
	}
	
	public ArgumentPattern booleanArgument(String key) throws Exception {
		return argument(new String[]{key}, null, false+"", true,true);
	}
	
	public ArgumentPattern booleanArgument(String[] keys) throws Exception {
		return argument(keys, null, false+"", true,true);
	}

	public ArgumentPattern parse(String argumentsLine) throws Exception {

		List<ArgumentDefinition> arguments = new ArrayList<ArgumentDefinition>();

		if (argumentsLine == null ) {
			throw new Exception("Unable to parse null string");
		}

		argumentsLine = argumentsLine.trim() ;
				
		StringBuffer token = new StringBuffer("");
		if(argumentsLine.equals(this.commandName)){
			argumentsLine = "";
		}else{
			
			if(argumentsLine.indexOf(this.commandName+" ") == 0){
				argumentsLine = argumentsLine.replaceFirst(this.commandName+"", "").trim();
			}
			
		}

		Stack<Character> stack = new Stack<Character>();
		for (int i = 0; i < argumentsLine.length(); i++) {
			char currentChar = argumentsLine.charAt(i);
			if ((i != 0 && argumentsLine.charAt(i - 1) != '\\')
					&& (currentChar == '\'' || currentChar == '"')) {
				if (!stack.empty() && stack.peek() == currentChar) {
					stack.pop();
				} else {
					stack.push(currentChar);
				}
			}

			if (!token.toString().equals("") && argumentsLine.charAt(i) == ' '
					&& stack.empty() && (i == argumentsLine.length()-1 || argumentsLine.charAt(i+1) == '-')) {

				if (!stack.empty()) {
					throw new Exception("Unbalanced quotations at "
							+ argumentsLine.substring(i - 1));
				}
				arguments.add(tokenToArgument(token.toString()));

				token = new StringBuffer("");
			} else  {
				if ((i != 0 && argumentsLine.charAt(i - 1) == '\\')
						&& (currentChar == '\'' || currentChar == '"')) {
					token = new StringBuffer(token.substring(0, token
							.toString().length() - 1));
				}

				token.append(argumentsLine.charAt(i));
			}
		}

		if (!token.toString().equals("")) {
			arguments.add(tokenToArgument(token.toString()));
		}

		validate(arguments);
		this.parsedArguments = arguments;
		
		return this;

	}

	public boolean getBoolean(String argName) throws Exception {
		
		ArgumentDescriptor argumentDescriptor = this.argumentsDescriptionMap
				.get(argName);
		
		if (argumentDescriptor == null) {
			throw new Exception("Unknown boolean argument " + argName);
		}
		
		for(String argumentName : argumentNamesGroupMap.get(argName)){
			for (ArgumentDefinition inputArgument : parsedArguments) {

				if (inputArgument.getName().equals(argumentName)) {
					if (inputArgument instanceof BooleanArgument) {
						return true;
					} else {
						throw new Exception("argument " + argumentName+ " is not boolean argument.");
					}

				}
			}
		}

		return Boolean.parseBoolean(argumentDescriptor.defaultValue);
	}

	public String getString(String argName) throws Exception {
				
		ArgumentDescriptor argumentDescriptor = this.argumentsDescriptionMap
				.get(argName);
		
		if (argumentDescriptor == null) {
			throw new Exception("Unknown string property argument " + argName);
		}
		
		for(String argumentName : argumentNamesGroupMap.get(argName)){
			for (ArgumentDefinition inputArgument : parsedArguments) {
				
				if (inputArgument.getName().equals(argumentName)) {
					if (inputArgument instanceof PropertyArgument) {
						return ((PropertyArgument) inputArgument).value;
					} else {
						throw new Exception("argument " + argumentName
								+ " is not string property argument.");
					}
				}
			}
		}

		return argumentDescriptor.defaultValue;
	}

	private ArgumentPattern argument(String[] possibleArgumentKeys, String[] choices,
			String defaultValue, boolean optional,boolean isBoolean) throws Exception {
		
		ArgumentDescriptor argumentDescriptor = new ArgumentDescriptor(choices,defaultValue, optional,isBoolean);
		
		for(String name : possibleArgumentKeys){
			if (this.argumentsDescriptionMap.containsKey(name)) {
				throw new Exception("Argument '" + name + "' is already defined");
			}
			this.argumentsDescriptionMap.put(name, argumentDescriptor);
			argumentNamesGroupMap.put(name,possibleArgumentKeys);
		}
		return this;
	}

	private ArgumentDefinition tokenToArgument(String token) throws Exception {
		token = token.trim();

		if (token.startsWith("--")) {
			return new BooleanArgument(token);
		} else if (token.startsWith("-")) {
	
			int index = 0;
			for(index=0;index<token.length();index++){
				if(token.charAt(index) == ' ' || token.charAt(index) == '='){
					break;
				}
			}
			
			index = index>=token.length()?token.length():index;
			String key = token.substring(0, index).trim();
			
			for(index=index+1;index<token.length();index++){
				if(token.charAt(index) != ' ' && token.charAt(index) != '='){
					index --;
					break;
				}
			}
			
			String value= null;
			
			if(index <= token.length()-1){
				value = token.substring(index + 1).trim();
			}

			return new PropertyArgument(key, value);
		} else {
			throw new Exception("Invalid token at "+token);
		}
	}

	public void validate(List<ArgumentDefinition> argumentsList) throws Exception {

		Set<String> foundArguments = new HashSet<String>();
		for (ArgumentDefinition argument : argumentsList) {

			ArgumentDescriptor argDescriptor = this.argumentsDescriptionMap.get(argument.getName());
			
			//Check if the argument has description
			if (argDescriptor == null) {
				throw new Exception("Undefined argument " + argument);
			}
			
			//check if the argument matches its description
			if(argDescriptor.isBoolean && !(argument instanceof BooleanArgument)){
				throw new Exception("Argument "+argument.getName()+" format should be as boolean (should start with '--')");
			}else if (!argDescriptor.isBoolean && !(argument instanceof PropertyArgument)){
				throw new Exception("Argument "+argument.getName()+" format should be as property (should start with '-')");
			}
			
			foundArguments.add(argument.getName());
			String[] choices = this.argumentsDescriptionMap.get(argument
					.getName()).choices;
	
			if(choices == null){
				choices = new String[]{};
			}
	
			String choicesExpected = "{";
			for(String choice : choices){
				if(choicesExpected.equals("{")){
					choicesExpected += "'"+choice+"'";
				}else{
					choicesExpected += ("|"+"'"+choice+"'");
				}
			}
			
			choicesExpected += "}";
					
			if (argument instanceof PropertyArgument && choices != null
					&& choices.length != 0) {
				String value = ((PropertyArgument) argument).value;
				boolean valid = false;

				for (String choice : choices) {
					if (value.equals(choice)) {
						valid = true;
						break;
					}
				}

				if (!valid) {
					throw new Exception("Value '"+value+"' is not valid for argument "
							+ argument.getName() + ", expected " + choicesExpected);
				}
			}
		}

		Set<String> checkedKeys = new HashSet<String>();
		for (String argumentName : argumentsDescriptionMap.keySet()) {
			
			if(checkedKeys.contains(argumentName) || argumentsDescriptionMap.get(argumentName).optional){
				continue;
			}
			
			String[] groupArgumentsKeys = argumentNamesGroupMap.get(argumentName);
			Set<String> currentKeys = new HashSet<String>();
			
			boolean found = false;
			
			for(String argKey : groupArgumentsKeys){
				if (foundArguments.contains(argKey)){
					found = true;
				}
			
				currentKeys.add(argKey);
			}
			
			if(!found){
				throw new Exception("Argument " + argumentName+ " is mandatory. Try to define it by "+currentKeys);
			}
			
			checkedKeys.addAll(currentKeys);
		
		}
	}

}
