package Project;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class holds all the functionality and information for the assets.
 */
public class Asset {

    // Constants //

    private static final String INSERT_ASSET = "INSERT INTO assets (AssetName, AssetQuantity, OrganisationId) VALUES (?, ?, ?);";
    private static final String REMOVE_ASSET = "DELETE FROM assets WHERE AssetID = ?";
    private static final String UPDATE_QUANTITY = "UPDATE assets SET AssetQuantity = ? WHERE AssetId = ?";
    private static final String GET_ASSET = "SELECT * FROM assets WHERE AssetId=?";
    private static final String GET_ASSET_ID = "SELECT * FROM assets WHERE AssetName = ?";
    private static final String GET_QUANTITY = "SELECT * FROM assets WHERE AssetId=?";
    private static final String ASSET_EXISTS = "SELECT * FROM assets WHERE AssetName=?";
    private static final String GET_ALL_ASSETS = """
            SELECT assets.assetid, assets.assetName, assets.AssetQuantity, organisations.organisationName
            FROM assets
            JOIN organisations ON assets.OrganisationId=organisations.organisationid""";

    private static final String GET_ORG_ASSETS = """
            SELECT assets.AssetName, assets.AssetQuantity, organisations.OrganisationName FROM assets
            JOIN organisations on assets.OrganisationID=organisations.OrganisationId
            Where assets.organisationId = ?""";

    private static final String GET_SPEC_ORG_ASSETS = """
            SELECT assets.AssetName, assets.AssetQuantity, organisations.OrganisationName FROM assets
            JOIN organisations on assets.OrganisationID=organisations.OrganisationId
            Where assets.organisationId = ? and assets.assetId = ?""";

    private static final String ASSET_ID = "SELECT assetId FROM assets WHERE AssetName=?;";
    private static final String UPDATE_ASSET = "UPDATE ASSETS SET ASSETNAME = ?, ASSETQUANTITY = ?, ORGANISATIONID = ? WHERE ASSETID = ?;";
    private static final String GET_ASSET_NAME = "SELECT assetName from assets where assetId = ? ;";


    // Variables //

    String name;
    int quantity;
    int orgUnitID;
    int assetId;

    // Database connection
    private static final Connection connection = DBConnection.getConn();


    // Methods //

    /**
     * Empty Asset Constructor for the GUI.
     */
    public Asset() {
        // Empty constructor
    }

    /**
     * Asset Constructor without ID, database creates the ID.
     *
     * @param name The name of the asset.
     * @param quantity The amount of the asset there is available.
     * @param orgUnitID The ID of the organisational unit to add assets to.
     */
    public Asset(String name, int quantity, int orgUnitID) {
        this.name = name;
        this.quantity = quantity;
        this.orgUnitID = orgUnitID;
    }

    /**
     * Asset Constructor, with ID.
     *
     * @param ID The ID of the asset.
     * @param name The name of the asset.
     * @param quantity The amount of the asset there is available.
     * @param orgUnitID The ID of the organisational unit to add asset to.
     */
    public Asset(int ID, String name, int quantity, int orgUnitID) {
        this.assetId = ID;
        this.name = name;
        this.quantity = quantity;
        this.orgUnitID = orgUnitID;
    }

