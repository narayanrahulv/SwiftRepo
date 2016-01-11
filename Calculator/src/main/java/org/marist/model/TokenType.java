package org.marist.model;

public enum TokenType {
	//idea is to create 'token types' for each piece of input passed in from the command string
	//token types are created for the EXPECTED INPUT (like the expressions as well as numbers)
	//whitespaces are ignored so in theory, add(1, 2) add( 1,2) or add(1,2 ) should be fine
	//token type is created for 'other' to denote NON-EXPECTED INPUT that are random characters or wildcard characters
	//if 'other' is flagged as part of the input tokens, then we know that something is wrong and to not proceed with operations
	NUMBER("-?[0-9]+")
	, MULTIPLY("(mult)")
	, DIVIDE("(div)")
	, ADD("(add)")
	, SUBTRACT("(sub)")
	, WHITESPACE("[ \t\f\r\n]+")
	, OTHERCHARS("[^multMULTdivDIVaddADDsubSUB0-9,()]");	
	
	public final String pattern;

    private TokenType(String pattern) {
      this.pattern = pattern;
    }
}
