package Project;

import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class holds all the functionality and information for the users.
 */
public class User {
    //SQL QUERIES
    private static final String GET_USERNAME = "SELECT UserName from USERS WHERE UserName = ?";
    //User Creation
    private static final String INSERT_USER = "INSERT INTO users (FirstName, LastName, UserName, PASSWORD, OrganisationId, PermID) VALUES (?,?,?,?,?,?)";
    //Account Validation
    private static final String GET_PASSWORD = "SELECT PASSWORD from USERS WHERE UserName = ?";
    //Get access lvl and orgunit
    private static final String GET_USER = """
            SELECT users.permId, organisations.OrganisationName
            FROM users
            Join organisations ON users.OrganisationId=organisations.OrganisationId
            where users.username = ?;""";

    //Grab all user accounts
    private static final String USER_ACCOUNTS = """
            SELECT users.FirstName, users.LastName, users.UserName, organisations.OrganisationName, permissions.PermType
            FROM users
            Join organisations ON users.OrganisationId=organisations.OrganisationId
            JOIN permissions ON users.PermID=permissions.PermId;""";
    //Grab user details from username
    private static final String GET_USER_DETAILS = """
            SELECT users.FirstName, users.LastName, users.UserName, organisationId, permId
            FROM users
            WHERE users.UserName = ?;""";

    private static final String GET_PERMISSIONS = "SELECT * FROM permissions";
    private static final String GET_ORGANISATIONS = "SELECT * FROM organisations";

    private static final String GET_PERMISSIONID = "SELECT permId From permissions Where PermType = ?;";
    private static final String GET_USER_PERMISSION = "SELECT PermID FROM users Where UserName = ?";
    private static final String GET_USERID = "SELECT UserId from users WHERE UserName = ?";
    private static final String GET_USER_CREDITS = """
            SELECT users.username, organisations.credits, organisations.pending FROM users
            RIGHT JOIN organisations ON users.organisationid=organisations.organisationid
            WHERE users.UserName = ?;""";

    private static final String GET_USER_ORG = "SELECT organisations.organisationName from USERS JOIN organisations on users.organisationId=organisations.organisationId WHERE users.username =?;";
    private static final String GET_ALL_USERNAME = " SELECT USERNAME FROM USERS;";
    private static final String GET_USER_ORG_ID = "SELECT organisationId from users where username = ?;";
    private static final String GET_PERM_TYPE = "SELECT permtype from permissions where permId = ?;";

    //Database connection
    private static Connection conn;

    //SQL Statements
    private PreparedStatement addUser;
    private PreparedStatement getPassword;
    private PreparedStatement getUserName;
    private PreparedStatement getUser;
    private PreparedStatement getPermissions;
    private PreparedStatement getOrganisations;
    private PreparedStatement getPermId;
    private PreparedStatement getAllUser;
    private PreparedStatement getThisUser;
    private PreparedStatement get_userId;
    private PreparedStatement getUserCredit;
    private PreparedStatement getUserPermission;
    private PreparedStatement getAllUserNames;
    private PreparedStatement getUserOrgId;
    private PreparedStatement getPermType;

    User userLogin;

    ResultSet rs = null;


    // Variables //

    String firstName;
    String lastName;
    String userName;
    int userID;
    String password;
    int permissionID;
    int orgUnitID;
    String orgName;
    //String accessLevel;
    BigDecimal credits;
    BigDecimal pending;


    // Methods //

