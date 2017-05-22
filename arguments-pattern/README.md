# Arguments Pattern
Java api used to parase command line arguments


# Arguments Defintions

You can define your arguments format using ArgumentPattern class, with either property argument which starts wtih '-'  and parsed as key&value format or boolean argument starts with '--' which has true/false value.

For property argument you can restrict choices for the value and make the argument optional with some default value, but boolean argument is always optilnal with default boolean value of 'false'

# Sample Code

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
    
 

