package Project;

import java.util.ArrayList;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class holds all the functionality for the admin user; it is a subclass of the User class.
 */
public class Admin extends User {

    // Constants //

    // SQL QUERIES
    private static final String GET_USERNAME = "SELECT UserName from USERS WHERE UserName = ?";
    //User Creation
    private static final String INSERT_USER = "INSERT INTO users (FirstName, LastName, UserName, PASSWORD, OrganisationId, PermID) VALUES (?,?,?,?,?,?)";
    private static final String UPDATE_USER = "UPDATE users SET FirstName =?, LastName = ?, UserName = ?, OrganisationId =?, PermId =? WHERE UserName = ?;";
    //Account Validation
    private static final String GET_PASSWORD = "SELECT PASSWORD from USERS WHERE UserName = ?";
    //Update Asset credits
    private static final String UPDATE_CREDITS = "UPDATE organisations SET Credits = ? WHERE OrganisationId = ? ";
    //Remove User
    private static  final String REMOVE_USER = "DELETE FROM users WHERE UserName = ?";
    private static final String UPDATE_PASSWORD = "UPDATE users SET PASSWORD = ? WHERE UserName= ? ";
    private static final String UPDATE_ORG_UNIT = "UPDATE users SET OrganisationId = ? WHERE UserId = ? ";
    private static final String REMOVE_UNIT = "DELETE FROM organisations WHERE OrganisationId = ?";


    // Variables //

    //SQL Statements
    private PreparedStatement addUser;
    private PreparedStatement getPassword;
    private PreparedStatement getUserName;
    private PreparedStatement updateCredits;
    private PreparedStatement remove_User;
    private PreparedStatement updateOrgUnit;
    private PreparedStatement editUser;
    private PreparedStatement removeUnit;
    private PreparedStatement updatePassword;

    ResultSet rs = null;


    // Methods //

