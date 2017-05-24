
package com.hmdev.tools.arguments;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.hmdev.tools.arguments.exception.ArgumentPatternException;
import com.hmdev.tools.arguments.exception.ArgumentUndefinedException;
import com.hmdev.tools.arguments.exception.InvalidFormatException;

public class ArgumentsTest {
	
	@Test
	public void multiPropertiesTest() throws ArgumentPatternException{

		ArgumentPattern pattern = new ArgumentPattern("command");
		pattern.booleanArgument("b")
		.booleanArgument("b-b")
		.propertyArgument(new String[]{"p","p-p"},"default-value")
		.propertyArgument("choice","default-value",new String[]{"c1","c2","c3"});

		pattern.parse("command -p 10");
		Assert.assertEquals(pattern.getString("p-p"), "10");
		
		pattern.parse("command");
		Assert.assertEquals(pattern.getString("choice"), "default-value");
		Assert.assertEquals(pattern.getBoolean("b-b"), false);
		
		pattern.parse("command -choice c1 -p 10 20 --b-b");
		System.out.println(pattern.getString("p"));
		
		Assert.assertEquals(pattern.getString("choice"), "c1");
		Assert.assertEquals(pattern.getBoolean("b-b"), true);
		Assert.assertEquals(pattern.getBoolean("b"), false);
		
		pattern.parse("command -choice c1 --b");
		Assert.assertEquals(pattern.getBoolean("b-b"), false);
		Assert.assertEquals(pattern.getBoolean("b"), true);
		
	}
	
	@Test
	public void defaultPropertyValueTest() throws ArgumentPatternException{

		ArgumentPattern pattern = new ArgumentPattern("command");
		pattern.booleanArgument("enable-something")
				.propertyArgument("assign-somevalue","default-value")
				.parse("command");
		
		String value = pattern.getString("assign-somevalue");
		
		Assert.assertEquals(value, "default-value");
	}
	
	@Test
	public void valueAssignTest() throws ArgumentPatternException{

		ArgumentPattern pattern = new ArgumentPattern("command");
		pattern.booleanArgument("enable-something")
				.propertyArgument("assign-somevalue");
		
		pattern.parse("command -assign-somevalue new-value");
		Assert.assertEquals(pattern.getString("assign-somevalue"), "new-value");
		
		pattern.parse("command -assign-somevalue 'new-value'");
		Assert.assertEquals(pattern.getString("assign-somevalue"), "new-value");
		
		pattern.parse("command -assign-somevalue='new-value'");
		Assert.assertEquals(pattern.getString("assign-somevalue"), "new-value");
		
		pattern.parse("command -assign-somevalue=\"new-value & 'new-value'\" --enable-something");
		Assert.assertEquals(pattern.getString("assign-somevalue"), "new-value & 'new-value'");
		Assert.assertEquals(pattern.getBoolean("enable-something"), true);
	}
	
	@Test
	public void choiceTest() throws ArgumentPatternException{

		ArgumentPattern pattern = new ArgumentPattern("command");
		pattern.booleanArgument("enable-something")
				.propertyArgument("assign-somevalue",new String[] {"a","b"})
				.parse("command -assign-somevalue a");
		
		String value = pattern.getString("assign-somevalue");
		
		Assert.assertEquals(value, "a");
	}
	
	@Test
	public void optionalVsMandatoryParametersTest() throws ArgumentPatternException{

		ArgumentPattern pattern = new ArgumentPattern("command");
		pattern.propertyArgument("mandatory").propertyArgument("optional","default-value");
		
		pattern.parse("command -mandatory 10");
		Assert.assertEquals(pattern.getString("optional"), "default-value");
		Assert.assertEquals(pattern.getString("mandatory"), "10");
		
		pattern.parse("command -mandatory 10 -optional new-value");
		Assert.assertEquals(pattern.getString("optional"), "new-value");
		Assert.assertEquals(pattern.getString("mandatory"), "10");
		
		Class<?> exceptionClass = null;
		
		try{
			pattern.parse("command -optional new-value");
		}catch(ArgumentPatternException ex){
			exceptionClass = ex.getClass();
		}
		
		Assert.assertEquals(exceptionClass,ArgumentUndefinedException.class,"expected exception: com.hmdev.tools.arguments.exception.ArgumentUndefinedException");
	}
	
	@Test
	public void invalidChoiceTest() throws ArgumentPatternException{

		String exMessage = null;
		Class<? extends ArgumentPatternException> exClass = null;
		try{
			ArgumentPattern pattern = new ArgumentPattern("command");
			pattern.booleanArgument("enable-something")
					.propertyArgument("assign-somevalue",new String[] {"a","b"})
					.parse("command -assign-somevalue c");
		}catch(ArgumentPatternException e){
			exMessage = e.getMessage();
			exClass = e.getClass();
		}

		Assert.assertTrue(exClass == InvalidFormatException.class && exMessage.contains("is not valid for argument"),"Expected to have wrong choice value for assign-somevalue (expected exception: com.hmdev.tools.arguments.exception.InvalidFormatException)");

	}
	
	@Test
	public void quotedValueTest() throws ArgumentPatternException{

		ArgumentPattern pattern = new ArgumentPattern("execute").propertyArgument("command");
		pattern.parse("execute -command 'echo -b \"some test\"'");
		
		Assert.assertEquals(pattern.getString("command"), "echo -b \"some test\"");
		
	}
}
