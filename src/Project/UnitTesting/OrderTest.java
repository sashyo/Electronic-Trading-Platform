package Project.UnitTesting;

import Project.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * This class hold the unit testing methods for Order.
 */
public class OrderTest {

    // Test Variables //

    static Order order = new Order(1, 1, Order.Type.BUY, 10, 20);
    static Order orderRemove = new Order(1, 1, Order.Type.SELL, 10, 20);
    static Order orderNotRem = new Order(1, 1, Order.Type.SELL, 10, 20);
    static Order orderBuy = new Order(1, 1, Order.Type.BUY, 10, 20);
    static Order orderSell = new Order(1, 1, Order.Type.SELL, 10, 20);
    static Order orderSell2 = new Order(1, 1, Order.Type.SELL, 5, 5);


    // Test Methods//

    // Initialise data before all tests

    /**
     * The setup test for mock data.
     */
    @BeforeAll // crate two users from two diff org units
    public static void setup() {
        Asset myAsset = new Asset("Check orders", 20, 1);
        Transaction myTransaction = new Transaction(4, 5);
        OrganisationalUnit orgUnitEnough = new OrganisationalUnit("Enough Credits", 200);
        OrganisationalUnit orgUnitNotEnough = new OrganisationalUnit("Not Enough Credits", 2);
        //User user = new User("Test", "User", "testUser", 2, 2, 1, "password");

        myAsset.addAsset(myAsset);
        myTransaction.addTransaction(myTransaction);
        orgUnitEnough.addOrgUnit(orgUnitEnough);
        orgUnitNotEnough.addOrgUnit(orgUnitNotEnough);

        order.addOrder(order);
        orderRemove.addOrder(orderRemove);
        orderNotRem.addOrder(orderNotRem);
        orderBuy.addOrder(orderBuy);
        orderSell.addOrder(orderSell);
        orderSell2.addOrder(orderSell2);

        orderNotRem.updateStatus(3, 2);
    }


    // add order
    //      - add buy order with enough credits
    //      - add buy order with insufficient credits
    //      - add order with correct quantity
    //      - add order with too high of a quantity

    /**
     * Add a sell order on an asset that exists, and the user has enough credits.
     */
    @Test
    /* Test passed */
    public void A_addOrder() {
        int userID = 1;
        int assetID = 1;
        Order.Type type = Order.Type.SELL;
        int quantity = 10;
        int price = 15;

        Order myOrder = new Order(userID, assetID, type, quantity, price);
        order.addOrder((myOrder));
    }

    /**
     * Add a buy order on an asset that exists, and the user has enough credits.
     */
    @Test
    /* Test passed */
    public void B_addOrderEnoughCredits() {
        int userID = 1;
        int assetID = 1;
        Order.Type type = Order.Type.BUY;
        int quantity = 3;
        int price = 1;

        Order myOrder = new Order(userID, assetID, type, quantity, price);
        order.addOrder((myOrder));
    }

    /**
     * Add a buy order where there use does not have enough credits.
     */
    @Test
    /* Test passed */
    public void C_addOrderNotEnoughCredits() {
        int userID = 2;
        int assetID = 1;
        Order.Type type = Order.Type.BUY;
        int quantity = 20;
        int price = 10;

        Order myOrder = new Order(userID, assetID, type, quantity, price);
        order.addOrder((myOrder));
    }

    /**
     * Add a buy order on an asset that exists, and the asset has enough quantity.
     */
    @Test
    /* Test passed */
    public void D_addOrderQuantity() {
        int userID = 1;
        int assetID = 1;
        Order.Type type = Order.Type.BUY;
        int quantity = 5;
        int price = 15;

        Order myOrder = new Order(userID, assetID, type, quantity, price);
        order.addOrder((myOrder));
    }

    /**
     * Add a sell order where there is not enough quantity of the asset.
     */
    @Test
    /* Test passed */
    public void E_addOrderWrongQuantity() {
        int userID = 1;
        int assetID = 1;
        Order.Type type = Order.Type.SELL;
        int quantity = 100;
        int price = 15;

        Order myOrder = new Order(userID, assetID, type, quantity, price);
        order.addOrder((myOrder));
    }

    /**
     * Add an order on a asset that does not exist.
     */
    @Test
    /* Test passed */
    public void F_assetNotExist() {
        int userID = 1;
        int assetID = 100;
        Order.Type type = Order.Type.SELL;
        int quantity = 10;
        int price = 15;

        Order myOrder = new Order(userID, assetID, type, quantity, price);
        order.addOrder((myOrder));
    }

    /**
     * Add an order when the user does not exist.
     */
    @Test
    /* Test passed */
    public void G_userNotExist() {
        int userID = 100;
        int assetID = 1;
        Order.Type type = Order.Type.BUY;
        int quantity = 10;
        int price = 15;

        Order myOrder = new Order(userID, assetID, type, quantity, price);
        order.addOrder((myOrder));
    }


    // remove order
    //      - check if order exists
    //      - check if order does not exist
    //      - check for ongoing transactions - order cannot be removed

    /**
     * Remove an order.
     */
    @Test
    /* Test Passed */
    public void H_removeOrder() {
        int orderID = 2;

        Order.removeOrder(orderID);
    }

    /**
     * Remove an order that does not exist.
     */
    @Test
    /* Test Passed */
    public void I_removeOrderNotExists() {
        int orderID = 100;

        Order.removeOrder(orderID);
    }