    /**
     * Admin Constructor to establish the DB connection.
     */
    public Admin(){
        //Database connection
        Connection conn = DBConnection.getConn();

        try {
            addUser = conn.prepareStatement(INSERT_USER);
            editUser = conn.prepareStatement(UPDATE_USER);
            getPassword = conn.prepareStatement(GET_PASSWORD);
            getUserName = conn.prepareStatement(GET_USERNAME);
            updateCredits = conn.prepareStatement(UPDATE_CREDITS);
            remove_User = conn.prepareStatement(REMOVE_USER);
            updateOrgUnit = conn.prepareStatement(UPDATE_ORG_UNIT);
            removeUnit = conn.prepareStatement(REMOVE_UNIT);
            updatePassword = conn.prepareStatement(UPDATE_PASSWORD);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Admin Constructor with ID.
     *
     * @param firstName The first name of the user.
     * @param LastName The last name of the user.
     * @param userName The username of the user.
     * @param userID The ID of the user.
     * @param orgUnitID The organisation unit ID of the user.
     * @param permissionID The permission level ID of the user.
     * @param password The password of the user.
     */
    public Admin(String firstName,String LastName, String userName, int userID,  int orgUnitID, int permissionID,
                 String password){
        super(firstName, LastName,  userName,  userID,   orgUnitID,  permissionID, password);

    }

    /**
     * Admin Constructor without ID, database creates the ID.
     *
     * @param firstName The first name of the user.
     * @param LastName The last name of the user.
     * @param userName The username of the user.
     * @param orgUnitID The organisation unit ID of the user.
     * @param permissionID The permission level ID of the user.
     * @param password The password of the user.
     */
    public Admin(String firstName,String LastName, String userName,  int orgUnitID, int permissionID,
                 String password){
        super( firstName,LastName,  userName, orgUnitID, permissionID, password);
    }

    /**
     * Admin Constructor
     *
     * @param userName The username of the user.
     * @param password The password of the user.
     */
    public Admin(String userName, String password){
        super();
    }

    /**
     * Allows admin user to create a new user and add them to the database.
     *
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param orgUnitID The organisation unit ID of the user.
     * @param accessLvl The access level of the user.
     */
    public void createUser(String firstName, String lastName,String username, String password, int orgUnitID, int accessLvl) {
        User newUser = new User(firstName, lastName, username, orgUnitID,accessLvl,password);

        //Set SQL parameters and runs the queries
        try {
            addUser.setString(1, newUser.getFirstName());
            addUser.setString(2, newUser.getLastName());
            addUser.setString(3, newUser.getUserName());
            addUser.setString(4, PasswordHash.createHash(newUser.getPassword()));
            addUser.setInt(5, newUser.getOrgUnit());
            addUser.setInt(6, newUser.getAccessLvl());
            addUser.execute();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Allows admin user to create a new organisational unit and add them to the database.
     *
     * @param name What the organisational unit is called.
     * @param credits How many credits the organisational unit has.
     */
    public void createOrgUnit(String name, double credits) {
        OrganisationalUnit newUnit = new OrganisationalUnit(name, credits);

        //adds them to the database
        newUnit.addOrgUnit(newUnit);
    }

    /**
     * Allows admin user to create a new asset for an organisational unit and add them to the database.
     *
     * @param name The ID of the organisational unit the asset belong to.
     * @param quantity The quantity of the amount of that asset
     * @param organisationalID The ID of the organisational unit that is getting a new asset.
     */
    public void addAsset(String name, int quantity, int organisationalID) {
        //Creates the new asset
        Asset newAsset =  new Asset(name, quantity,organisationalID);
        newAsset.addAsset(newAsset);
    }

    /**
     * Allows admin user to create set a user's password and adds the encrypted version of it to the database.
     *
     * @param username The User that's password is being reset of user the password belongs to.
     * @param newPassword The new password
     */
    public void setUserPassword(String username, String newPassword) {
        try{
            updatePassword.setString(1,PasswordHash.createHash(newPassword));
            updatePassword.setString(2,username);
            updatePassword.executeUpdate();
            System.out.print("updated");
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Allows admin user to edit an organisational unit's credit balance and update the information in the database.
     *
     * @param orgUnitID The ID of the organisational unit that will have its credits edited.
     * @param credits The new credit balance for the organisational unit.
     */
    public void editUnitCredits(int orgUnitID, double credits) {
        OrganisationalUnit.updateCredits(orgUnitID,credits);
    }

    /**
     * Allows admin user to set/change the organisational unit a user belongs to and add/update the information to/in
     * the database.
     *
     * @param userID The ID of the user to set/change the organisational unit.
     * @param orgUnitID The ID of the organisational unit to be set/changed to.
     */
    public void setUserOrgUnit(int userID, int orgUnitID) {
        try{
            updateOrgUnit.setString(1, Integer.toString(orgUnitID));
            updateOrgUnit.setString(2,Integer.toString(userID));
            updateOrgUnit.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Allows admin user to remove a user from the database.
     *
     * @param userName The username of the user to remove.
     * @return True if the user was removed, false otherwise.
     * @throws SQLException when something goes wrong with the database.
     */
    public boolean removeUser(String userName) throws SQLException {
        int userID;
        ArrayList<Integer> orders;

        // Get user ID
        userID = getUserID(userName);
        if (userID == -1) {
            // Could not retrieve user ID
            System.out.print("User cannot be removed - user ID could not be retrieved.");
            return false;
        }

        // Check user has no ongoing orders
        orders = Order.getUserOrders(userID);
        if (!orders.isEmpty()) {
            // User has orders
            System.out.print("User cannot be removed - user has orders.");
            return false;
        }

        // Try to remove user
        try{
                remove_User.setString(1,userName);
                remove_User.execute();
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    /**
     * Update user account info.
     *
     * @param fName The new First Name for the user.
     * @param lName The new Last Name for the user.
     * @param uName The new User Name for the user.
     * @param org   The organisational unit ID that the user will belong to.
     * @param perm  The new permission ID of the user.
     * @param tempUser The the original username, incase the username is being updated.
     */
    public void editUser(String fName, String lName, String uName, int org, int perm, String tempUser) {
        System.out.println("HI YOURE EDITING");
        System.out.println(fName + lName + uName + org + perm );
        try{
            editUser.setString(1, fName);
            editUser.setString(2, lName);
            editUser.setString(3, uName);
            editUser.setInt(4, org);
            editUser.setInt(5, perm);
            editUser.setString(6, tempUser);
            editUser.executeQuery();

        } catch (SQLException throwables) {
                throwables.printStackTrace();
        }
    }

    /**
     * Allows admin user to remove a organisational unit from the database.
     *
     * @param unitId The ID of the organisation to remove.
     */
    public void removeOrgUnit(int unitId) {
        //if (this.getAccessLvl() == 1){
            try{
                removeUnit.setString(1,Integer.toString(unitId));
                removeUnit.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        //}
    }

    /**
     * Returns the update credits variable.
     * @return Update credits.
     */
    public PreparedStatement getUpdateCredits() {
        return updateCredits;
    }

    /**
     * Sets the update credits variable.
     * @param updateCredits The value to set as.
     */
    public void setUpdateCredits(PreparedStatement updateCredits) {
        this.updateCredits = updateCredits;
    }

    /**
     * Returns the user name of the user.
     * @return Username.
     */
    public PreparedStatement getGetUserName() {
        return getUserName;
    }
}