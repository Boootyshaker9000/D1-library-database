package ui.statistics;

import dao.ActiveLoansDAO;
import dao.LibraryStatisticsDAO;
import exceptions.DbException;
import models.ActiveLoans;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel responsible for displaying library statistics and a dashboard of overdue loans.
 * Shows aggregate data like total books and readers, and a detailed list of active loans.
 */
public class StatisticsPanel extends JPanel {

    private final LibraryStatisticsDAO statisticsDAO = new LibraryStatisticsDAO();
    private final ActiveLoansDAO activeLoansDAO = new ActiveLoansDAO();

    private final JLabel totalBooksLabel;
    private final JLabel availableBooksLabel;
    private final JLabel totalReadersLabel;
    private final JLabel overdueLoansLabel;
    private final JLabel inventoryValueLabel;

    private final DefaultTableModel tableModel;

    /**
     * Constructs the StatisticsPanel and initializes the UI components.
     */
    public StatisticsPanel() {
        setLayout(new BorderLayout());

        JPanel statsContainer = new JPanel(new GridLayout(1, 5, 10, 10));
        statsContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        totalBooksLabel = createStatLabel();
        availableBooksLabel = createStatLabel();
        totalReadersLabel = createStatLabel();
        overdueLoansLabel = createStatLabel();
        overdueLoansLabel.setForeground(Color.RED);
        inventoryValueLabel = createStatLabel();

        statsContainer.add(createCard(totalBooksLabel, "Total Books"));
        statsContainer.add(createCard(availableBooksLabel, "Available"));
        statsContainer.add(createCard(totalReadersLabel, "Readers"));
        statsContainer.add(createCard(overdueLoansLabel, "Overdue!"));
        statsContainer.add(createCard(inventoryValueLabel, "Inventory Value"));

        add(statsContainer, BorderLayout.NORTH);

        String[] columnNames = {"Loan ID", "Book", "Reader", "Due Date", "Days Overdue"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Integer.class;
                return super.getColumnClass(columnIndex);
            }
        };

        JTable table = new JTable(tableModel);
        table.removeColumn(table.getColumnModel().getColumn(0));

        table.setDefaultRenderer(Object.class, new OverdueRowRenderer());

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(actionEvent -> refreshData());
        refreshButton.setFocusable(false);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshData();
    }

    /**
     * Creates a standardized label for statistical values.
     *
     * @return a styled JLabel
     */
    private JLabel createStatLabel() {
        JLabel label = new JLabel("...", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        return label;
    }

    /**
     * Creates a visual card containing a title and a value label.
     *
     * @param valueLabel the label displaying the statistic value
     * @param title the title of the statistic card
     * @return a JPanel representing the card
     */
    private JPanel createCard(JLabel valueLabel, String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    /**
     * Refreshes the statistics and the loan table data from the database.
     * Handles exceptions by displaying error messages in the UI.
     */
    private void refreshData() {
        try {
            statisticsDAO.getLibraryStatistics().ifPresent(stats -> {
                totalBooksLabel.setText(String.valueOf(stats.totalBooks()));
                availableBooksLabel.setText(String.valueOf(stats.availableBooks()));
                totalReadersLabel.setText(String.valueOf(stats.totalReaders()));
                overdueLoansLabel.setText(String.valueOf(stats.overdueLoans()));
                inventoryValueLabel.setText(stats.totalInventoryValue().toString());
            });
        } catch (DbException dbException) {
            totalBooksLabel.setText("Err");
        }

        tableModel.setRowCount(0);
        try {
            List<ActiveLoans> loans = activeLoansDAO.getActiveLoansDetails();
            for (ActiveLoans activeLoans : loans) {
                tableModel.addRow(new Object[]{
                        activeLoans.loanId(),
                        activeLoans.bookTitle(),
                        activeLoans.readerName(),
                        activeLoans.returnDate(),
                        activeLoans.daysOverdue()
                });
            }
        } catch (DbException dbException) {
            JOptionPane.showMessageDialog(this, "Error loading details: " + dbException.getMessage());
        }
    }

    /**
     * Custom table cell renderer that highlights overdue loans in red.
     */
    private static class OverdueRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            int daysOverdue = (int) table.getModel().getValueAt(table.convertRowIndexToModel(row), 4);

            if (daysOverdue > 0) {
                component.setForeground(Color.RED);
                component.setFont(component.getFont().deriveFont(Font.BOLD));
            } else {
                component.setForeground(Color.BLACK);
            }

            if (isSelected) {
                component.setForeground(table.getSelectionForeground());
                component.setBackground(table.getSelectionBackground());
            } else {
                component.setBackground(Color.WHITE);
            }

            return component;
        }
    }
}