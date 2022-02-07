package Project.UnitTesting;
import static org.junit.jupiter.api.Assertions.*;

import Project.OrganisationalUnit;
import Project.User;
import Project.Admin;
import Project.Order;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.AfterTest;
//import org.testng.annotations.BeforeClass;
//import org.junit.Test;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class hold the unit testing methods for Admin.
 */
public class AdminTest {
    private Admin admin = new Admin();
    private User user = new User ("adminTest", "adminTest", "adminTest2",
            1,1,"password");

    /**
     * Create a user.
     */
    @Test
        /*Test Passed*/
    public void createUser(){
        admin.createUser("adminTest2", "adminTest2", "adminTest2",
                "test",1,1);
        System.out.print(admin.getUserID("adminTest2"));
        try {
            admin.removeUser("adminTest2");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Create an organisation.
     */
    @Test
        /*Test Passed*/
    public void createOrgUnit(){
        String orgName = "organisationalTest";
        Double credits = 1500.0;
        admin.createOrgUnit(orgName,credits);
        int id =0;
        try {
            id = OrganisationalUnit.getOrg("organisationalTest");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        admin.removeOrgUnit(id);

    }

    /**
     * Edit an organisation's credits.
     */
    @Test
        /*Test Passed*/
    public void editUnitCredits(){
        String orgName = "organisationalTest";
        Double credits = 1500.0;
        admin.createOrgUnit(orgName,credits);
        int id = -1;
        try {
            id = OrganisationalUnit.getOrg("organisationalTest");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        admin.editUnitCredits(id, 3000);
        assertEquals(3000.0,OrganisationalUnit.getCredits(id));
        admin.removeOrgUnit(id);

    }

    /**
     * Add an asset.
     */
    @Test
        /*Test Passed*/
    public void addAsset(){
        admin.addAsset("chairs", 10, 1);
    }

    /**
     * Set the password of a user.
     */
    @Test
        /*Test Passed*/
    public void setPassword(){
        admin.createUser("adminTest2", "adminTest2", "adminTest",
                "test",1,1);
        admin.setUserPassword("adminTest","adminTest");
        boolean check = false;
        try {
            check = admin.userLoginValidation( "adminTest", "adminTest");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            admin.removeUser("adminTest");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assertTrue(check);
    }

    /**
     * Edit a user.
     */
    @Test
    /*Test Passed*/
    public void editUser(){
        admin.createUser("adminTest3", "adminTest3", "adminTest3",
                "test",1,1);
        admin.editUser("editedTest","editedTest","adminTest3",1,1,"adminTest3");
        try {
            admin.getThisUser("adminTest3");
            admin.removeUser("adminTest3");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assertEquals("editedTest",admin.getFirstName() );
    }

    /**
     * Set a user's organisation.
     */
    @Test
    /*Test Passed*/
    public void setUserOrgUnit(){
        admin.createUser("adminTest3", "adminTest3", "adminTest3",
                "test",1,1);
        int userID = admin.getUserID("adminTest3");
        admin.createOrgUnit("organisationalTest",1500.0);
        int id = -1;
        try {
            id = OrganisationalUnit.getOrg("organisationalTest");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        admin.setUserOrgUnit(userID,id);
        assertEquals(admin.getUserOrgId("adminTest3"),id);
        try {
            admin.removeUser("adminTest3");
            admin.removeOrgUnit(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /**
     * Remove a user.
     * @throws SQLException when something goe wrong in the database.
     */
    @Test
        /*Test Passed*/
    public void removeUser() throws SQLException {
        admin.createUser("adminTest3", "adminTest3", "adminTest3",
                "test",1,1);
        admin.removeUser("adminTest3");
        assertFalse(admin.userNameExists("adminTest3"));
    }

    /**
     * Remove an organisation unit.
     */
    @Test
    /*Test Passed*/
    public void removeOrgUnit(){
        OrganisationalUnit test = new OrganisationalUnit("test",1500);
        test.addOrgUnit(test);
        int id =0;
        try {
            id = OrganisationalUnit.getOrg("test");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        admin.removeOrgUnit(id);
        id=0;
        try {
            id = OrganisationalUnit.getOrg("test");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        assertEquals(0,id);
    }

}
