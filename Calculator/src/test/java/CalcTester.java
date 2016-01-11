import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.marist.model.CalcLexer;
import org.marist.model.Token;
import org.marist.model.TokenType;


public class CalcTester {
	CalcLexer calculator = new CalcLexer();
	//String input = "add(1, mult(2, 3)) test";
	String input = "add(2147483648, mult(2,3))";
	ArrayList<Token> inputtokens = calculator.lex(input);
	
	@Test
	public void testBadInput() {
		if(inputtokens.isEmpty()) {
			fail("We had some kind of bad or invalid input string");	
		}
	}
	
	@Test
	public void testInputNumbers() {
		for (Token token : inputtokens) {
			if(token.type == TokenType.NUMBER) {
				if(calculator.validate_int_Range(token.data) == false) {
					fail("Some of the inputted numbers are not in range");			
				}
			}
		}
	}	
}
