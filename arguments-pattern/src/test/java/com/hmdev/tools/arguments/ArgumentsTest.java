package com.hmdev.tools.arguments;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ArgumentsTest {
	
	@Test
	public void MultiPropertiesTest() throws Exception{

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
		
		pattern.parse("command -choice c1 --b-b");
		Assert.assertEquals(pattern.getString("choice"), "c1");
		Assert.assertEquals(pattern.getBoolean("b-b"), true);
		Assert.assertEquals(pattern.getBoolean("b"), false);
		
		pattern.parse("command -choice c1 --b");
		Assert.assertEquals(pattern.getBoolean("b-b"), false);
		Assert.assertEquals(pattern.getBoolean("b"), true);
		
	}
	
	@Test
	public void defaultPropertyValueTest() throws Exception{

		ArgumentPattern pattern = new ArgumentPattern("command");
		pattern.booleanArgument("enable-something")
				.propertyArgument("assign-somevalue","default-value")
				.parse("command");
		
		String value = pattern.getString("assign-somevalue");
		
		Assert.assertEquals(value, "default-value");
	}
	
	@Test
	public void valueAssignTest() throws Exception{

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
	public void choiceTest() throws Exception{

		ArgumentPattern pattern = new ArgumentPattern("command");
		pattern.booleanArgument("enable-something")
				.propertyArgument("assign-somevalue", true,new String[] {"a","b"})
				.parse("command -assign-somevalue a");
		
		String value = pattern.getString("assign-somevalue");
		
		Assert.assertEquals(value, "a");
	}
	
	@Test
	public void invalidChoiceTest() throws Exception{

		String exMessage = null;
		try{
			ArgumentPattern pattern = new ArgumentPattern("command");
			pattern.booleanArgument("enable-something")
					.propertyArgument("assign-somevalue", true,new String[] {"a","b"})
					.parse("command -assign-somevalue c");
		}catch(Throwable e){
			exMessage = e.getMessage();
		}
		
		Assert.assertTrue(exMessage!=null && exMessage.contains("is not valid for argument"),"Expected to have wrong choice value for assign-somevalue");

	}
	
}