    /**
     * Adds an asset to the database.
     *
     * @param myAsset The asset to add.
     */
    public void addAsset(Asset myAsset) {
        // Variables
        PreparedStatement addAsset;
        Boolean exists;

        // Check the asset does not already exist
        exists = assetExists(name);
        if (exists) {
            // Asset already exists, do not need to create a new one
            System.out.println("The asset could not be created - asset already exists.");
            return;
        }

        // Add new asset
        try {
            addAsset = connection.prepareStatement(INSERT_ASSET);

            addAsset.setString(1, myAsset.name);
            addAsset.setInt(2, myAsset.quantity);
            addAsset.setInt(3, myAsset.orgUnitID);
            addAsset.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Removes an asset from the database.
     *
     * @param assetID The ID of the asset to remove.
     * @throws SQLException when something goes wrong with the database.
     */
    public void removeAsset(int assetID) throws SQLException {
        // Variables
        PreparedStatement removeAsset;
        Asset myAsset;
        ArrayList<Integer> orderIDs;

        // Check if asset exists
        myAsset = Asset.getAsset(assetID);
        if (myAsset == null) {
            // Asset does not exist
            System.out.print("Could not retrieve asset - asset does not exist.");
            return;
        }

        // Check for orders on the asset
        orderIDs = Order.getAssetOrders(assetID);
        if (!orderIDs.isEmpty()) {
            // There are orders on the asset
            System.out.print("Asset cannot be removed - there are orders.");
            return;
        }

        // Try to remove asset
        try {
            removeAsset = connection.prepareStatement(REMOVE_ASSET);

            removeAsset.setString(1, Integer.toString(assetID));
            removeAsset.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Updates the quantity of an asset in the database.
     *
     * @param assetID The ID of the asset to update.
     * @param newQuantity The new quantity of assets.
     */
    public static void updateQuantity(int assetID, int newQuantity) {
        // Variables
        PreparedStatement updateQuantity;
        Asset myAsset;

        // Check not negative
        if (newQuantity < 0) {
            // Quantity negative, do not update
            System.out.print("Quantity negative.");
            return;
        }

        // Check asset exists
        myAsset = Asset.getAsset(assetID);
        if (myAsset == null){
            // The asset does not exist
            System.out.println("Could not update asset quantity - the asset does not exist.");
            return;
        }

        // Try to update quantity
        try {
            updateQuantity = connection.prepareStatement(UPDATE_QUANTITY);

            updateQuantity.setString(1, Integer.toString(newQuantity));
            updateQuantity.setString(2, Integer.toString(assetID));
            updateQuantity.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves the asset's name.
     *
     * @param assetId The ID of the asset.
     * @return The name of the asset if it exists, otherwise returns null.
     */
    public String getAssetName(int assetId) {
        // Variables
        PreparedStatement getAssetName;
        ResultSet rs;

        // Try to get name
        try {
            getAssetName = connection.prepareStatement(GET_ASSET_NAME);

            getAssetName.setInt(1, assetId);
            rs = getAssetName.executeQuery();

            if (rs.next()) {

                return rs.getString(1);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Searches for an asset in the database using the asset ID and retrieves it.
     *
     * @param assetID The ID of the asset to find.
     * @return The asset if it exists, otherwise returns null.
     */
    public static Asset getAsset(int assetID) {
        // Variables
        PreparedStatement getAsset;
        final ResultSet result;

        // Try to retrieve the asset
        try {
            getAsset = connection.prepareStatement(GET_ASSET);

            getAsset.setString(1, Integer.toString(assetID));

            result = getAsset.executeQuery();
            if (!result.next()) {
                // No asset matched this ID
                return null;
            }

            return new Asset(
                    result.getInt("AssetId"),
                    result.getString("AssetName"),
                    result.getInt("AssetQuantity"),
                    result.getInt("OrganisationID")
            );

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Searches for an asset in the database and retrieves its ID.
     *
     * @param assetName The asset name to retrieve ID for.
     * @return The asset ID if it exists, otherwise returns null.
     */
    public static Integer getAssetID(String assetName) {
        // Variables
        PreparedStatement getAssetID;
        final ResultSet result;

        // Try to retrieve ID
        try {
            getAssetID = connection.prepareStatement(GET_ASSET_ID);

            getAssetID.setString(1, assetName);

            result = getAssetID.executeQuery();
            if (!result.next()) {
                // No asset matched this name
                return null;
            }

            return result.getInt("AssetId");

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the inventory of an asset.
     *
     * @param assetID The ID of the asset to retrieve inventory.
     * @return The inventory of the asset if it could be retrieved, otherwise returns null.
     */
    public static Integer getInventory(int assetID) {
        // Variables
        PreparedStatement getQuantity;
        final ResultSet result;

        // Try to retrieve inventory
        try {
            getQuantity = connection.prepareStatement(GET_QUANTITY);

            getQuantity.setString(1, Integer.toString(assetID));

            result = getQuantity.executeQuery();
            if (!result.next()) {
                // No asset matched this ID
                return null;
            }

            return result.getInt("AssetQuantity");

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Searches for an asset in the database using its name to see if it exists.
     *
     * @param assetName The name of the asset to search for.
     * @return Returns true if the asset exists, false otherwise.
     */
    public Boolean assetExists(String assetName) {
        // Variables
        PreparedStatement assetExists;
        final ResultSet result;

        // Check if asset exists
        try {
            assetExists = connection.prepareStatement(ASSET_EXISTS);

            assetExists.setString(1, assetName);

            result = assetExists.executeQuery();
            return result.next();

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Used to list all user data in a table under Admin setting for user creation in the GUI.
     *
     * @return A DefaultTableModel if the assets could be found.
     * @throws SQLException when something goes wrong with the database.
     */
    public DefaultTableModel getAllAssets() throws SQLException {

        // Variables
        ResultSet rs;
        PreparedStatement getAllAssets;
        DefaultTableModel assetTableModel;
        assetTableModel = new DefaultTableModel(){@Override
        public boolean isCellEditable(int row, int column) {
            //all cells false
            return false;
        }};

        // Try to get assets
        assetTableModel.addColumn("Asset ID");
        assetTableModel.addColumn("Asset Name");
        assetTableModel.addColumn("Quantity");
        assetTableModel.addColumn("Owner");

        getAllAssets = connection.prepareStatement(GET_ALL_ASSETS);
        rs = getAllAssets.executeQuery();

        // Add assets to the table
        while(rs.next()){

            assetTableModel.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)

        });
        rs.close();
        }
        return assetTableModel;
    }

    /**
     * Retrieves the assets of an organisation.
     *
     * @param  orgId The organisation ID to get assets for.
     * @return lists of assets that the organisation has.
     * @throws SQLException when something goes wrong with the database.
     * */
    public ArrayList<String>getOrgAssets(int orgId) throws SQLException {
        String temp;
        ResultSet rs;
        PreparedStatement getOrgAssets = connection.prepareStatement(GET_ORG_ASSETS);
        getOrgAssets.setInt(1,orgId);
        rs = getOrgAssets.executeQuery();
        ArrayList<String> orgAssetList = new ArrayList<>();
        while(rs.next()){
            temp = rs.getString(1);
            orgAssetList.add(temp);
        }
        return orgAssetList;
    }

    /**
     * Retrieves all assets in the database.
     *
     * @return lists of assets that the organisation has
     * @throws SQLException when something goes wrong with the database.
     */
    public ArrayList<String>getAssetList() throws SQLException {
        String temp;
        ResultSet rs;
        PreparedStatement getOrgAssets = connection.prepareStatement(GET_ALL_ASSETS);
        rs = getOrgAssets.executeQuery();
        ArrayList<String> assetList = new ArrayList<>();
        while(rs.next()){
            temp = rs.getString(2);
            assetList.add(temp);
        }
        return assetList;
    }


    /**
     * Retrieves the asset quantity for an organisation.
     *
     * @param orgId The organisation ID.
     * @param assetId The asset ID.
     * @return The quantity of the asset, otherwise 0.
     */
    public int getOrgAssetQty(int orgId, int assetId)  {
        ResultSet rs;
        PreparedStatement getOrgAssetQty;// = null;
        try {
            getOrgAssetQty = connection.prepareStatement(GET_SPEC_ORG_ASSETS);
            getOrgAssetQty.setInt(1,orgId);
            getOrgAssetQty.setInt(2,assetId);
            rs = getOrgAssetQty.executeQuery();
            if(rs.next()){
                return rs.getInt("AssetQuantity");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return 0;
    }

    /**
     * Retrieves the asset ID from its name for order creation.
     *
     * @param assetName The name of the organisational unit.
     * @return The ID of the organisational unit.
     * @throws SQLException when something goes wrong with the database.
     */
    public static int assetId(String assetName) throws SQLException {
        // Variables
        final ResultSet result;
        PreparedStatement getOrg;

        // Try to get the ID
        getOrg = connection.prepareStatement(ASSET_ID);

        getOrg.setString(1, assetName);
        getOrg.executeQuery();

        result = getOrg.executeQuery();
        if (result.next()) {
            // Return the ID
            return result.getInt(1);
        }

        // No unit with this name was found
        return 0;
    }

    /**
     * Updates all the information of an asset - some information may stay the same.
     *
     * @param name The name to update to.
     * @param qty The quantity to update to.
     * @param org The organisation unit ID to update to.
     * @param assetId The ID of the asset to update.
     * @throws SQLException when something goes wrong with the database.
     */
    public static void updateAsset(String name, int qty, int org, int assetId) throws SQLException {
        PreparedStatement updateAsset;
        updateAsset = connection.prepareStatement(UPDATE_ASSET);
        updateAsset.setString(1,name);
        updateAsset.setInt(2, qty);
        updateAsset.setInt(3, org);
        updateAsset.setInt(4, assetId);
        updateAsset.executeQuery();
    }

}




