package Project;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * This class holds all the functionality and information for the transactions.
*/
public class Transaction {

    // Constants //

    private static final String INSERT_TRANSACTION = "INSERT INTO transactions (BuyOrderId, SellOrderId, TransactionTimeStamp) VALUES (?, ?, ?);";
    private static final String GET_TRANSACTION = "SELECT * FROM transactions WHERE TransactionID=?";
    private static final String GET_TRANSACTION_BOOL = "SELECT * FROM transactions WHERE SellOrderID=? OR BuyOrderID=?";
    private static final String GET_ALL_TRANSACTIONS = "SELECT * FROM transactions \n";

    // Variables //

    int transactionID;
    int buyOrderID;
    int sellOrderID;
    LocalDateTime transactionTimestamp;

    // Database connection
    private static final Connection connection = DBConnection.getConn();


    // Methods //

    /**
     * Empty order constructor for the GUI.
     */
    public Transaction() {
        // Empty constructor
    }

    /**
     * Transaction Constructor without ID, database creates the ID.
     *
     * @param buyOrderID The ID of the buy order of the transaction.
     * @param sellOrderID The ID of the sell order of the transaction.
     */
    public Transaction(int buyOrderID, int sellOrderID) {
        this.buyOrderID = buyOrderID;
        this.sellOrderID = sellOrderID;
        this.transactionTimestamp = java.time.LocalDateTime.now();
    }

    /**
     * Transaction Constructor with ID.
     *
     * @param transactionID The ID of the transaction.
     * @param buyOrderID The ID of the buy order of the transaction.
     * @param sellOrderID The ID of the sell order of the transaction.
     */
    public Transaction(int transactionID, int buyOrderID, int sellOrderID) {
        this.transactionID = transactionID;
        this.buyOrderID = buyOrderID;
        this.sellOrderID = sellOrderID;
        this.transactionTimestamp = java.time.LocalDateTime.now();
    }

    /**
     * Adds a created transaction to the database.
     *
     * @param myTransaction The transaction to add.
     * @return True if the transaction was successfully added, false otherwise.
     */
    public static Boolean addTransaction(Transaction myTransaction) {
        // Variables
        PreparedStatement addTransaction;

        // Try to add transaction to the database
        try {
            addTransaction = connection.prepareStatement(INSERT_TRANSACTION);

            addTransaction.setString(1, Integer.toString(myTransaction.buyOrderID));
            addTransaction.setString(2, Integer.toString(myTransaction.sellOrderID));
            addTransaction.setString(3, String.valueOf(myTransaction.transactionTimestamp));
            addTransaction.executeUpdate();

            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Searches for a transaction in the database using the transaction ID and retrieves it.
     *
     * @param transactionID The ID of the transaction to find.
     * @return The transaction if it exists, otherwise returns null.
     */
    public static Transaction getTransaction(int transactionID) {
        // Variables
        PreparedStatement getTransaction;
        final ResultSet result;

        // Try to retrieve the asset
        try {
            getTransaction = connection.prepareStatement(GET_TRANSACTION);

            getTransaction.setString(1, Integer.toString(transactionID));

            result = getTransaction.executeQuery();
            if (!result.next()) {
                // No transaction matched this ID
                return null;
            }

            return new Transaction(
                    result.getInt("BuyOrderID"),
                    result.getInt("SellOrderID")
            );

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Searches for any ongoing transactions on an order in the database using the order ID to see if it exists.
     *
     * @param orderID The ID of the order to check for transactions.
     * @return True if the transactions exist, false otherwise. If the checking fails, it returns null.
     */
    public static Boolean transactionExists(int orderID) {
        // Variables
        PreparedStatement getTransaction;
        final ResultSet result;

        // Check if transaction exists
        try {
            getTransaction = connection.prepareStatement(GET_TRANSACTION_BOOL);

            getTransaction.setString(1, Integer.toString(orderID));
            getTransaction.setString(2, Integer.toString(orderID));

            result = getTransaction.executeQuery();

            // Return if there exists any transactions
            return result.next();

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Used to create table model for transactions in the GUI - lists all current transactions.
     *
     * @return A DefaultTableModel if the transactions could be found.
     * @throws SQLException when something goes wrong with the database.
     */
    public DefaultTableModel getAllTransactions() throws SQLException {
        ResultSet rs;
        PreparedStatement getAllTransactions;
        getAllTransactions = connection.prepareStatement(GET_ALL_TRANSACTIONS);
        DefaultTableModel defaultTableModel = new DefaultTableModel(){@Override
        public boolean isCellEditable(int row, int column) {
            //all cells false
            return false;
        }};
        defaultTableModel.addColumn("Transaction ID:");
        defaultTableModel.addColumn("Sell Order ID");
        defaultTableModel.addColumn("Buy Order ID");
        defaultTableModel.addColumn("Timestamp");

        rs= getAllTransactions.executeQuery();
        while(rs.next()){
            defaultTableModel.addRow(new Object[]{rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getTimestamp(4)});
        }
        rs.close();
        return  defaultTableModel;
    }

}