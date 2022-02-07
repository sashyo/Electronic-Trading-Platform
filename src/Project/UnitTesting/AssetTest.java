package Project.UnitTesting;

import static org.junit.jupiter.api.Assertions.*;

import Project.Asset;
import Project.Order;
import Project.OrganisationalUnit;
import Project.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class hold the unit testing methods for Asset.
 */
public class AssetTest {

    // Testing Variables //

    static Asset asset = new Asset("Asset", 13, 1);
    static Asset assetNoOrders = new Asset("Asset No Orders", 7, 1);
    static Asset remAsset = new Asset("Remove Asset", 24, 1);
    static Asset assetOrderNotRemove = new Asset("Asset Orders Not Remove", 6, 1);
    static Asset assetTrans = new Asset("Asset Orders Remove", 18, 1);


    // Testing Methods //

    /**
     * The setup test for mock data.
     */
    // Initialise data before all tests
    @BeforeAll
    public static void setup() {
        Order orderRemove = new Order(1, 3, Order.Type.BUY, 2, 12);
        Order orderNotRemove = new Order(1, 4, Order.Type.BUY, 1, 5);
        Order orderBuy = new Order(1, 5, Order.Type.BUY, 2, 12);
        Order orderSell = new Order(1, 5, Order.Type.SELL, 2, 12);
        Transaction transaction = new Transaction(3, 4);
        OrganisationalUnit organisationalUnit = new OrganisationalUnit("Get Inventory", 100);
        OrganisationalUnit organisationalUnit2 = new OrganisationalUnit("Test", 100);

        asset.addAsset(asset);
        assetNoOrders.addAsset(assetNoOrders);
        remAsset.addAsset(remAsset);
        assetOrderNotRemove.addAsset(assetOrderNotRemove);
        assetTrans.addAsset(assetTrans);

        orderRemove.addOrder(orderRemove);
        orderNotRemove.addOrder(orderNotRemove);
        orderBuy.addOrder(orderBuy);
        orderSell.addOrder(orderSell);
        transaction.addTransaction(transaction);
        organisationalUnit.addOrgUnit(organisationalUnit);
        organisationalUnit2.addOrgUnit(organisationalUnit2);

//        orderNotRemove.updateStatus(2, 3);
    }


    // add asset
    //      - add new asset
    //      - add asset that already exists

    /**
     * Add an asset.
     */
    @Test
    /* Test passed */
    public void A_addAsset() {
        String name = "Test Asset";
        int quantity = 3;
        int orgUnitID = 1;

        Asset asset = new Asset(name, quantity, orgUnitID);
        asset.addAsset(asset);
    }

    /**
     * Add an existing asset.
     */
    @Test
    /* Test passed */
    public void B_addAssetExists() {
        String name = "Test Asset";
        int quantity = 3;
        int orgUnitID = 1;

        Asset asset = new Asset(name, quantity, orgUnitID);
        asset.addAsset(asset);
    }


    // remove asset
    //      - check if asset exists (with no orders or transactions)
    //      - check if asset does not exist
    //      - check if it has orders with status ID of 1 or 2 - orders should be removed too
    //      - check if it has orders with status ID that's not 1 or 2 - asset (and its orders) cannot be removed
    //      - check for ongoing transactions - asset (and its orders) cannot be removed

    /**
     * Remove an asset.
     * @throws SQLException when something goes wrong in the database.
     */
    @Test
    /* Test passed */
    public void C_removeAsset() throws SQLException {
        int assetID = 2;

        remAsset.removeAsset(assetID);
    }

    /**
     * Remove an asset that does not exist.
     * @throws SQLException when something goes wrong in the database.
     */
    @Test
    /* Test passed */
    public void D_removeAssetNotExists() throws SQLException {
        int assetID = 100;

        remAsset.removeAsset(assetID);
    }

    /**
     * Remove an asset with a correct status.
     * @throws SQLException when something goes wrong in the database.
     */
    @Test
    public void E_removeOrderStatus() throws SQLException {
        int assetID = 3;

        remAsset.removeAsset(assetID);
    }

