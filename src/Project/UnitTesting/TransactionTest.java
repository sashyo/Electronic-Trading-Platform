package Project.UnitTesting;

import Project.Asset;
import Project.Order;
import Project.OrganisationalUnit;
import Project.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * This class hold the unit testing methods for Transaction.
 */
public class TransactionTest {

    // Test Variables //

    static Transaction transaction = new Transaction(1, 1);
    static Transaction transactionRem = new Transaction(1, 1);


    // Test Methods //

    /**
     * The setup test for mock data.
     */
    // Initialise data before all tests
    @BeforeAll
    public static void setup() {
        OrganisationalUnit orgUnit = new OrganisationalUnit("Test Org", 100);
        Asset asset = new Asset("Test Asset", 100, 1);
        Order orderBuy = new Order(1, 1, Order.Type.BUY, 10, 5);
        Order orderSell = new Order(1, 1, Order.Type.SELL, 10, 5);
        Order order = new Order(1, 1, Order.Type.SELL, 10, 5);
        Transaction myTransaction = new Transaction(1, 2);

        orgUnit.addOrgUnit(orgUnit);
        asset.addAsset(asset);
        orderBuy.addOrder(orderBuy);
        orderSell.addOrder(orderSell);
        order.addOrder(order);
        myTransaction.addTransaction(myTransaction);
        transactionRem.addTransaction(transactionRem);
    }


    // add transaction
    //      - create new transaction

    /**
     * Add a transaction.
     */
    @Test
    /* Test passed */
    public void A_addTransaction() {
        int buyID = 1;
        int sellID = 2;

        Transaction myTransaction = new Transaction(buyID, sellID);
        transaction.addTransaction((myTransaction));
    }


    // get transaction
    //      - get transaction that exists
    //      - get transaction that does not exist

    /**
     * Get a transaction.
     */
    @Test
    /* Test passed */
    public void D_getTransaction() {
        int transactionID = 1;
        Transaction myTransaction;

        myTransaction = transaction.getTransaction(transactionID);

        System.out.println(myTransaction);
    }

    /**
     * Get a transaction that does not exist.
     */
    @Test
    /* Test passed */
    public void E_getTransactionNotExists() {
        int transactionID = 100;
        Transaction myTransaction;

        myTransaction = transaction.getTransaction(transactionID);

        System.out.println(myTransaction);
    }


    // get transaction bool
    //      - check if exists - returns true
    //      - check if does not exist - returns false

    /**
     * Check a transaction exits.
     */
    @Test
    /* Test passed */
    public void F_transactionExistsTrue() {
        Boolean exists;
        int orderID = 1;

        exists = Transaction.transactionExists(orderID);
        if (exists == null || !exists) {
            fail();
        }

        System.out.println(exists);
    }

    /**
     * Check a transaction does not exist.
     */
    @Test
    /* Test passed */
    public void C_transactionExistsFalse() {
        Boolean exists;
        int orderID = 300;

        exists = Transaction.transactionExists(orderID);
        if (exists == null || exists) {
            fail();
        }

        System.out.println(exists);
    }

}