    /**
     * Remome a completed order.
     */
    @Test
    /* Test Passed */
    public void J_removeOrderWrongStatus() {
        int orderID = 4;

        Order.removeOrder(orderID);
    }


    // update price
    //      - check order exists
    //      - check order does not exist
    //      - check negative price
    //      - if buy, check that price does not exceed credits

    /**
     * Update the price of an order.
     */
    @Test
    /* Test Passed */
    public void L_updatePrice() {
        int orderID = 1;
        int price = 5;

        order.updatePrice(orderID, price);
    }

    /**
     * Update the price of an order that does not exist.
     */
    @Test
    /* Test passed */
    public void M_updatePriceNotExists() {
        int orderID = 100;
        int price = 5;

        order.updatePrice(orderID, price);
    }

    /**
     * Update the price of an order with a negative.
     */
    @Test
    /* Test passed */
    public void N_updatePriceNegative() {
        int orderID = 1;
        int price = -10;

        order.updatePrice(orderID, price);
    }

    /**
     * Update the price of an order where price exceeds credits.
     */
    @Test
    /* Test passed */
    public void O_updatePriceExceed() {
        int orderID = 1;
        int price = 500;

        order.updatePrice(orderID, price);
    }


    // update status
    //      - order exists
    //      - order not exists

    /**
     * Update the status of an order.
     */
    @Test
    /* Test passed */
    public void P_updateStatusExists() {
        int orderID = 1;
        int statusID = 3;

        order.updateStatus(orderID, statusID);
    }

    /**
     * Update the status of an order that does not exist.
     */
    @Test
    /* Test passed */
    public void Q_updateStatusNotExists() {
        int orderID = 100;
        int statusID = 5;

        order.updateStatus(orderID, statusID);
    }


    // compare orders
    //      - compare matching orders
    //      - compare incorrect order types
    //      - compare orders with different values

    /**
     * Compare orders to create transaction.
     */
    @Test
    public void R_compareOrders() {
        int buyID = 4;
        int sellID = 5;

        order.compareOrders(buyID, sellID);
    }

    /**
     * Compare orders with incorrect types.
     */
    @Test
    public void S_compareIncorrectTypes() {
        int buyID = 4;
        int sellID = 4;

        order.compareOrders(buyID, sellID);
    }

    /**
     * Compare orders with different information.
     */
    @Test
    public void T_compareDifferentValues() {
        int buyID = 4;
        int sellID = 6;

        order.compareOrders(buyID, sellID);
    }


    // get order
    //      - check order exists
    //      - check order does not exist

    /**
     * Get an order that exists.
     */
    @Test
    /* Test passed */
    public void U_getOrderExists() {
        Order myOrder;
        int orderID = 1;

        myOrder = Order.getOrder(orderID);
        if (myOrder == null) {
            fail();
        }

        System.out.println(myOrder);
    }

    /**
     * Get an order that does not exist.
     */
    @Test
    /* Test passed */
    public void V_getOrderNotExists() {
        Order myOrder;
        int orderID = 100;

        myOrder = Order.getOrder(orderID);
        if (myOrder != null) {
            fail();
        }
    }


    // get order type
    //      - check asset exists
    //      - check asset does not exist

    /**
     * Get an order type on an order that exists.
     */
    @Test
    /* Test passed */
    public void W_getTypeExists() {
        Order.Type type;
        int orderID = 1;

        type = Order.getOrderType(orderID);
        if (type == null) {
            fail();
        }
    }

    /**
     * Get an order type on an order that does not exist.
     */
    @Test
    /* Test passed */
    public void X_getTypeNotExists() {
        Order.Type type;
        int orderID = 100;

        type = Order.getOrderType(orderID);
        if (type != null) {
            fail();
        }
    }


    // get order status
    //      - check asset exists
    //      - check asset does not exist

    /**
     * Get an order status on an order that exists.
     */
    @Test
    /* Test passed */
    public void Y_getStatusExists() {
        Integer statusID;
        int orderID = 1;

        statusID = Order.getOrderStatus(orderID);
        if (statusID == null) {
            fail();
        }
    }

    /**
     * Get an order status on an order that does not exist.
     */
    @Test
    /* Test passed */
    public void Z_getStatusNotExists() {
        Integer statusID;
        int orderID = 100;

        statusID = Order.getOrderStatus(orderID);
        if (statusID != null) {
            fail();
        }
    }


    // get orders list
    //      - all orders
    //      - sell orders
    //      - buy orders

    /**
     * Get list of all order IDs in the database.
     * @throws SQLException when something goes wrong with the database.
     */
    @Test
    /* Test passed */
    public void Z1_getOrders() throws SQLException {
        ArrayList<Integer> orders = Order.getOrders();
        System.out.println(orders);
    }

    /**
     * Get list of all buy order IDs in the database.
     * @throws SQLException when something goes wrong with the database.
     */
    @Test
    /* Test passed */
    public void Z2_getBuyOrders() throws SQLException {
        ArrayList<Integer> orders = Order.getBuyOrders();
        System.out.println(orders);
    }

    /**
     * Get list of all sell order IDs in the database.
     * @throws SQLException when something goes wrong with the database.
     */
    @Test
    /* Test passed */
    public void Z3_getSellOrders() throws SQLException {
        ArrayList<Integer> orders = Order.getSellOrders();
        System.out.println(orders);
    }

}
