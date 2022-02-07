package Project;

import javax.swing.table.DefaultTableModel;
//import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * This class holds all the functionality and information for the orders.
 */
public class Order {

    // Constants //

    private static final String INSERT_ORDER = "INSERT INTO orders (UserID, AssetID, OrderType, Quantity, Price, OrderStatusID, OrderTimeStamp) VALUES (?, ?, ?, ?, ?, ?, ?);";
    private static final String UPDATE_ORDER = "UPDATE orders SET UserID = ?, AssetID = ?,OrderType = ?,  Quantity = ?, Price = ?  WHERE orderId = ?;";
    private static final String REMOVE_ORDER = "DELETE FROM orders WHERE OrderID=?";
    private static final String UPDATE_PRICE = "UPDATE orders SET Price = ? WHERE OrderId = ?";
    private static final String UPDATE_STATUS = "UPDATE orders SET OrderStatusId = ? WHERE OrderId = ?";
    private static final String GET_ORDER = "SELECT * FROM orders WHERE OrderId=?";
    private static final String GET_ALL_ORDERS = """
            SELECT orders.orderId,orderTypes.orderTypeName,assets.assetName,orders.quantity, orders.price, users.userName,orderStatus.OrderStatusType, orders.ordertimestamp\s
            FROM orders
            JOIN users ON orders.userid=users.userid
            JOIN assets ON orders.assetId=assets.AssetId
            JOIN ordertypes ON orders.ordertype=ordertypes.OrderTypeID
            JOIN orderstatus ON orders.orderstatusid=orderstatus.OrderStatusid
            ;""";

    private static final String GET_ORDER_ASSET = "SELECT * FROM orders WHERE AssetId=?";
    private static final String GET_ORDER_USER = "SELECT * FROM orders WHERE UserId=?";
    private static final String GET_ALL_USER_ORDERS = """
            SELECT orders.orderId,orderTypes.orderTypeName,assets.assetName,orders.quantity, orders.price, users.userName,orderStatus.OrderStatusType, orders.ordertimestamp
            FROM orders
            JOIN users ON orders.userid=users.userid
            JOIN assets ON orders.assetId=assets.AssetId
            JOIN ordertypes ON orders.ordertype=ordertypes.OrderTypeID
            JOIN orderstatus ON orders.orderstatusid=orderstatus.OrderStatusid
            WHERE users.UserId = ?;""";

    private static final String GET_ALL_ORG_ORDERS = """
            SELECT orders.orderId,orderTypes.orderTypeName,assets.assetName,orders.quantity, orders.price, users.userName,orderStatus.OrderStatusType, orders.ordertimestamp, organisations.organisationName
            FROM orders
            JOIN users ON orders.userid=users.userid
            JOIN organisations ON users.OrganisationId=organisations.organisationId
            JOIN assets ON orders.assetId=assets.AssetId
            JOIN ordertypes ON orders.ordertype=ordertypes.OrderTypeID
            JOIN orderstatus ON orders.orderstatusid=orderstatus.OrderStatusid
            WHERE organisations.organisationName = ? AND orderstatus.orderStatusType ="Unfulfilled" OR orderstatus.orderStatusType ="";""";

    private static final String GET_ORG_HISTORY = """
            SELECT orders.orderId,orderTypes.orderTypeName,assets.assetName,orders.quantity, orders.price, users.userName,orderStatus.OrderStatusType, orders.ordertimestamp, organisations.organisationName
            FROM orders
            JOIN users ON orders.userid=users.userid
            JOIN organisations ON users.OrganisationId=organisations.organisationId
            JOIN assets ON orders.assetId=assets.AssetId
            JOIN ordertypes ON orders.ordertype=ordertypes.OrderTypeID
            JOIN orderstatus ON orders.orderstatusid=orderstatus.OrderStatusid
            WHERE organisations.organisationName = ? AND orderstatus.orderStatusType ="Completed";""";

    private static final String GET_DASH_ORDERS = """
            SELECT orderTypes.orderTypeName,assets.assetName,orders.quantity, orders.price, organisations.organisationName,orderStatus.OrderStatusType
            FROM orders
            JOIN users ON orders.UserID=users.userId
            JOIN organisations ON users.OrganisationId=organisations.OrganisationId
            JOIN assets ON orders.assetId=assets.AssetId
            JOIN ordertypes ON orders.ordertype=ordertypes.OrderTypeID
            JOIN orderstatus ON orders.orderstatusid=orderstatus.OrderStatusid
            WHERE orderstatus.orderStatusType ="Unfulfilled\"""";

