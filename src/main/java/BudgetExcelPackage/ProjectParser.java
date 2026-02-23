package BudgetExcelPackage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectParser {
    private Lexer lexer;
    private Token currentToken;
    private Object[][] data;

    public ProjectParser(String text, Object[][] data){
        if(text == null){
            System.out.println("Text is empty, you can't initialize a lexer with an empty text");
        } else {
            lexer = new Lexer(text);
        }
        this.data = data;
    }

    public ProjectParser(Object[][] data){
        this.data = data;
    }

    public String parse(){
        if(lexer == null){
            System.out.println("BudgetExcelPackage.Lexer not set");
            return null;
        }
        currentToken = lexer.getNextToken();
        double result = expr();

        if (currentToken.getType() != TokenType.EOF) {
            error();
        }

        if(isInteger(result)){
            return "" + Math.round(result);
        }
        return "" + result;
    }

    private double func(){
        boolean function1;
        if(currentToken.getType() == TokenType.POW){
            eat(TokenType.POW);
            function1 = true;
        } else {
            eat(TokenType.GCD);
            function1 = false;
        }
        if(currentToken.getType() != TokenType.LPAREN){
            error();
        }
        eat(TokenType.LPAREN);
        double expr1 = expr();
        if(currentToken.getType() != TokenType.COMMA){
            error();
        }
        eat(TokenType.COMMA);
        double expr2 = expr();
        if(currentToken.getType() != TokenType.RPAREN){
            error();
        }
        eat(TokenType.RPAREN);
        if(function1){
            return Math.pow(expr1, expr2);
        }
        return gcd(expr1, expr2);
    }

    private double factor(){
        Token token = currentToken;
        double result = 0;
        switch(currentToken.getType()) {
            case NUMBER:
                eat(TokenType.NUMBER);
                result = Double.parseDouble(token.getValue());
                break;
            case LPAREN:
                eat(TokenType.LPAREN);
                result = expr();
                eat(TokenType.RPAREN);
                break;
            case ENTRY:
                String entry = token.getValue();
                Pattern pattern = Pattern.compile("([A-Z]+)(\\d+)");
                Matcher matcher = pattern.matcher(entry);
                if (matcher.matches()) {
                    int column = getColumnIndex(matcher.group(1));
                    int row = Integer.parseInt(matcher.group(2)) - 1;
                    eat(TokenType.ENTRY);

                    if (row < 0 || row >= data.length || column < 0 || column >= data[0].length) {
                        throw new RuntimeException("Out of bounds: Cell " + entry + " does not exist.");
                    }
                    try {
                        result = Double.parseDouble("" + data[row][column]);
                    } catch (NumberFormatException e) {
                        error4();
                    }
                } else {
                    error();
                }
                break;
            case MINUS:
                eat(TokenType.MINUS);
                result = -factor();
                break;
            case POW:
            case GCD:
                result = func();
                break;
            default:
                error();
        }
        while(currentToken.getType() == TokenType.FACTORIAL){
            eat(TokenType.FACTORIAL);
            if(result < 0){
                error2();
            }
            result = factorial(result);
        }

        return result;
    }

    private double term(){
        double result = factor();
        while(currentToken.getType() == TokenType.MULTIPLY || currentToken.getType() == TokenType.DIVIDE || currentToken.getType() == TokenType.MODULO){
            if(currentToken.getType() == TokenType.MULTIPLY){
                eat(TokenType.MULTIPLY);
                result *= factor();
            } else if(currentToken.getType() == TokenType.DIVIDE){
                eat(TokenType.DIVIDE);
                double check = factor();
                if(Math.abs(check) < 1e-6){
                    error3();
                }
                result /= check;
            } else if (currentToken.getType() == TokenType.MODULO){
                eat(TokenType.MODULO);
                result = modulo(result, factor());
            }
        }
        return result;
    }

    private double expr(){
        double result = term();
        while(currentToken.getType() == TokenType.PLUS || currentToken.getType() == TokenType.MINUS){
            if(currentToken.getType() == TokenType.PLUS){
                eat(TokenType.PLUS);
                result += term();
            } else if(currentToken.getType() == TokenType.MINUS){
                eat(TokenType.MINUS);
                result -= term();
            }
        }
        return result;
    }

    private void eat(TokenType tokenType){
        if(currentToken.getType() == tokenType){
            currentToken = lexer.getNextToken();
        } else {
            error();
        }
    }

    public Lexer getLexer() {
        return lexer;
    }

    public Token getCurrentToken() {
        return currentToken;
    }

    public Object[][] getData() {
        return data;
    }

    public void setData(Object[][] data) {
        this.data = data;
    }

    public void setLexer(Lexer lexer) {
        if(lexer == null){
            this.lexer = null;
        } else {
            this.lexer = lexer;
        }
        currentToken = null;
    }

    public void setLexer(String text) {
        if(text == null){
            System.out.println("Text is empty, you can't initialize a lexer with an empty text");
        } else {
            lexer = new Lexer(text);
            currentToken = null;
        }
    }

    private void error(){
        throw new RuntimeException("Invalid syntax");
    }
    private void error2(){
        throw new ArithmeticException("The factorial is only defined for natural numbers and 0 (but you can look up the gamma function if you want to)");
    }
    private void error3(){
        throw new ArithmeticException("You can't divide by zero (generally, we don't define 0/0 here). It is also possible that you tried to divide by a number, for which |number| < 1e-6. Because of inaccuracies when calculating with floating point numbers, I decided to use 1e-6 as a threshold for inaccuracies, so a number like 0.000000000000000000001 is regarded as 0.0.");
    }
    private void error4(){
        throw new RuntimeException("In this parser version you can only calculate with real numbers in your cells. Yours probably was empty or had a String in it or something.");
    }
    private void error5(){
        throw new ArithmeticException("This version of the gcd algorithm only supports whole numbers, not real numbers.");
    }
    private void error6(){
        throw new ArithmeticException("The modulo operand is only defined for whole numbers, yours probably was a real or rational number (note that we have a tolerance of 1e-6, because of floating point inaccuracies). There is also a possibility that you tried to calculate something modulo 0, which isn't doable.");
    }

    private int getColumnIndex(String col){
        int index = 0;
        for(int i = 0; i < col.length(); i++){
            char c = col.charAt(i);
            index = index * 26 + (c - 'A' + 1);
        }
        return index - 1;
    }

    private double factorial(double number){
        long result = 1;
        if(!isInteger(number)){
            error2();
        }
        long n = Math.round(number);
        while(n > 0){
            result *= n;
            n--;
        }
        return result;
    }

    private double gcd(double a, double b){
        if(!isInteger(a) || !isInteger(b)){
            error5();
        }
        long a1 = Math.round(a);
        long b1 = Math.round(b);
        if(b1 == 0) return a1;
        return gcd(b1, a1 % b1);
    }

    private double modulo(double a, double b){
        if(!isInteger(a) || !isInteger(b) || Math.abs(b) < 1e-6){
            error6();
        }
        double a1 = Math.round(a);
        double b1 = Math.round(b);
        return (a1 - Math.floor(a1 / b1) * b1);
    }

    private boolean isInteger(double num) {
        double threshold = 1e-6;
        return Math.abs(num - Math.round(num)) < threshold;
    }
    //To account for floating point inaccuracies
}