    /**
     * Remove an asset with an incorrect status.
     * @throws SQLException when something goes wrong in the database.
     */
    @Test
    public void F_removeOrderStatusWrong() throws SQLException {
        int assetID = 5;

        remAsset.removeAsset(assetID);
    }


    // update quantity
    //      - check asset exists
    //      - check asset does not exist
    //      - check negative quantity

    /**
     * Update the quantity of an asset.
     */
    @Test
    /* Test passed */
    public void H_updateQuantity() {
        int assetID = 1;
        int quantity = 100;

        asset.updateQuantity(assetID, quantity);
    }

    /**
     * Update the quantity of an asset that does not exist.
     */
    @Test
    /* Test passed */
    public void I_updateQuantityNotExists() {
        int assetID = 100;
        int quantity = 20;

        asset.updateQuantity(assetID, quantity);
    }

    /**
     * Update the quantity of an asset to a negative.
     */
    @Test
    /* Test passed */
    public void J_updateQuantityNegative() {
        int assetID = 1;
        int quantity = -10;

        asset.updateQuantity(assetID, quantity);
    }


    // get asset
    //      - if it exists, it returns it
    //      - if not exists, it is null

    /**
     * Get an asset.
     */
    @Test
    /* Test passed */
    public void K_getAssetExists() {
        Asset myAsset;
        int assetID = 1;

        myAsset = Asset.getAsset(assetID);
        if (myAsset == null) {
            fail();
        }
    }

    /**
     * Get an asset that does not exist.
     */
    @Test
    /* Test passed */
    public void L_getAssetNotExists() {
        Asset myAsset;
        int assetID = 100;

        myAsset = Asset.getAsset(assetID);
        if (myAsset != null) {
            fail();
        }
    }


    // get asset id
    //      - if it exists, it returns ID
    //      - if not exists, it is null

    /**
     * Get an asset ID.
     * @throws SQLException when something goes wrong with the database.
     */
    @Test
    /* Test passed */
    public void M_getAssetIDExists() throws SQLException {
        String assetName = "Asset";
        Integer assetID;

        assetID = Asset.getAssetID(assetName);
        if (assetID == null) {
            Assertions.fail();
        }

        System.out.println(assetID);
    }

    /**
     * Get an asset ID that does not exist.
     * @throws SQLException when something goes wrong with the database.
     */
    @Test
    /* Test passed */
    public void N_getAssetNotExists() throws SQLException {
        String assetName = "Asset Not Exists";
        Integer assetID;

        assetID = Asset.getAssetID(assetName);
        if (assetID != null) {
            Assertions.fail();
        }

        System.out.println(assetID);
    }


    // get inventory
    //      - check if exists
    //      - check if not exists

    /**
     * Get an asset quantity/inventory.
     */
    @Test
    /* Test passed */
    public void O_getInvExists() {
        Integer inventory;
        int assetID = 1;

        inventory = Asset.getInventory(assetID);
        if (inventory == null) {
            fail();
        }

        System.out.println(inventory);
    }

    /**
     * Get an asset quantity/inventory that does not exist.
     */
    @Test
    /* Test passed */
    public void P_getInvNotExists() {
        Integer inventory;
        int assetID = 100;

        inventory = Asset.getInventory(assetID);
        if (inventory != null) {
            fail();
        }
    }


    // get order asset
    //      - check asset exists
    //      - check asset does not exist
    //      - check if orders
    //      - check if no orders

    /**
     * Get all orders on an asset.
     * @throws SQLException when something goes wrong with the database.
     */
    @Test
    /* Test passed */
    public void Q_ordersExist() throws SQLException {
        ArrayList<Integer> orderIDs;
        int assetID = 1;

        orderIDs = Order.getAssetOrders(assetID);
        System.out.println(orderIDs);
    }

    /**
     * Get all orders on an asset, the asset has no orders..
     * @throws SQLException when something goes wrong with the database.
     */
    @Test
    /* Test passed */
    public void R_ordersNotExists() throws SQLException {
        ArrayList<Integer> orderIDs;
        int assetID = 4;

        orderIDs = Order.getAssetOrders(assetID);
        System.out.println(orderIDs);
    }

