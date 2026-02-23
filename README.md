This is Budget Excel, a cheap table editor with formular support.

It supports basic mathematical operations (+, -, *, /, %, !), but you can also reference other table cells (e.g., A1 + B1) and do a few named functions, such as pow(-2, 3) or gcd(3, 5) (for power calculation and calculating the greatest common divisor respectively)

For formular support, I've written a recusive descent parser.
Grammar
------
program -> expr
expr    -> term { ('+' | '-') term }
term    -> factor { ('*' | '/' | '%') factor }
factor  -> ( NUMBER | '(' expr ')' | ENTRY | '-' factor | func ) { '!' }
func    -> ('pow' | 'gcd') '(' expr ',' expr ')'

NUMBER  -> [ Digit { Digit } ] '.' Digit { Digit } | Digit { Digit }
ENTRY   -> Letter { Letter } NonZeroDigit { Digit }
NonZeroDigit -> '1' | '2' | ... | '9'

Here is how the formular support works:
------
1) select any cell
2) write '=', so the program knows you want to write a formular
3) write your expression, e.g. pow(A1, 2) * 32.4 + 3
