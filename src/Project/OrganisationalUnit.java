package Project;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class holds all the functionality and information for the organisation units.
 */
public class OrganisationalUnit {

    // Constants //

    private static final String INSERT_ORG_UNIT = "INSERT INTO organisations (OrganisationName, Credits) VALUES (?, ?);";
    private static final String UPDATE_CREDITS = "UPDATE organisations SET Credits = ? WHERE OrganisationId = ?";
    private static final String GET_CREDITS = "SELECT * FROM organisations WHERE OrganisationId=?";
    private static final String GET_ORG_UNIT = "SELECT * FROM organisations WHERE OrganisationID=?";
    private static final String GET_ORG_ID = "SELECT * FROM users WHERE UserID=?";
    private static final String ORG_UNIT_EXISTS = "SELECT * FROM organisations WHERE OrganisationName=?";
    private static final String GET_ORG = "SELECT OrganisationId FROM organisations WHERE OrganisationName=?;";
    private static final String GET_ALL_UNITS = "SELECT * FROM organisations;";
    private static final String GET_USER_ORG = """
            SELECT organisations.OrganisationName FROM users
            RIGHT JOIN organisations ON users.OrganisationId=organisations.OrganisationId
            WHERE users.UserName = ?;""";
    private static final String UPDATE_PENDING = "UPDATE organisations SET Pending = ? WHERE OrganisationId = ?";
    private static final String GET_PENDING = "SELECT * FROM organisations WHERE OrganisationId=?";
    private static final String GET_USERS = " SELECT * from users where organisationId = ?;";


    // Variables //

    String name;
    double credits;
    int intCred;
    int pendingCred;

    // Database connection
    private static final Connection connection = DBConnection.getConn();


    // Methods //

    /**
     * Empty organisational constructor for the GUI.
     */
    public OrganisationalUnit() {
        // Empty constructor
    }

    /**
     * Organisational Unit Constructor without pending credits.
     *
     * @param name The name of the organisational unit.
     * @param credits The credit balance the organisational unit has.
     */
    public OrganisationalUnit(String name, double credits) {
        this.name = name;
        this.credits = credits;
    }

    /**
     * Organisational Unit Constructor with pending credits.
     *
     * @param name The name of the organisational unit.
     * @param intCred The credit balance the organisational unit has.
     * @param pendingCred The pending credits balance.
     */
    public OrganisationalUnit(String name, int intCred, int pendingCred) {
        this.name = name;
        this.intCred = intCred;
        this.pendingCred = pendingCred;
    }