    private static final String GET_BUY_ORDERS = "SELECT * FROM orders WHERE OrderType=1";
    private static final String GET_SELL_ORDERS = "SELECT * FROM orders WHERE OrderType=2";
    private static final String GET_ORDERS = "SELECT * FROM orders";


    // Variables //

    /**
     * The order types available.
     */
    public enum Type {
        /**
         * The buy order type.
         */
        BUY,
        /**
         * The sell order type.
         */
        SELL
    }

    int userID;
    int assetID;
    Type type;
    int quantity;
    int price;
    int orderStatusID;
    LocalDateTime orderTimestamp;
    int orderId;

    // Database connection
    private static final Connection connection = DBConnection.getConn();


    // Methods //

    /**
     * Empty order constructor for the GUI.
     */
    public Order() {
        // Empty constructor
    }

    /**
     * Order Constructor without ID, database creates the ID.
     *
     * @param userID The ID of the asset involved in the order.
     * @param assetID The ID of the asset involved in the order.
     * @param type The type of order it is (BUY or SELL).
     * @param quantity The quantity to create of the order.
     * @param price The price of the order.
     */
    public Order(int userID, int assetID, Type type, int quantity, int price) {
        this.userID = userID;
        this.assetID = assetID;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.orderStatusID = 0; // 0 = null status
        this.orderTimestamp = java.time.LocalDateTime.now();
    }

    /**
     * Order Constructor with ID.
     *
     * @param orderId The ID of the order.
     * @param userID The ID of the asset involved in the order.
     * @param assetID The ID of the asset involved in the order.
     * @param type The type of order it is (BUY or SELL).
     * @param quantity The quantity to create of the order.
     * @param price The price of the order.
     */
    public Order(int orderId, int userID, int assetID, Type type, int quantity, int price) {
        this.orderId = orderId;
        this.userID = userID;
        this.assetID = assetID;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.orderStatusID = 0; // 0 = null status
        this.orderTimestamp = java.time.LocalDateTime.now();
    }