    //Database connection for User class functionality
    /**
     * User Constructor for database connection and preparing statements for database functionality.
     */
    public User() {
        // Database connection
        conn = DBConnection.getConn();

        try {
            // Set prepare statements
            addUser = conn.prepareStatement(INSERT_USER);
            getPassword = conn.prepareStatement(GET_PASSWORD);
            getUserName = conn.prepareStatement(GET_USERNAME);
            getUser = conn.prepareStatement(GET_USER);
            getPermissions = conn.prepareStatement(GET_PERMISSIONS);
            getOrganisations = conn.prepareStatement(GET_ORGANISATIONS);
            getPermId = conn.prepareStatement(GET_PERMISSIONID);
            getAllUser = conn.prepareStatement(USER_ACCOUNTS);
            getThisUser = conn.prepareStatement(GET_USER_DETAILS);
            get_userId = conn.prepareStatement(GET_USERID);
            getUserCredit = conn.prepareStatement(GET_USER_CREDITS);
            getUserPermission = conn.prepareStatement(GET_USER_PERMISSION);
            getAllUserNames = conn.prepareStatement(GET_ALL_USERNAME);
            getUserOrgId = conn.prepareStatement(GET_USER_ORG_ID);
            getPermType = conn.prepareStatement(GET_PERM_TYPE);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * User Constructor with ID - Creates a new user object with a user ID set.
     *
     * @param firstName    The first name of the user.
     * @param LastName     The last name of the user.
     * @param userName     The username of the user.
     * @param userID       The ID of the user.
     * @param orgUnitID    The ID of the organisational unit the user belongs to.
     * @param permissionID The access level of the user.
     * @param password     The password of the user.
     */
    public User(String firstName,String LastName, String userName, int userID,  int orgUnitID, int permissionID,
                String password) {
        this.firstName = firstName;
        this.lastName = LastName;
        this.userName = userName;
        this.userID = userID;
        this.orgUnitID = orgUnitID;
        this.permissionID = permissionID;
        this.password = password;
    }

    /**
     * User Constructor without ID, database creates the ID - Creates a new user object with a user ID set with out the user ID.
     *
     * @param firstName    The first name of the user.
     * @param LastName     The last name of the user.
     * @param userName     The username of the user.
     * @param orgUnitID    The ID of the organisational unit the user belongs to.
     * @param permissionID The access level of the user.
     * @param password     The password of the user.
     */
    public User(String firstName,String LastName, String userName,  int orgUnitID, int permissionID,
                String password){

        this.firstName = firstName;
        this.lastName = LastName;
        this.userName = userName;
        this.orgUnitID = orgUnitID;
        this.permissionID = permissionID;
        this.password = password;

    }

    /**
     * User Constructor without ID, database creates the ID - Mainly used for user login.
     *
     * @param userName      The first name of the user.
     * @param password      The last name of the user.
     * @param permissionID  The ID of the organisational unit the user belongs to.
     * @param organisation  The access level of the user.
     */
    public User(String userName, String password, int permissionID, String organisation) {
        this.userName = userName;
        this.password = password;
        this.permissionID = permissionID;
        this.orgName = organisation;
    }

    /**
     * Adds user into the database and hashes password
     * @param u User object to add.
     */
    public void addUser(User u) {
        try {
            addUser.setString(1, u.getFirstName());
            addUser.setString(2, u.getLastName());
            addUser.setString(3, u.getUserName());
            addUser.setString(4, PasswordHash.createHash(u.getPassword()));
            addUser.setInt(5, u.getOrgUnit());
            addUser.setInt(6, u.getAccessLvl());
            addUser.execute();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Permissions name from its ID.
     *
     * @param permId The id of the permission.
     * @return The permission type, otherwise returns null.
     * @throws SQLException when something goes wrong with the database.
     */
    public String getPermType(int permId) throws SQLException {
        getPermType.setInt(1, permId);
        rs = getPermType.executeQuery();
        if(rs.next()){
            return rs.getString(1);
        }
        return null;
    }

    /***
     * User Login - Searches the username and check if password matches.
     *
     * @param username Username to check is contained in the database
     * @param password Password to check against password stored as hash within database
     * @return True if login details provided where valid, false otherwise.
     * @throws SQLException when something goes wrong with the database.
     */
    public boolean userLoginValidation(String username, String password) throws SQLException {
        // for comparing with user input might not need the username one
        String tempPassword = null;
        String tempUsername;
        userLogin = new User(userName, password, permissionID, orgName);

        try {
            // Set user input for getUserName sql parameter
            getUserName.setString(1, username);

            rs = getUserName.executeQuery();


            //Grabs username from database and stores into tempUsername
            if (rs.next()) {
                tempUsername = rs.getString((1));
                rs.close();
                //Set user input for getPassword SQL parameter
                getPassword.setString(1, tempUsername);
                rs = getPassword.executeQuery();
                //If username exists, Grabs password from database associated with username and store into temp
                if (rs.next()) {
                    tempPassword = rs.getString(1);

                }

                //validates where password are the matching
                assert tempPassword != null;
                if (PasswordHash.validatePassword(password, tempPassword)) {
                    System.out.println("SUCCESS");

                    //grab user info
                    getUser.setString(1, tempUsername);
                    rs = getUser.executeQuery();
                    if (rs.next()) {
                        orgName = rs.getString(2);
                        permissionID = rs.getInt(1);

                    }

                    System.out.print(getUserName());

                    return true;


                } else {
                    System.out.println("WRONG PASSWORD");
                }
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    /**
     * Retrieves the all the types of permissions from the database.
     * Grabs all permission types to be used under dropdown option in user creations.
     *
     * @return An arraylist of strings containing the different type of permissions levels.
     * @throws SQLException when something goes wrong with the database.
     */
    public ArrayList<String> getAllPermissions() throws SQLException {
        String temp;
        rs = getPermissions.executeQuery();
        ArrayList<String> permissionList = new ArrayList<>();
        while (rs.next()) {
            temp = rs.getString(2);
            permissionList.add(temp);
        }
        return permissionList;
    }

    /**
     * Retrieves all organisation units to be used under dropdown option in user creations.
     *
     * @return An arraylist of strings containing the names of all the different organisations in the database.
     * @throws SQLException when something goes wrong with the database.
     */
    public ArrayList<String> getAllOrganisations() throws SQLException {
        String temp;

        rs = getOrganisations.executeQuery();
        ArrayList<String> organisationList = new ArrayList<>();
        while (rs.next()) {
            temp = rs.getString(2);
            organisationList.add(temp);
        }
        return organisationList;
    }

    /**
     * Retrieves all usernames that exists in the system.
     *
     * @return An arraylist of strings containing the names of all users in the database.
     * */
    public ArrayList<String> getAllUserNames (){
        String temp;
        try {
            rs = getAllUserNames.executeQuery();
            ArrayList<String> usernameList = new ArrayList<>();
            while (rs.next()){
                temp = rs.getString(1);
                usernameList.add(temp);
            }
            return usernameList;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves the permission id associated with the given permission type.
     *
     * @param permType The permission type name.
     * @return The Permission id of the given name type.
     * @throws SQLException when something goes wrong with the database.
     */
    public int getPermId(String permType) throws SQLException {

        getPermId.setString(1, permType);
        rs = getPermId.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }

        return 0;
    }

    /**
     * Lists all user data in a table under Admin settings for user creations.
     *
     * @return Default Table Model containing all the users in the database.
     * @throws SQLException when something goes wrong with the database.
     */
    public DefaultTableModel getAllUser() throws SQLException {
        DefaultTableModel defaultTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

        defaultTableModel.addColumn("Firstname");
        defaultTableModel.addColumn("Lastname");
        defaultTableModel.addColumn("Username");
        defaultTableModel.addColumn("Organisation");
        defaultTableModel.addColumn("Access Level");
        rs = getAllUser.executeQuery();


        while (rs.next()) {

            defaultTableModel.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});

        }
        rs.close();

        return defaultTableModel;
    }

    /**
     * Updates this users information with the information found in the database.
     *
     * @param u Username of the user to retrieve from database about.
     * @throws SQLException when something goes wrong with the database.
     */
    public void getThisUser(String u) throws SQLException {
        getThisUser.setString(1, u);
        rs = getThisUser.executeQuery();
        if (rs.next()) {
            firstName = rs.getString(1);
            lastName = rs.getString(2);
            userName = rs.getString(3);
            orgUnitID = rs.getInt(4);
            permissionID = rs.getInt(5);
        }

    }

    /**
     * Checks if the username exits in the database.
     *
     * @param userNameToCheck The username to check.
     * @return True if the username exits, false otherwise.
     */
    public boolean userNameExists(String userNameToCheck) {

        try {
            getUserName.setString(1, userNameToCheck);
            rs = getUserName.executeQuery();
            if (rs.next()) {
                System.out.print("Checking Username - userName exits");
                rs.close();
                return true;

            } else {
                System.out.print("Checking Username - userName doesn't");
                rs.close();
                return false;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the ID of a user using their username.
     *
     * @param userName Username of the user to find in database.
     * @return The userID of user if found, otherwise -1.
     */
    public int getUserID(String userName) {
        try {
            get_userId.setString(1, userName);
            rs = get_userId.executeQuery();
            int tempid = -1;
            if (rs.next()) {
                tempid = rs.getInt(1);
            }
            return tempid;
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     * Set the username of a user.
     *
     * @param newUserName The username to change to.
     */
    public void setUserName(String newUserName){
        this.userName = newUserName;
    }

    /**
     * Returns the first name of the user.
     *
     * @return FirstName.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name of the user.
     *
     * @return LastName.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the ID of the organisational unit the user belongs to.
     *
     * @return The organisational unit ID.
     */
    public int getOrgUnit() {
        return orgUnitID;
    }

    /**
     * Returns the Username of the user.
     *
     * @return Username.
     */
    public String getUserName(){
        return userName;
    }

    /**
     * Returns the user's access level within the system.
     *
     * @return The access level of the user.
     */
    public int getAccessLvl() {
        return permissionID;
    }

    /**
     * Returns the access level of a user using their username.
     *
     * @param username The username of the user.
     * @return The access level.
     */
    public int getAccessLvl(String username){
        try {
            getUserPermission.setString(1, username);
            rs = getUserPermission.executeQuery();
            int temppermission = -1;
            if (rs.next()) {
                temppermission = rs.getInt(1);
            }
            return temppermission;
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     * Returns the password of the user.
     *
     * @return Password.
     */
    String getPassword(){return password;}

    /**
     * Returns the organisational unit name the user belongs to.
     *
     * @return Organisational unit's name.
     */
    String getOrgName(){return orgName;}


     //String getAccessName(){return accessLevel;}

    /**
     * User Constructor to hold user credits and pending credits
     *
     * @param credits The credit of the user's organisation unit.
     * @param pending The pending credits of the user's organisational unit.     *
     */
    public User(BigDecimal credits, BigDecimal pending){
        this.credits = credits;
        this.pending = pending;
    }

    /**
     * Returns the credits of the user's organisational unit.
     *
     * @return Credits.
     */
    public BigDecimal getCredits() {
        return credits;
    }

    /**
     * Returns the pending credits of the user's organisational unit.
     *
     * @return Pending credits.
     * */
    public BigDecimal getPending() {
        return pending;
    }

    /**
     * Retrieves available credits for a user.
     *
     * @param user Username of user whose credits are getting grabbed.
     * @return The credits available to user.
     * @throws SQLException when something goes wrong with the database.
     */
    public BigDecimal getUserCredits(String user) throws SQLException {

        getUserCredit.setString(1, user);
        rs = getUserCredit.executeQuery();
        System.out.println(getUserName());

        if(rs.next()){
            return credits = rs.getBigDecimal(2);
        }
        return null;
    }


    /**
     * Retrieves the name of the user's organisational unit.
     *
     * @param user The username of user who's organisation name is being found.
     * @return The name of the organisation that user belongs to.
     * @throws SQLException when something goes wrong with the database.
     */
    public String getUserOrg(String user) throws SQLException {
        PreparedStatement getUserOrg;
        getUserOrg = conn.prepareStatement(GET_USER_ORG);

        getUserOrg.setString(1, user);
        rs = getUserOrg.executeQuery();
        System.out.println(getUserName());

        if(rs.next()){
            return rs.getString(1);


        }
        return null;
    }

    /**
     * Retrieves the ID of the user's organisational unit.
     *
     * @param user The username of user who's organisation id is being found.
     * @return The ID of the organisation that user belongs to.
     */
    public int getUserOrgId(String user){
        try {
            getUserOrgId.setString(1, user);
            rs=getUserOrgId.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;

    }

}
