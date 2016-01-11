package org.marist.model;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class CalcLexer {
	private static Logger LOGGER = Logger.getLogger(CalcLexer.class);
	
	//operation behaviors defined using the generic "operation" interface defined within MathOperation and lambda functions
	//for consistency, they are given the same names as specified under TokenType.java
	MathOperation add = (a, b) -> a + b;
	MathOperation multiply = (a, b) -> a * b;
	MathOperation divide = (a, b) -> a / b;
	MathOperation subtract = (a, b) -> a - b;
	
	//what if we had 2 stacks: one with all the operations, one with all the numbers
	//this way, the first 2 elements of the numbers stack will be combined with the first item from the operations stack
	//then that result will be stored somewhere and so on till both stacks are empty ?
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		
	    String input = "add(1, mult(2, 3))";
	    
	    //place to store all the valid tokens
	    Stack arithmeticStack = new Stack(); 
	    //place to process output
	    Queue<String> outputQueue = new PriorityQueue<String>();
	    
	    //tokenize the input and process accordingly: bad tokens will be caught during lexing and the program will stop running/log errors
	    ArrayList<Token> inputtokens = lex(input);
	    
	    if (inputtokens.isEmpty()) {
	    	LOGGER.info("Main method: There is no input to process. There must have been a problem. Please see if there are any error logs.");
	    }
	    else {
	    	LOGGER.debug("Main method: Printing out contents of inputtokens to ensure input is tokenized correctly and associated with the right types.");
		    for (Token token : inputtokens) {	    	
		        System.out.println(token);
		    }
	    	
	    	LOGGER.info("Main method: Seems like input is valid. Let's process it ...");
	    	
	    	for (Token token : inputtokens) {
		    	//even though input is valid, let's make sure that numbers are within range
	    		if(token.type == TokenType.NUMBER) {
	    			if(validate_int_Range(token.data) == false) {
		    			LOGGER.error("Main method: We have number values in the input that are not in the proper range. Numerical inputs must be between " + Integer.MIN_VALUE + " and " + Integer.MAX_VALUE);
		    			LOGGER.info("Main method: Input contents will be cleared. Program cannot proceed due to this.");
		    			inputtokens.clear();
		    		}
	    			else {
	    				//put number onto the operation stack
	    				arithmeticStack.push(token.data);
	    			}
		    	}
	    		else {
	    			//since we're cleaning out invalid types in the lex method and erroring out if there are any invalid types:
	    			//so if TokenType is not a number, at this point we know that it can only be a valid operation name
	    			//put operation name onto the operation stack
	    			arithmeticStack.push(token.data);
	    		}
	    	}	
	    }
	    
	    if(arithmeticStack.isEmpty()) {
	    	LOGGER.info("Operation stack is empty. Something could have gone wrong.");
	    }
	    else {
	    	/*LOGGER.debug("Main method: Printing out contents of operationStack just to view contents.");
	    	while(!arithmeticStack.isEmpty()) {
		    	System.out.println(arithmeticStack.pop());
		    }*/
	    	
	    	LOGGER.info("Main method: Seems like our operation stack has been populated. Let's process it ...");
	    	
	    	/*
	    	 * Idea here is as follows
	    	 * 1. Pop numbers from the arithmeticStack into outputQueue until you hit an operation
	    	 * 2. Once you hit an operation, perform the operation on the numbers present in the queue and clear queue
	    	 * 2.a Replace queue elements with result of operation in step 2 above
	    	 * 3. Repeat process of popping numbers from arithmeticStack into outputQueue until you hit an operation and perform operation
	    	 * as outlined in steps 2 and 2.a
	    	 * FINALLY: the outputQueue will only contain the final result that needs to be printed 
	    	 */
	    	int finalResult = 0;
	    	while(!arithmeticStack.isEmpty()) {
	    		if (isInteger(arithmeticStack.pop().toString()) == true) {
	    			outputQueue.add((String) arithmeticStack.pop());
	    		}
	    		else {
	    			//we've hit an operation: perform the operation on the elements in the queue
	    			java.util.Iterator<String> outputQueueIterator = outputQueue.iterator();
	    			while(outputQueueIterator.hasNext()) {
	    				String number = outputQueueIterator.next();
	    				
	    				LOGGER.debug("Main method: queue output " + number);
	    			}
	    		}
	    	}
	    }
	  }
	
	public static ArrayList<Token> lex(String input) {
	    // The tokens to return
	    ArrayList<Token> tokens = new ArrayList<Token>();

	    // Lexer logic begins here
	    StringBuffer tokenPatternsBuffer = new StringBuffer();
	    for (TokenType tokenType : TokenType.values())
	      tokenPatternsBuffer.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.pattern));
	    Pattern tokenPatterns = Pattern.compile(new String(tokenPatternsBuffer.substring(1)));

	    // Begin matching tokens
	    Matcher matcher = tokenPatterns.matcher(input);
	    while (matcher.find()) {
	      if (matcher.group(TokenType.NUMBER.name()) != null) {
	    	tokens.add(new Token(TokenType.NUMBER, matcher.group(TokenType.NUMBER.name())));
	        continue;
	      } else if (matcher.group(TokenType.ADD.name()) != null) {
	        tokens.add(new Token(TokenType.ADD, matcher.group(TokenType.ADD.name())));
	        continue;
	      } else if (matcher.group(TokenType.SUBTRACT.name()) != null) {
	        tokens.add(new Token(TokenType.SUBTRACT, matcher.group(TokenType.SUBTRACT.name())));
	        continue;
		  } else if (matcher.group(TokenType.DIVIDE.name()) != null) {
	        tokens.add(new Token(TokenType.DIVIDE, matcher.group(TokenType.DIVIDE.name())));
	        continue;
		  } else if (matcher.group(TokenType.MULTIPLY.name()) != null) {
	        tokens.add(new Token(TokenType.MULTIPLY, matcher.group(TokenType.MULTIPLY.name())));
	        continue;
		  } else if (matcher.group(TokenType.WHITESPACE.name()) != null)
	        continue;
		  else if (matcher.group(TokenType.OTHERCHARS.name()) != null) {
			  //the first OTHERCHARS value that we find, exit the loop and stop adding to tokens ArrayList
			  //log this as an error and clear the contents of the tokens array list
			  LOGGER.error("Lex method: Input contains invalid expressions. Program cannot proceed due to this.");
			  tokens.clear();
			  break;
		  }
	    }
	    
	    return tokens;
	  }
	
	public static boolean validate_int_Range(String data) {
		boolean valid = false;
		
		try {
			int number = Integer.parseInt(data);
			
			if((Integer.MIN_VALUE <= number) && (number <= Integer.MAX_VALUE)) {
				valid = true;
			}
		}
		catch(java.lang.NumberFormatException ex) {
			LOGGER.error("validate_ints Method: Exception caught while converting " + data + " to integer.");
		}
		
		return valid;
	}

	public static boolean isInteger(String toConvert) {
		try {
			Integer.parseInt(toConvert);
			return true;
		} catch(NumberFormatException e) {
			LOGGER.error("isInteger Method: Exception caught while converting " + toConvert + " to integer.");
			return false;			
		}
	}
	
	private static int performMathOperation(int a, int b, MathOperation arithmeticOperation) {
		LOGGER.debug("first value: " + a);
		LOGGER.debug("second value: " + b);
		LOGGER.debug("operation: " + arithmeticOperation);
		return arithmeticOperation.operation(a, b);		
	}
}