    /**
     * Adds a created order to the database.
     *
     * @param myOrder The order to add.
     */
    public void addOrder(Order myOrder) {
        // Variables
        PreparedStatement addOrder;
        Integer orgUnitID;
        Double credits;
        Integer inventory;

        // Check quantity of asset creating the order on
        inventory = Asset.getInventory(myOrder.assetID);
        if (inventory == null) {
            // The inventory could be returned
            System.out.println("inventory null - could not return inventory from asset.");
            return;
        }
        else {


            if (myOrder.quantity > inventory && myOrder.type == Type.SELL ) {
                // The quantity is too much
                System.out.println("The order could not be created - insufficient inventory of asset.");
                return;
            }
        }

        // If the order is a buy order, need to check credits
        if (myOrder.type == Type.BUY) {
            // Get the ID of the organisational unit the user who created the order belongs to
            orgUnitID = OrganisationalUnit.getOrgUnitID(myOrder.userID);
            System.out.println(orgUnitID);
            if (orgUnitID == null) {
                // No org unit ID could be returned
                System.out.println("orgUnitID null - could not return ID from user table.");
                return;
            }
            else {
                // Check organisational unit has enough credits
                credits = OrganisationalUnit.getCredits(orgUnitID);
                System.out.println(credits);
                if (credits == null) {
                    // The credits could be returned
                    System.out.println("credits null - could not return credits from org unit.");
                    return;
                }
                else {
                    if (credits < myOrder.price) {
                        // Insufficient credits
                        System.out.println("The order could not be created - insufficient credits.");
                        return;
                    }
                }

                // Add credits to pending
                OrganisationalUnit.addPending(orgUnitID, myOrder.price);
            }
        }

        // Try to add order
        try {
            addOrder = connection.prepareStatement(INSERT_ORDER);

            addOrder.setInt(1, myOrder.userID);
            addOrder.setInt(2, myOrder.assetID);

            if (myOrder.type == Type.BUY) {
                addOrder.setString(3, Integer.toString(1)); // ID of BUY order type is 1
            }
            else {
                addOrder.setString(3, Integer.toString(2)); // ID of SELL order type is 2
            }

            addOrder.setInt(4, myOrder.quantity);
            addOrder.setDouble(5, myOrder.price);
            addOrder.setInt(6, myOrder.orderStatusID);
            addOrder.setString(7, String.valueOf(myOrder.orderTimestamp));
            addOrder.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Removes an order from the database. If the order status is completed it cannot be removed.
     *
     * @param orderID The order orderID.
     */
    public static void removeOrder(int orderID) {
        // Variables
        PreparedStatement removeOrder;
        Order myOrder;
        Integer orderStatus;
        Integer orgUnitID;
        Double credits;

        // Check order exists
        myOrder = getOrder(orderID);
        if (myOrder == null) {
            // Order does not exist, cannot delete
            System.out.print("Order cannot be retrieved - the order does not exist.");
            return;
        }
        else {
            // Check order status
            orderStatus = getOrderStatus(orderID);
            if (orderStatus == null || orderStatus == 2) {
                // The orderStatus is completed
                System.out.print("The order cannot be removed - the orderStatus is completed.");
                return;
            }

            // Get organisational ID and credits
            orgUnitID = OrganisationalUnit.getOrgUnitID(myOrder.userID);
            if (orgUnitID == null) {
                // No org unit ID could be returned
                System.out.println("orgUnitID null - could not return ID from user table.");
                return;
            }
            credits = OrganisationalUnit.getCredits(orgUnitID);
            if (credits == null) {
                // The credits could be returned
                System.out.println("credits null - could not return credits from org unit.");
                return;
            }

            // Remove credits from pending
            OrganisationalUnit.removePending(orgUnitID, myOrder.price);
        }

        // Try to remove from database
        try {
            removeOrder = connection.prepareStatement(REMOVE_ORDER);

            removeOrder.setInt(1, orderID);
            removeOrder.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Updates the price of a sell order in the database.
     *
     * @param orderID The ID of the sell order to update.
     * @param newPrice The new price.
     */
    public void updatePrice(int orderID, double newPrice) {
        // Variables
        PreparedStatement updatePrice;
        Order myOrder;
        Integer orgUnitID;
        Double credits;

        // Check not negative
        if (newPrice < 0) {
            // Price negative, do not update
            System.out.print("Price negative.");
            return;
        }

        // Check that order exists
        myOrder = getOrder(orderID);
        if (myOrder == null){
            // Order does not exist
            System.out.print("Could not update price - order does not exist.");
            return;
        }

        // Check status ID is not 2 = completed
        if (myOrder.orderStatusID == 2) {
            // Status is completed
            System.out.print("Could not update price - order status is completed.");
            return;
        }

        // If buy order, check that price does not exceed credits
        if (myOrder.type == Type.BUY) {
            // Get the ID of the organisational unit the user who created the order belongs to
            orgUnitID = OrganisationalUnit.getOrgUnitID(myOrder.userID);
            if (orgUnitID == null) {
                // No org unit ID could be returned
                System.out.println("orgUnitID null - could not return ID from user table.");
                return;
            }
            else {
                // Check organisational unit has enough credits
                credits = OrganisationalUnit.getCredits(orgUnitID);
                if (credits == null) {
                    // The credits could be returned
                    System.out.println("credits null - could not return credits from org unit.");
                    return;
                }
                else {
                    if (credits < newPrice) {
                        // Insufficient credits
                        System.out.println("The could not be updated - insufficient credits.");
                        return;
                    }
                }
            }
        }

        // Try to update price
        try {
            updatePrice = connection.prepareStatement(UPDATE_PRICE);

            updatePrice.setDouble(1, newPrice);
            updatePrice.setInt(2, orderID);
            updatePrice.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Updates the status of an order in the database.
     *
     * @param orderID The ID of the sell order to update.
     * @param newStatusID The new status.
     */
    public static void updateStatus(int orderID, int newStatusID) {
        // Variables
        Order myOrder;
        PreparedStatement updateStatus;

        // Check that order exists
        myOrder = getOrder(orderID);
        if (myOrder == null){
            // Order does not exist
            System.out.print("Could not update status - order does not exist.");
            return;
        }

        // Check status ID exists
        if (newStatusID > 2 || newStatusID < 0) {
            // Status does not exist
            System.out.print("Could not update status - this status ID does not exist.");
            return;
        }

        // Try to update status
        try {
            updateStatus = connection.prepareStatement(UPDATE_STATUS);

            updateStatus.setDouble(1, newStatusID);
            updateStatus.setInt(2, orderID);
            updateStatus.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Compares two orders and adds a transaction to the database if requirements are met.
     *
     * @param buyOrderID The ID of the first order.
     * @param sellOrderID The ID of the second order.
     */
    public static void compareOrders(int buyOrderID, int sellOrderID) {
        // Variables
        Order buyOrder;
        Order sellOrder;
        Order.Type orderTypeBuy;
        Order.Type orderTypeSell;
        Integer buyOrgUnitID;
        Integer sellOrgUnitID;
        Integer buyStatus;
        Integer sellStatus;
        double price;
        double unspent;
        Double credits;
        Integer assetQuant;
        Transaction newTransaction;
        Boolean added;
        boolean outStock = false;

        // Check order types
        orderTypeBuy = Order.getOrderType(buyOrderID);
        orderTypeSell = Order.getOrderType(sellOrderID);
        if (orderTypeBuy != Order.Type.BUY || orderTypeSell != Order.Type.SELL) {
            // Incorrect order types
            System.out.println("Could not create transaction - the order types of these orders are incorrect.");
            return;
        }

        // Get the orders from these ids
        buyOrder = getOrder(buyOrderID);
        sellOrder = getOrder(sellOrderID);
        if (buyOrder == null || sellOrder == null) {
            // Couldn't return order(s) from ID(s)
            System.out.println("Could not create transaction - order(s) could not be returned.");
            return;
        }

        // Get organisational unit's the orders come from
        buyOrgUnitID = OrganisationalUnit.getOrgUnitID(buyOrder.userID);
        sellOrgUnitID = OrganisationalUnit.getOrgUnitID(sellOrder.userID);
        if (buyOrgUnitID == null || sellOrgUnitID == null) {
            // Couldn't return org unit ID
            System.out.println("Could not create transaction - the org unit(s) ID could not be returned.");
            return;
        }

        // Get and check order status'
        buyStatus = Order.getOrderStatus(buyOrderID);
        sellStatus = Order.getOrderStatus(sellOrderID);
        if (buyStatus == null || sellStatus == null || buyStatus != 1 || sellStatus != 1) {
            // Couldn't return status IDs
            System.out.println("Could not create transaction - the order status is not 'Unfulfilled'.");
            return;
        }

        // Check asset quantity can be retrieved and is correct
        assetQuant = Asset.getInventory(buyOrder.assetID);
        if (assetQuant == null) {
            // The asset quantity could not retrieved
            System.out.println("Could not create transaction - asset quantity could not be retrieved.");
            return;
        }
        else if (assetQuant < buyOrder.quantity) {
            // The asset quantity is too low
            outStock = true;
        }

        // Compare the details for these orders
        if (buyOrder.assetID == sellOrder.assetID && buyOrder.quantity == sellOrder.quantity && !outStock) {
            // Check prices and get lowest
            price = lowestPrice(buyOrder.price, sellOrder.price);
            if (price == -1) {
                // The buy order price undercuts the sell order
                System.out.println("Could not create transaction - buy price undercuts sell price.");
                return;
            }

            // These orders fit the requirements to create a transaction
            newTransaction = new Transaction(buyOrderID, sellOrderID);
            //add asset for buyer
            Asset assetData = new Asset();
            Asset name = Asset.getAsset(buyOrder.assetID);
            Asset temp = new Asset(name.name,buyOrder.quantity, OrganisationalUnit.getOrgUnitID(buyOrder.userID));
            assetData.addAsset(temp);

            // Try to add the transaction the the database
            added = Transaction.addTransaction(newTransaction);

            if (added) {
                // Transaction added, update order status'
                updateStatus(buyOrderID, 2); // 2 = completed
                updateStatus(sellOrderID, 2);

                // Remove quantity from asset
                assetQuant = assetQuant - buyOrder.quantity;
                Asset.updateQuantity(buyOrder.assetID, assetQuant);

                // Check if the buy order price was higher
                if (price < buyOrder.price) {
                    // Get unspent credits
                    unspent = buyOrder.price - price;

                    // Add back into the org units credits
                    credits = OrganisationalUnit.getCredits(buyOrgUnitID);
                    if (credits == null) {
                        credits = 0.0;
                    }
                    credits += unspent;
                    OrganisationalUnit.updateCredits(buyOrgUnitID, credits);

                    // Remove unspent from pending
                    OrganisationalUnit.removePending(buyOrgUnitID, unspent);
                }

                // Remove spent credits from pending column
                OrganisationalUnit.removePending(buyOrgUnitID, price);

                // Add spent credits to org of sell order
                credits = OrganisationalUnit.getCredits(sellOrgUnitID);
                if (credits == null) {
                    credits = 0.0;
                }
                credits += price;
                OrganisationalUnit.updateCredits(sellOrgUnitID, credits);


            }
            else {
                System.out.println("Could not create transaction - transaction failed to add to database.");
                return;
            }
        }
        else if (!outStock) {
            // These orders do not fit the requirements to create a transaction
            System.out.println("Could not create transaction - the order(s do not fit requirements.");
            return;
        }

        // Tell user if stock is out
        if (outStock) {
            System.out.println("TRANSACTION: Orders pending - there is not yet enough stock to complete.");
        }
    }

    /**
     * Searches for an order in the database using the order ID and retrieves it.
     *
     * @param orderID The ID of the order to find.
     * @return The order if it exists, otherwise returns null.
     */
    public static Order getOrder(int orderID) {
        // Variables
        PreparedStatement getOrder;
        final ResultSet result;
        String orderTypeString;
        Type orderType;

        // Try to retrieve order
        try {
            getOrder = connection.prepareStatement(GET_ORDER);

            getOrder.setInt(1, orderID);

            result = getOrder.executeQuery();
            if (!result.next()) {
                // No order matched this ID
                return null;
            }

            // Change order type variable into the enum type variable
            orderTypeString = result.getString("OrderType");
            if (orderTypeString.equals("1")){
                orderType = Type.BUY;
            }
            else {
                orderType = Type.SELL;
            }

            return new Order(
                    result.getInt("orderId"),
                    result.getInt("UserID"),
                    result.getInt("AssetID"),
                    orderType,
                    result.getInt("Quantity"),
                    result.getInt("Price")
            );

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Searches for an order in the database using the order ID and returns its type.
     *
     * @param orderID The ID of the order to find.
     * @return The type of the order, otherwise returns null.
     */
    public static Type getOrderType(int orderID) {
        // Variables
        PreparedStatement getOrder;
        final ResultSet result;
        String orderTypeString;
        Type orderType;

        // Try to retrieve type
        try {
            getOrder = connection.prepareStatement(GET_ORDER);

            getOrder.setString(1, Integer.toString(orderID));

            result = getOrder.executeQuery();
            if (!result.next()) {
                // No order matched this ID
                return null;
            }

            // Change order type variable into the enum type variable
            orderTypeString = result.getString("OrderType");
            if (orderTypeString.equals("1")){
                orderType = Type.BUY;
            }
            else {
                orderType = Type.SELL;
            }

            return orderType;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Searches for an order in the database using the order ID and returns its status ID.
     *
     * @param orderID The ID of the order to find.
     * @return The status ID of the order, otherwise returns null.
     */
    public static Integer getOrderStatus(int orderID) {
        // Variables
        PreparedStatement getOrder;
        final ResultSet result;

        // Try to retrieve order status ID
        try {
            getOrder = connection.prepareStatement(GET_ORDER);

            getOrder.setString(1, Integer.toString(orderID));

            result = getOrder.executeQuery();
            if (!result.next()) {
                // No order matched this ID
                return null;
            }

            return result.getInt("OrderStatusId");

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves list of all the current orders out on an asset in the database.
     *
     * @param assetID The ID of the asset to get orders for.
     * @return The list of all the IDs of order out on an asset.
     * @throws SQLException when something goes wrong with the database.
     */
    public static ArrayList<Integer> getAssetOrders(int assetID) throws SQLException {
        int temp;
        ResultSet rs;
        ArrayList<Integer> orders = new ArrayList<>();

        // Get order list
        PreparedStatement getOrders = connection.prepareStatement(GET_ORDER_ASSET);
        getOrders.setString(1, Integer.toString(assetID));

        rs = getOrders.executeQuery();
        while(rs.next()){
            temp = rs.getInt(1);
            orders.add(temp);
        }
        return orders;
    }

    /**
     * Retrieves list of all the current orders a user has in the database.
     *
     * @param userID The ID of the user to get orders for.
     * @return The list of all the IDs of order made by a user.
     * @throws SQLException when something goes wrong with the database.
     */
    public static ArrayList<Integer> getUserOrders(int userID) throws SQLException {
        int temp;
        ResultSet rs;
        ArrayList<Integer> orders = new ArrayList<>();

        // Get order list
        PreparedStatement getOrders = connection.prepareStatement(GET_ORDER_USER);
        getOrders.setString(1, Integer.toString(userID));

        rs = getOrders.executeQuery();
        while(rs.next()){
            temp = rs.getInt(1);
            orders.add(temp);
        }
        return orders;
    }

    /**
     * Retrieves the price of a current order.
     *
     * @param orderID The order to get price for.
     * @return The price of the order, or null if something went wrong.
     */
    public static Double getPrice(int orderID) {
        // Variables
        PreparedStatement getPrice;
        final ResultSet result;

        // Try to retrieve order status ID
        try {
            getPrice = connection.prepareStatement(GET_ORDER);

            getPrice.setInt(1, orderID);

            result = getPrice.executeQuery();
            if (!result.next()) {
                // No order matched this ID
                return null;
            }

            return result.getDouble("Price");

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Creates table model for orders in the GUI - lists all current orders.
     *
     * @return A DefaultTableModel if the orders could be found.
     * @throws SQLException when something goes wrong with the database.
     */
    public DefaultTableModel getAllOrders() throws SQLException {
        ResultSet rs;
        PreparedStatement getAllOrders;
        getAllOrders = connection.prepareStatement(GET_ALL_ORDERS);

        DefaultTableModel defaultTableModel = new DefaultTableModel(){@Override
        public boolean isCellEditable(int row, int column) {
            //all cells false
            return false;
        }};

        defaultTableModel.addColumn("Order ID:");
        defaultTableModel.addColumn("Order Type");
        defaultTableModel.addColumn("Asset");
        defaultTableModel.addColumn("Quantity");
        defaultTableModel.addColumn("Price");
        defaultTableModel.addColumn("User");
        defaultTableModel.addColumn("Status");
        defaultTableModel.addColumn("Timestamp");

        rs= getAllOrders.executeQuery();
        //ArrayList<String>orderList = new ArrayList<>();

        while(rs.next()){
            defaultTableModel.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4), rs.getBigDecimal(5),rs.getString(6),rs.getString(7), rs.getTimestamp(8)});
        }
        rs.close();

        return  defaultTableModel;
    }

    /**
     * Updates order information in the GUI.
     *
     * @param userID The ID of user who owns the order.
     * @param assetID The ID of the asset the order is on.
     * @param type The type of order.
     * @param qty The quantity of the asset.
     * @param price The price of the order.
     * @param orderId The ID of tne order updating.
     * @throws SQLException when something goes wrong with the database.
     */
    public static void updateOrder(int userID, int assetID, int type, int qty, double price, int orderId) throws SQLException {
        //ResultSet rs;
        PreparedStatement updateOrder;
        Integer orgUnitID;
        Double origPrice;

        // Get original price
        origPrice = getPrice(orderId);

        // Update order
        updateOrder = connection.prepareStatement(UPDATE_ORDER);

        updateOrder.setInt(1,userID);
        updateOrder.setInt(2,assetID);
        updateOrder.setInt(3,type);
        updateOrder.setInt(4,qty);
        updateOrder.setDouble(5,price);
        updateOrder.setInt(6, orderId);

        updateOrder.executeQuery();

        // Check if price changed
        if (origPrice != null && origPrice != price) {
            // Update org unit pending if buy order
            if (type == 1) {
                // Get org unit id
                orgUnitID = OrganisationalUnit.getOrgUnitID(userID);
                if (orgUnitID != null) {
                    // Put original price back into credits from pending
                    OrganisationalUnit.revertPending(orgUnitID, origPrice);

                    // Add new price to pending
                    OrganisationalUnit.addPending(orgUnitID, price);
                }
            }
        }
    }

    /**
     * Creates table model for a specific users orders - lists all orders of a user.
     *
     * @param userID  The ID of the user.
     * @return A DefaultTableModel if the orders could be found.
     * @throws SQLException when something goes wrong with the database.
     */
    public DefaultTableModel getAllUserOrders(int userID) throws SQLException {
        ResultSet rs;
        PreparedStatement getAllOrders;

        getAllOrders = connection.prepareStatement(GET_ALL_USER_ORDERS);

        getAllOrders.setInt(1,userID);
        DefaultTableModel defaultTableModel = new DefaultTableModel(){@Override
        public boolean isCellEditable(int row, int column) {
            //all cells false
            return false;
        }};

        defaultTableModel.addColumn("Order ID:");
        defaultTableModel.addColumn("Order Type");
        defaultTableModel.addColumn("Asset");
        defaultTableModel.addColumn("Quantity");
        defaultTableModel.addColumn("Price");
        defaultTableModel.addColumn("User");
        defaultTableModel.addColumn("Status");
        defaultTableModel.addColumn("Timestamp");

        rs= getAllOrders.executeQuery();
        //ArrayList<String>orderList = new ArrayList<>();

        while(rs.next()){
            defaultTableModel.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4), rs.getBigDecimal(5),rs.getString(6),rs.getString(7), rs.getTimestamp(8)});
        }
        rs.close();

        return  defaultTableModel;
    }

    /**
     * Creates table model for an organisation's orders - lists all orders of an organisation.
     *
     * @param orgName The name of the organisation.
     * @return A DefaultTableModel if the orders could be found.
     * @throws SQLException when something goes wrong with the database.
     */
    public DefaultTableModel getAllOrgOrders(String orgName) throws SQLException {
        ResultSet rs;
        PreparedStatement getAllOrders;

        getAllOrders = connection.prepareStatement(GET_ALL_ORG_ORDERS);

        getAllOrders.setString(1,orgName);
        DefaultTableModel defaultTableModel = new DefaultTableModel(){@Override
        public boolean isCellEditable(int row, int column) {
            //all cells false
            return false;
        }};

        defaultTableModel.addColumn("Order ID:");
        defaultTableModel.addColumn("Order Type");
        defaultTableModel.addColumn("Asset");
        defaultTableModel.addColumn("Quantity");
        defaultTableModel.addColumn("Price");
        defaultTableModel.addColumn("User");
        defaultTableModel.addColumn("Status");
        defaultTableModel.addColumn("Timestamp");

        rs= getAllOrders.executeQuery();
        //ArrayList<String>orderList = new ArrayList<>();

        while(rs.next()){
            defaultTableModel.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4), rs.getBigDecimal(5),rs.getString(6),rs.getString(7), rs.getTimestamp(8)});
        }
        rs.close();

        return  defaultTableModel;
    }

    /**
     * Creates table model for current orders for all users to see - lists all orders, but only specific information.
     *
     * @return A DefaultTableModel if the orders could be found.
     * @throws SQLException when something goes wrong with the database.
     */
    public DefaultTableModel getAllDashOrders() throws SQLException {
        ResultSet rs;
        PreparedStatement getAllOrders;

        getAllOrders = connection.prepareStatement(GET_DASH_ORDERS);

        DefaultTableModel defaultTableModel = new DefaultTableModel(){@Override
        public boolean isCellEditable(int row, int column) {
            //all cells false
            return false;
        }};

        defaultTableModel.addColumn("Order Type");
        defaultTableModel.addColumn("Asset");
        defaultTableModel.addColumn("Quantity");
        defaultTableModel.addColumn("Price");
        defaultTableModel.addColumn("Organisation");
        defaultTableModel.addColumn("Status");

        rs= getAllOrders.executeQuery();
        //ArrayList<String>orderList = new ArrayList<>();

        while(rs.next()){
            defaultTableModel.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getInt(3), rs.getBigDecimal(4),rs.getString(5),rs.getString(6)});
        }
        rs.close();

        return  defaultTableModel;
    }

    /**
     * Creates table model for complete orders (order history) for all users to see in a specific organisation.
     *
     * @param orgName The name of the organisation.
     * @return A DefaultTableModel if the orders could be found.
     * @throws SQLException when something goes wrong with the database.
     */
    public DefaultTableModel getOrgOrderHistory(String orgName) throws SQLException {
        ResultSet rs;
        PreparedStatement getOrgOrderHistory;

        getOrgOrderHistory = connection.prepareStatement(GET_ORG_HISTORY);
        getOrgOrderHistory.setString(1,orgName);

        DefaultTableModel defaultTableModel = new DefaultTableModel(){@Override
        public boolean isCellEditable(int row, int column) {
            //all cells false
            return false;
        }};

        defaultTableModel.addColumn("Order ID:");
        defaultTableModel.addColumn("Order Type");
        defaultTableModel.addColumn("Asset");
        defaultTableModel.addColumn("Quantity");
        defaultTableModel.addColumn("Price");
        defaultTableModel.addColumn("User");
        defaultTableModel.addColumn("Status");
        defaultTableModel.addColumn("Timestamp");

        rs= getOrgOrderHistory.executeQuery();
        //ArrayList<String>orderList = new ArrayList<>();

        while(rs.next()){
            defaultTableModel.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4), rs.getBigDecimal(5),rs.getString(6),rs.getString(7), rs.getTimestamp(8)});
        }
        rs.close();

        return  defaultTableModel;
    }

    /**
     * Checks all orders of an asset if any are completed.
     *
     * @param assetID The asset to check orders on.
     * @return True if there are completed orders, false otherwise.
     * @throws SQLException when something goes wrong with the database.
     */
    public static Boolean checkOrderStatusAsset(int assetID) throws SQLException {
        Integer statusID;
        ArrayList<Integer> orders;
        boolean completed = false;

        // Get IDs of all orders on an asset
        orders = getAssetOrders(assetID);
        if (orders.isEmpty()) {
            return false;
        }

        // Check status'
        for (int orderID : orders) {
            statusID = getOrderStatus(orderID);

            // Get if completed
            if (statusID != null && statusID == 2) {
                completed = true;
            }
        }

        return completed;
    }

    /**
     * Checks all orders of a user if any are completed.
     *
     * @param userID The user to check orders on.
     * @return True if there are completed orders, false otherwise.
     * @throws SQLException when something goes wrong with the database.
     */
    public static Boolean checkOrderStatusUser(int userID) throws SQLException {
        Integer statusID;
        ArrayList<Integer> orders;
        boolean completed = false;

        // Get IDs of all orders on an asset
        orders = getUserOrders(userID);
        if (orders.isEmpty()) {
            return false;
        }

        // Check status'
        for (int orderID : orders) {
            statusID = getOrderStatus(orderID);

            // Get if completed
            if (statusID != null && statusID == 2) {
                completed = true;
            }
        }

        return completed;
    }

    /**
     * Matches buy and sell order prices to retrieve lowest price.
     *
     * @param buyPrice The price of the buy order.
     * @param sellPrice The price of the sell order.
     * @return The lowest price (sell price), or -1 if buy price undercuts sell price.
     */
    public static double lowestPrice(double buyPrice, double sellPrice) {
        if (buyPrice < sellPrice) {
            // The buy price does not meet minimum price required
            return -1;
        }
        else {
            // The sell price is always the minimum price
            return sellPrice;
        }
    }

    /**
     * Retrieves list of all the current buy orders in the database.
     *
     * @return A list of all the buy order IDs
     * @throws SQLException when something goes wrong with the database.
     */
    public static ArrayList<Integer> getBuyOrders() throws SQLException {
        int temp;
        ResultSet rs;
        ArrayList<Integer> buyOrders = new ArrayList<>();

        PreparedStatement getBuyOrders = connection.prepareStatement(GET_BUY_ORDERS);

        rs = getBuyOrders.executeQuery();
        while(rs.next()){
            temp = rs.getInt(1);
            buyOrders.add(temp);
        }
        return buyOrders;
    }

    /**
     * Retrieves list of all the current sell orders in the database.
     *
     * @return A list of all the sell order IDs
     * @throws SQLException when something goes wrong with the database.
     */
    public static ArrayList<Integer> getSellOrders() throws SQLException {
        int temp;
        ResultSet rs;
        ArrayList<Integer> sellOrders = new ArrayList<>();

        PreparedStatement getSellOrders = connection.prepareStatement(GET_SELL_ORDERS);

        rs = getSellOrders.executeQuery();
        while(rs.next()){
            temp = rs.getInt(1);
            sellOrders.add(temp);
        }
        return sellOrders;
    }

    /**
     * Retrieves list of all the current orders in the database.
     *
     * @return A list of all the order IDs
     * @throws SQLException when something goes wrong with the database.
     */
    public static ArrayList<Integer> getOrders() throws SQLException {
        int temp;
        ResultSet rs;
        ArrayList<Integer> orders = new ArrayList<>();

        PreparedStatement getOrders = connection.prepareStatement(GET_ORDERS);

        rs = getOrders.executeQuery();
        while(rs.next()){
            temp = rs.getInt(1);
            orders.add(temp);
        }
        return orders;
    }

    /**
     * Automates the "compareOrders" method to create transactions.
     *
     * @throws SQLException when something goes wrong with the database.
     */
    public static void automateTransactions() throws SQLException {
        // Variables
        ArrayList<Integer> buyOrders;
        ArrayList<Integer> sellOrders;
        Order buyOrder;
        Order sellOrder;
        Integer buyStatus;
        Integer sellStatus;
        double price;

        // Get current buy and sell orders
        buyOrders = getBuyOrders();
        sellOrders = getSellOrders();

        // Loop through all orders to compare
        for (int buyID : buyOrders)
        {
            for (int sellID : sellOrders)
            {
                // Get orders for these ids
                buyOrder = getOrder(buyID);
                sellOrder = getOrder(sellID);

                // If orders are null ignore, and move on to next ids
                if (buyOrder != null && sellOrder != null) {
                    // Get and check order status'
                    buyStatus = getOrderStatus(buyID);
                    sellStatus = getOrderStatus(sellID);

                    // If status' are null/completed ignore, and move on to next ids
                    if ((buyStatus != null && sellStatus != null) && (buyStatus == 1 && sellStatus == 1)) {
                        // If this return -1, ignore and move on the next ids
                        price = lowestPrice(buyOrder.price, sellOrder.price);
                        if (price != -1) {
                            // Compare the details for these orders
                            if (buyOrder.assetID == sellOrder.assetID && buyOrder.quantity == sellOrder.quantity) {
                                // Compare the orders
                                compareOrders(buyID, sellID);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Automates the "updateStatus" method to change status to "Unfulfilled" when the status is null.
     *
     * @throws SQLException when something goes wrong with the database.
     */
    public static void automateStatusUpdate() throws SQLException {
        ArrayList<Integer> orders;
        Integer statusID;

        // Get orders
        orders = getOrders();

        // Loop through orders
        for (int orderID : orders) {
            // Get order status
            statusID = getOrderStatus(orderID);

            // Edit status if 0
            if (statusID != null && statusID == 0) {
                updateStatus(orderID, 1);
            }
        }
    }

}