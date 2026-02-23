# Budget Excel

This is Budget Excel, a lightweight spreadsheet editor with full formula support. 

It supports basic mathematical operations (`+`, `-`, `*`, `/`, `%`, `!`), but you can also reference other table cells (e.g., `=A1+B1`) and use a few named functions, such as `pow(-2, 3)` or `gcd(3, 5)` (for calculating powers and the greatest common divisor, respectively).

For the formula support, I implemented a custom **Recursive Descent Parser**.

## How to use formulas
1. Select any cell in the table.
2. Type `=`, so the program knows you want to evaluate a formula.
3. Write your expression. Example: `=pow(A1, 2) * 32.4 + 3`
4. Press Enter to calculate the result.

## Grammar Rules
Here is the exact grammar my parser uses to evaluate the mathematical expressions:

```text
program -> expr
expr    -> term { ('+' | '-') term }
term    -> factor { ('*' | '/' | '%') factor }
factor  -> ( NUMBER | '(' expr ')' | ENTRY | '-' factor | func ) { '!' }
func    -> ('pow' | 'gcd') '(' expr ',' expr ')'
NUMBER  -> [ Digit { Digit } ] '.' Digit { Digit } | Digit { Digit }
ENTRY   -> Letter { Letter } NonZeroDigit { Digit }

NonZeroDigit -> '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
