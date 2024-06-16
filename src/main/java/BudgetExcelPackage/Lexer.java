package BudgetExcelPackage;

public class Lexer {
    private String text;
    private int pos;
    private char currentChar;

    public Lexer(String text) {
        setNewText(text);
    }

    private void advance() {
        pos++;
        if(pos >= text.length()){
            currentChar = '\0';
        } else {
            currentChar = text.charAt(pos);
        }
    }

    private String number(){
        StringBuilder result = new StringBuilder();
        while(Character.isDigit(currentChar)){
            result.append(currentChar);
            advance();
        }
        if(currentChar != '.'){
            return (result.toString() + ".0");
        }
        if(result.isEmpty()){
            result.append('0');
        }
        do {
            result.append(currentChar);
            advance();
        } while (Character.isDigit(currentChar));

        if(result.charAt(result.length() - 1) == '.'){
            result.append('0');
        }
        return result.toString();
    }

    private String entry(){
        StringBuilder result = new StringBuilder();
        while(currentChar >= 'A' && currentChar <= 'Z'){
            result.append(currentChar);
            advance();
        }
        if(!Character.isDigit(currentChar) || currentChar == '0'){
            error();
        }
        while(Character.isDigit(currentChar)){
            result.append(currentChar);
            advance();
        }
        return result.toString();
    }

    private Token functionToken(){
        StringBuilder result = new StringBuilder();
        while(currentChar >= 'a' && currentChar <= 'z') {
            result.append(currentChar);
            advance();
        }
        if(result.toString().equals("pow")){
            return new Token(TokenType.POW, "pow");
        }
        if(result.toString().equals("gcd")){
            return new Token(TokenType.GCD, "gcd");
        }
        error();
        return null; //This should never be reached
    }

    public Token getNextToken(){
        while(currentChar != '\0'){
            if(Character.isWhitespace(currentChar)){
                advance();
            } else if(Character.isDigit(currentChar) || currentChar == '.'){
                return new Token(TokenType.NUMBER, number());
            } else if(currentChar >= 'A' && currentChar <= 'Z') {
                return new Token(TokenType.ENTRY, entry());
            } else if(currentChar >= 'a' && currentChar <= 'z'){
                return functionToken();
            } else {
                switch(currentChar){
                    case '+':
                        advance();
                        return new Token(TokenType.PLUS, "+");
                    case '-':
                        advance();
                        return new Token(TokenType.MINUS, "-");
                    case '*':
                        advance();
                        return new Token(TokenType.MULTIPLY, "*");
                    case '/':
                        advance();
                        return new Token(TokenType.DIVIDE, "/");
                    case '(':
                        advance();
                        return new Token(TokenType.LPAREN, "(");
                    case ')':
                        advance();
                        return new Token(TokenType.RPAREN, ")");
                    case '!':
                        advance();
                        return new Token(TokenType.FACTORIAL, "!");
                    case ',':
                        advance();
                        return new Token(TokenType.COMMA, ",");
                    case '%':
                        advance();
                        return new Token(TokenType.MODULO, "%");
                    default:
                        error();
                }
            }
        }
        return new Token(TokenType.EOF, null);
    }

    private void error(){
        throw new RuntimeException("Invalid character");
    }
    public void setNewText(String input){
        if(input.isEmpty()){
            throw new RuntimeException("Empty input");
        }
        this.text = input;
        pos = 0;
        currentChar = text.charAt(0);
    }

    public String getText() {
        return text;
    }

    public int getPos() {
        return pos;
    }

    public char getCurrentChar() {
        return currentChar;
    }
}
