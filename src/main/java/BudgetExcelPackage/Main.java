package BudgetExcelPackage;

import java.util.InputMismatchException;
import java.util.Scanner;

import static BudgetExcelPackage.CheapTable.createAndShowGUI;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("How many rows do you want to have? Type in any natural number (1, 2, 3, 4,...)");
        int rows = 0;
        while (rows <= 0) {
            try {
                rows = sc.nextInt();
                sc.nextLine();
                if(rows<=0){
                    System.out.println("This wasn't a natural number");
                }
            } catch (InputMismatchException e) {
                System.out.println("InputMismatchException");
                System.out.println("This definitely wasn't a natural number");
                sc.nextLine();
            }
        }

        System.out.println("How many columns do you want to have? Type in any natural number (1, 2, 3, 4,...)");
        int columns = 0;
        while (columns <= 0) {
            try {
                columns = sc.nextInt();
                sc.nextLine();
                if(columns<=0){
                    System.out.println("This wasn't a natural number");
                }
            } catch (InputMismatchException e) {
                System.out.println("InputMismatchException");
                System.out.println("This definitely wasn't a natural number");
                sc.nextLine();
            }
        }
        sc.close();

        int finalRows = rows;
        int finalColumns = columns;
        javax.swing.SwingUtilities.invokeLater(() -> CheapTable.createAndShowGUI(finalRows, finalColumns));
    }
}