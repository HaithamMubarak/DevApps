# Arguments Pattern
Java api used to parse command line arguments


# Arguments Defintions

You can define your arguments format using ArgumentPattern class, with either property argument which starts wtih '-'  and parsed as key&value format or boolean argument starts with '--' which has true/false value.

For property argument you can restrict choices for the value and make the argument optional with some default value, but boolean argument is always optilnal with default boolean value of 'false'

## Samples

1- Create your argument pattern:

``` java
ArgumentPattern pattern = new ArgumentPattern("command");//command is the process name, parsed line should start with 'command'
                                                         // or start wtih arguments directly, examples of valid lines:
                                                         // command --flag -p 10
                                                         // --flag -p 10
                                                         // but this one is invalid : echo --flag
                                                         //(either ommited or starts with assigned name)
                                                         
pattern.booleanArgument("flag")                                          //defines new boolean argument flag
.propertyArgument(new String[]{"p","property-alias"})                    //defines new property argument p with alias property-alias
.propertyArgument("choice","default-value",new String[]{"c1","c2","c3"});//defines property argument with restrcited choices [c1|c2|c3]
                                                                         //default value
 ```
2- Parse command line parameters : 
 
 ``` java

//Valid lines
pattern.parse("command --flag -p 10"); // or
pattern.parse("--flag -p 10");
pattern.parse("command --flag -p='10'");

//Invalid lines
pattern.parse("--flag");// throws exception, property 'p' is mandatory since it has no default value
pattern.parse("-p 10 -choice c4");//throws exception, since c4 value is not in  [c1|c2|c3]

 ```
 
 3- Get parameters value:
  ``` java
  boolean flag = pattern.getBoolean("flag");
  
  // p and alias have the same value, they have the same reference.
  String p = pattern.getString("p");
  String alias = pattern.getString("property-alias");
  ```
    
  ## Future Enhancements
  
  <ul>
  <li>Adding parameters help support</li>
  <li>Adding dependent parameters support<li>
  </ul>
  
  Thank You!
  
 
 