    /**
     * Get all orders on an asset that does not exist.
     * @throws SQLException when something goes wrong with the database.
     */
    @Test
    /* Test passed */
    public void S_orderAssetNotExist() throws SQLException {
        ArrayList<Integer> orderIDs;
        int assetID = 100;

        orderIDs = Order.getAssetOrders(assetID);
        System.out.println(orderIDs);
    }


    // asset exists
    //      - check if asset exists - returns true
    //      - check if asset does not exist - returns false

    /**
     * Check asset exists.
     */
    @Test
    /* Test passed */
    public void T_assetExistsTrue() {
        Boolean exists;
        String name = "Asset";

        exists = asset.assetExists(name);
        if (!exists) {
            fail();
        }

        System.out.println(exists);
    }

    /**
     * Check asset does not exist.
     */
    @Test
    /* Test passed */
    public void U_assetExistsFalse() {
        Boolean exists;
        String name = "Does Not Exist";

        exists = asset.assetExists(name);
        if (exists) {
            fail();
        }

        System.out.println(exists);
    }


    // get org assets
    //      - if org exists
    //      - if org does not exist

    /**
     * Get all assets of an organisation.
     * @throws SQLException when something goes wrong with the database.
     */
    @Test
    /* Test passed */
    public void V_getOrgAssetsExist() throws SQLException {
        ArrayList<String> orgAssetList = null;
        int orgUnitID = 1;

        orgAssetList = asset.getOrgAssets(orgUnitID);

        System.out.println(orgAssetList);
    }

    /**
     * Get all assets of an organisation, the organisation has none.
     * @throws SQLException when something goes wrong with the database.
     */
    @Test
    /* Test passed */
    public void W_getOrgAssetsNotExist() throws SQLException {
        ArrayList<String> orgAssetList = null;
        int orgUnitID = 100;

        orgAssetList = asset.getOrgAssets(orgUnitID);

        System.out.println(orgAssetList);
    }


    // get org asset quantity
    //      - if org and asset exists
    //      - if org does not exist
    //      - if asset does not exist

    /**
     * Get the quantity of an asset.
     * @throws SQLException when something goes wrong with the database.
     */
    @Test
    /* Test passed */
    public void X_getAssetQtyExist() throws SQLException {
        int quant;
        int orgUnitID = 1;
        int assetID = 1;

        quant = asset.getOrgAssetQty(orgUnitID, assetID);
        if (quant == 0) {
            fail();
        }

        System.out.println(quant);
    }

    /**
     * Get the quantity of an asset where the organisation does not exist.
     * @throws SQLException when something goes wrong with the database.
     */
    @Test
    /* Test passed */
    public void Y_qtyOrgNotExist() throws SQLException {
        int quant;
        int orgUnitID = 100;
        int assetID = 1;

        quant = asset.getOrgAssetQty(orgUnitID, assetID);
        if (quant != 0) {
            fail();
        }

        System.out.println(quant);
    }

    /**
     * Get the quantity of an asset where the asset does not exist.
     * @throws SQLException when something goes wrong with the database.
     */
    @Test
    /* Test passed */
    public void Z_qtyAssetNotExist() throws SQLException {
        int quant;
        int orgUnitID = 1;
        int assetID = 100;

        quant = asset.getOrgAssetQty(orgUnitID, assetID);
        if (quant != 0) {
            fail();
        }

        System.out.println(quant);
    }


    // update asset
    //      - asset exists
    //      - asset does not exist

    /**
     * Update an asset.
     * @throws SQLException when something goes wrong with the database.
     */
    @Test
    /* Test passed */
    public void Z1_updateAssetExists() throws SQLException {
        String name = "Edited Name";
        int qty = 500;
        int org = 1;
        int assetID = 1;

        Asset.updateAsset(name, qty, org, assetID);
    }

    /**
     * Update an asset that does not exist.
     * @throws SQLException when something goes wrong with the database.
     */
    @Test
    /* Test passed */
    public void Z2_updateAssetNotExists() throws SQLException {
        String name = "Edited Name";
        int qty = 500;
        int org = 1;
        int assetID = 100;

        Asset.updateAsset(name, qty, org, assetID);
    }

}
