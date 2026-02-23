package BudgetExcelPackage;

import javax.swing.*;
import java.awt.*;

public class CheapTable extends JPanel {
    private final ProjectParser parser;

    /**
     * This constructor is responsible for creating the table with our pre-set height and width.
     * This is just a basic JTable, but I added row header for better readability.
     *
     * @param height number of rows
     * @param width number of columns
     */
    public CheapTable(int height, int width){
        super(new GridLayout(1,0));

        String[] columnNames = new String[width];
        for(int i = 0; i < width; i++){
            columnNames[i] = getColumnName(i);
        }

        String[] rowNames = new String[height];
        for(int i = 0; i < height; i++){
            rowNames[i] = "" + (i + 1);
        }

        Object[][] data = new Object[height][width];
        parser = new ProjectParser(data);

        final JTable table = new JTable(data, columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(800, 450));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JList<String> rowHeader = new JList<>(rowNames);
        rowHeader.setFixedCellHeight(table.getRowHeight());
        rowHeader.setBackground(Color.lightGray);
        rowHeader.setBorder(BorderFactory.createLineBorder(Color.black));
        scrollPane.setRowHeaderView(rowHeader);


        /*
        This is the interface for the BudgetExcelPackage.ProjectParser.
        The TableModelListener gets notified whenever you change something in a cell.
        If your change starts with "=", this program tries to parse the text right next to the character.
        If your String isn't parsable (wrong syntax, wrong characters,...), our program does nothing and nothing changes.
        */
        table.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            Object value = table.getValueAt(row, col);

            if(value != null){
                if(value.toString().startsWith("=")){
                    String formula = value.toString().substring(1);
                    if(!formula.isEmpty()){
                        parser.setLexer(formula);
                        try {
                            String result = parser.parse();
                            table.setValueAt(result, row, col);
                        } catch (RuntimeException ex){
                            System.out.println(ex.getMessage());
                            table.setValueAt("#ERROR!", row, col);
                        }
                    }
                }
            }

        });
        add(scrollPane);

    }

    /**
     * This method is responsible for naming all the columns.
     * The resulting format: A, B, C,..., Y, Z, AA, AB,..., ZY, ZZ, AAA,...
     *
     * @param col number of columns
     * @return name for corresponding column
     */
    private String getColumnName(int col){
        StringBuilder result = new StringBuilder();
        col++;
        while (col > 0){
            int remainder = (col - 1) % 26;
            result.insert(0, ((char) ('A' + remainder)));
            col = (col - 1) / 26;
        }
        return result.toString();
    }

    /**
     * This is just the basic method for creating and showing the table.
     * I got this from the oracle table tutorial, I didn't really change much here:
     * <a href="https://docs.oracle.com/javase/tutorial/uiswing/components/table.html#data">...</a>
     * <p>
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static void createAndShowGUI(int height, int width) {
        JFrame frame = new JFrame("Budget Excel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CheapTable newContentPane = new CheapTable(height, width);
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);

        frame.pack();
        frame.setVisible(true);
    }

}