    /**
     * Adds a created organisational unit to the database.
     *
     * @param myOrgUnit The organisational unit to add.
     */
    public void addOrgUnit(OrganisationalUnit myOrgUnit) {
        // Variables
        PreparedStatement addOrgUnit;
        Boolean exists;

        // Check the org unit does not already exist
        exists = orgUnitExists(name);
        if (exists) {
            // Org unit already exists, do not need to create a new one
            System.out.println("The org unit could not be created - org unit already exists.");
            return;
        }

        // Add new org unit
        try {
            addOrgUnit = connection.prepareStatement(INSERT_ORG_UNIT);

            addOrgUnit.setString(1, myOrgUnit.name);
            addOrgUnit.setString(2, Double.toString(myOrgUnit.credits));
            addOrgUnit.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Updates the credits of an organisational unit in the database.
     *
     * @param orgUnitID The ID of the organisational unit to update.
     * @param newCredits The new credit balance.
     */
    public static void updateCredits(int orgUnitID, double newCredits) {
        // Variables
        PreparedStatement updateCredits;
        OrganisationalUnit myOrgUnit;

        // Check not negative
        if (newCredits < 0) {
            // Credits negative, do not update
            System.out.print("Credits negative.");
            return;
        }

        // Check that org unit exists
        myOrgUnit = getOrgUnit(orgUnitID);
        if (myOrgUnit == null){
            // Org unit does not exist
            System.out.print("Could not update newCredits - org unit does not exist.");
            return;
        }

        // Try to update newCredits
        try {
            updateCredits = connection.prepareStatement(UPDATE_CREDITS);

            updateCredits.setDouble(1, newCredits);
            updateCredits.setInt(2, orgUnitID);
            updateCredits.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves the organisation ID from its name for user creation.
     *
     * @param orgName The name of the organisational unit.
     * @return The ID of the organisational unit.
     * @throws SQLException when something goes wrong with the database.
     */
    public static int getOrg(String orgName) throws SQLException {
        // Variables
        final ResultSet result;
        PreparedStatement getOrg;

        // Try to get the ID
        getOrg = connection.prepareStatement(GET_ORG);

        getOrg.setString(1, orgName);
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
     * Retrieve the credit amount of an organisational unit.
     *
     * @param orgUnitID The ID of the organisational unt to retrieve.
     * @return Returns the organisational unit if it exists, otherwise returns null.
     */
    public static OrganisationalUnit getOrgUnit(int orgUnitID) {
        // Variables
        PreparedStatement getOrgUnit;
        final ResultSet result;

        // Try to return the credits
        try {
            getOrgUnit = connection.prepareStatement(GET_ORG_UNIT);

            getOrgUnit.setInt(1, orgUnitID);

            result = getOrgUnit.executeQuery();
            if (!result.next()) {
                // No organisational unit matched this ID
                return null;
            }

            return new OrganisationalUnit(
                    result.getString("OrganisationName"),
                    result.getDouble("Credits")
            );

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Uses the userID to return the organisational unit the user belongs to.
     *
     * @param userID The ID of the user to find organisational unit for.
     * @return Returns the organisational unit ID if it exists, otherwise returns null.
     */
    public static Integer getOrgUnitID(int userID) {
        // Variables
        PreparedStatement getOrgID;
        final ResultSet result;

        // Try to return the organisational unit ID
        try {
            getOrgID = connection.prepareStatement(GET_ORG_ID);

            getOrgID.setString(1, Integer.toString(userID));

            result = getOrgID.executeQuery();
            if (!result.next()) {
                // No user matched this ID
                return null;
            }

            return result.getInt("OrganisationId");

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the credit amount of an organisational unit.
     *
     * @param orgUnitID The ID of the organisational unit to get the credits from.
     * @return The credits of the organisational unit if it could be retrieved, otherwise returns null.
     */
    public static Double getCredits(int orgUnitID) {
        // Variables
        PreparedStatement getCredits;
        final ResultSet result;

        // Try to retrieve org unit
        try {
            getCredits = connection.prepareStatement(GET_CREDITS);

            getCredits.setString(1, Integer.toString(orgUnitID));

            result = getCredits.executeQuery();
            if (!result.next()) {
                // No org unit matched this ID
                return null;
            }

            return result.getDouble("Credits");

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Searches for an organisational unit in the database using its name to see if it exists.
     *
     * @param name The name of the organisational unit to search for.
     * @return Returns true if the organisational unit exists, false otherwise.
     */
    public Boolean orgUnitExists(String name) {
        // Variables
        PreparedStatement orgUnitExists;
        final ResultSet result;

        // Check if the organisational unit exists
        try {
            orgUnitExists = connection.prepareStatement(ORG_UNIT_EXISTS);

            orgUnitExists.setString(1, name);

            result = orgUnitExists.executeQuery();
            return result.next();

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Used to create table model for organisational units in the GUI - lists all current organisational units.
     *
     * @return A DefaultTableModel if the organisations could be found.
     * @throws SQLException when something goes wrong with the database.
     */
    public DefaultTableModel getAllUnits() throws SQLException {
        PreparedStatement getAllUnitData;
        getAllUnitData = connection.prepareStatement(GET_ALL_UNITS);
        final ResultSet rs;
        DefaultTableModel defaultTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        defaultTableModel.addColumn("Organisation Name");
        defaultTableModel.addColumn("Credits");
        defaultTableModel.addColumn("Pending Credits");

        rs = getAllUnitData.executeQuery();

        while (rs.next()) {
            defaultTableModel.addRow(new Object[]{rs.getString(2), rs.getBigDecimal(3), rs.getBigDecimal(4)});
        }
        rs.close();
        return defaultTableModel;
    }

    /**
     * Retrieves the organisational unit of a user.
     *
     * @param user The username of the user.
     * @return A list holding the user organisation ID.
     * @throws SQLException when something goes wrong with the database.
     */
    public ArrayList<String> getUserOrg(String user) throws SQLException {
        PreparedStatement getUserUnit;
        String temp;
        getUserUnit = connection.prepareStatement(GET_USER_ORG);
        ResultSet rs;
        getUserUnit.setString(1, user);
        rs = getUserUnit.executeQuery();
        ArrayList<String> userOrgList = new ArrayList<>();
        while (rs.next()) {
            temp = rs.getString(1);
            userOrgList.add(temp);
        }
        return userOrgList;
    }

    /**
     * Returns the pending credit amount of an organisational unit.
     *
     * @param orgUnitID The ID of the organisational unit to get the pending credits from.
     * @return The pending credits of the organisational unit if it could be retrieved, otherwise returns null.
     */
    public static Double getPending(int orgUnitID) {
        // Variables
        PreparedStatement getPending;
        final ResultSet result;

        // Try to retrieve pending credits
        try {
            getPending = connection.prepareStatement(GET_PENDING);

            getPending.setInt(1, orgUnitID);

            result = getPending.executeQuery();
            if (!result.next()) {
                // No org unit matched this ID
                return null;
            }

            return result.getDouble("Pending");

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Takes credits from an organisational unit and adds them to the pending column.
     *
     * @param orgUnitID The ID of the organisational unit to add to.
     * @param addCredits The credits to add to pending.
     */
    public static void addPending(int orgUnitID, double addCredits) {
        // Variables
        PreparedStatement updateCredits;
        OrganisationalUnit myOrgUnit;
        Double credits;
        double newCredits;
        Double pending;
        double newPending;

        // Check not negative
        if (addCredits < 0) {
            // Credits negative, do not add
            System.out.print("Add credits negative.");
            return;
        }

        // Check that org unit exists
        myOrgUnit = getOrgUnit(orgUnitID);
        if (myOrgUnit == null){
            // Org unit does not exist
            System.out.print("Could not add pending - org unit does not exist.");
            return;
        }

        // Get unit's credits
        credits = OrganisationalUnit.getCredits(orgUnitID);
        if (credits == null) {
            // The credits could be returned
            System.out.println("credits null - could not return credits from org unit.");
            return;
        }

        // Get pending credits
        pending = getPending(orgUnitID);
        if (pending == null) {
            pending = 0.0;
        }

        // Check add does not exceed current credits
        if (credits < addCredits) {
            // Credits exceed, do not remove
            System.out.print("Cannot add pending - add amount exceeds credits.");
            return;
        }

        // Add credits to pending
        newPending = pending + addCredits;

        // Remove added from credits
        newCredits = credits - addCredits;
        OrganisationalUnit.updateCredits(orgUnitID, newCredits);

        // Try to update
        try {
            updateCredits = connection.prepareStatement(UPDATE_PENDING);

            updateCredits.setDouble(1, newPending);
            updateCredits.setInt(2, orgUnitID);
            updateCredits.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Takes credits from the pending column and adds them back to the organisational unit.
     *
     * @param orgUnitID The ID of the organisational unit to add back to.
     * @param removeCredits The credits to remove from pending.
     */
    public static void revertPending(int orgUnitID, double removeCredits) {
        // Variables
        PreparedStatement updateCredits;
        OrganisationalUnit myOrgUnit;
        Double credits;
        double newCredits;
        Double pending;
        double newPending;

        // Check not negative
        if (removeCredits < 0) {
            // Credits negative, do not remove
            System.out.print("Remove credits negative.");
            return;
        }

        // Check that org unit exists
        myOrgUnit = getOrgUnit(orgUnitID);
        if (myOrgUnit == null){
            // Org unit does not exist
            System.out.print("Could not remove pending - org unit does not exist.");
            return;
        }

        // Get unit's credits
        credits = OrganisationalUnit.getCredits(orgUnitID);
        if (credits == null) {
            // The credits could be returned
            System.out.println("credits null - could not return credits from org unit.");
            return;
        }

        // Get pending credits
        pending = getPending(orgUnitID);
        if (pending == null) {
            // Credits could not be retrieved
            System.out.print("Could not retrieve pending.");
            return;
        }

        // Check remove does not exceed current pending
        if (removeCredits > pending) {
            // Credits exceed, do not remove
            System.out.print("Cannot remove pending - credits amount exceeds pending.");
            return;
        }

        // Remove credits from pending
        newPending = pending - removeCredits;

        // Add removed to credits
        newCredits = credits + removeCredits;
        OrganisationalUnit.updateCredits(orgUnitID, newCredits);

        // Try to update
        try {
            updateCredits = connection.prepareStatement(UPDATE_PENDING);

            updateCredits.setDouble(1, newPending);
            updateCredits.setInt(2, orgUnitID);
            updateCredits.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Removes spent credits from the pending column of an organisational unit.
     *
     * @param orgUnitID The ID of the organisational unit.
     * @param spentCredits The credits spent to remove from pending.
     */
    public static void removePending(int orgUnitID, double spentCredits) {
        // Variables
        PreparedStatement updateCredits;
        OrganisationalUnit myOrgUnit;
        Double pending;
        double newPending;

        // Check not negative
        if (spentCredits < 0) {
            // Credits negative, do not remove
            System.out.print("Spent credits negative.");
            return;
        }

        // Check that org unit exists
        myOrgUnit = getOrgUnit(orgUnitID);
        if (myOrgUnit == null){
            // Org unit does not exist
            System.out.print("Could not remove pending - org unit does not exist.");
            return;
        }

        // Get pending credits
        pending = getPending(orgUnitID);
        if (pending == null) {
            // Credits could not be retrieved
            System.out.print("Could not retrieve pending.");
            return;
        }

        // Check spent does not exceed current pending
        if (spentCredits > pending) {
            // Credits exceed, do not remove
            System.out.print("Cannot remove pending - spent amount exceeds pending.");
            return;
        }

        // Remove credits from pending
        newPending = pending - spentCredits;

        // Try to update
        try {
            updateCredits = connection.prepareStatement(UPDATE_PENDING);

            updateCredits.setDouble(1, newPending);
            updateCredits.setInt(2, orgUnitID);
            updateCredits.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves the information of an organisational unit using its name.
     *
     * @param orgName The name of the organisational unit.
     * @return The organisational unit if it exists, otherwise returns nul.
     */
    public static OrganisationalUnit getOrgUnit (String orgName) {
        PreparedStatement getOrgUnit;
        final ResultSet rs;

        try {
            getOrgUnit = connection.prepareStatement(ORG_UNIT_EXISTS);
            getOrgUnit.setString(1, orgName);
            rs = getOrgUnit.executeQuery();
            if (rs.next()) {
                return new OrganisationalUnit(
                        rs.getString("OrganisationName"),
                        rs.getInt("Credits"),
                        rs.getInt("Pending")
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();

        }
        return null;
    }

    /**
     * Check if an organisation has any users.
     *
     * @param orgUnitID The organisational unit to check.
     * @return True if there are user in the organisation, false otherwise.
     * @throws SQLException when something goes wrong with the database.
     */
    public static Boolean usersExist(int orgUnitID) throws SQLException {
        String temp;
        ResultSet rs;
        ArrayList<String> users = new ArrayList<>();

        PreparedStatement getUsers = connection.prepareStatement(GET_USERS);
        getUsers.setInt(1, orgUnitID);

        rs = getUsers.executeQuery();
        while(rs.next()){
            temp = rs.getString("UserName");
            users.add(temp);
        }

        // Check length of this list
        return !users.isEmpty();
    }

}







