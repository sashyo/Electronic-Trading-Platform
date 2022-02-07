package Project;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


/**
 * This class holds all the functionality for the GUI and how it runs.
 */
public class GUI extends JFrame implements Runnable {

    private static boolean EditStatus = false;

    private static User userData;
    private static Admin adminData;
    private static Asset assetData;
    private static OrganisationalUnit orgData;
    private static Order orderData;
    private static Transaction transactionData;



    private static final Color SBAR_OVERLAY_COLOR = new Color(233,236,242);
    private static final Color SBAR_COLOR = new Color(228, 230, 236);

    private static final Color PAGE_COLOR = UIManager.getColor("RootPane.background");

    private static JPanel loginPanel;
    private static JSplitPane dashboardPanel;

    private static JPanel UserPage;
    private static  JPanel OrgUnitPage;
    private static JPanel AssetsPage;

    private static JTabbedPane AdminPage;
    private static JPanel AdminUsers;
    private static JPanel AdminAssets;
    private static JPanel AdminOrders;
    private static JPanel AdminUnits;
    private static JPanel AdminTransactions;

    private static JTable UnitTable;
    private static JTable UserTable;
    private static JTable AssetTable;
    private static JTable OrderTable;
    private static JTable TransactionTable;
    private static JTable nonAdminUserTable;
    private static JTable OrgDashTable;
    private static JTable MarketPlaceTable;
    private static final JTable OrgHistoryTable = new JTable();

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String APP_TITLE = "App";

    /**
     * The side bar width.
     */
    public static final int SBAR_WIDTH = 80;

    private static final Font HEADER = new Font("Segoe UI Light", Font.PLAIN, 46);

    private static final Font TITLE = new Font("Segoe UI Semilight", Font.PLAIN, 24);
    private static final Font SUBTITLE = new Font("Segoe UI", Font.PLAIN, 20);
    private static final Font SUBTITLEALT = new Font("Segoe UI", Font.PLAIN, 18);


    private static final Font CAPTION = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font CAPTIONALT = new Font("Segoe UI", Font.PLAIN, 13);

    /**
     * The text height.
     */
    public static final int TEXT_HEIGHT = 26;

    /**
     * Timer refresh rate to automate table refresh, order status updates, and transactions.
     */
    public final static int ONE_MINUTE = 60000;
    private static Timer timer;
    private static Asset selectedAsset;
    private static String currentUser;
    private static int selectedUserID;
    private static OrganisationalUnit selectedUnit;
    private static Order selectedOrder;
    private static Transaction selectedTransaction;

    /**
     * GUI Constructor to set title.
     *
     * @param title The title of the GUI window.
     * @throws HeadlessException when something goes wrong.
     */
    public GUI(String title) throws HeadlessException {
        super(title);
    }

    // ==== MAIN ==== //

    /**
     * The main program to run the gui and run timer to automate functionality.
     *
     * @param args The parsed arguments.
     */
    public static void main(String[] args) {

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code">
        /* If Windows (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        //</editor-fold>

        UnitTable = new JTable();
        UserTable = new JTable();
        AssetTable = new JTable();
        OrderTable = new JTable();
        TransactionTable = new JTable();
        OrgDashTable = new JTable();

        //timer to sync gui tables with database every minute
        timer = new Timer(ONE_MINUTE, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    UserTable.setModel(userData.getAllUser());
                    AssetTable.setModel(assetData.getAllAssets());
                    UnitTable.setModel(orgData.getAllUnits());
                    OrgDashTable.setModel(orderData.getAllOrgOrders(userData.getUserOrg(currentUser)));
                    MarketPlaceTable.setModel(orderData.getAllDashOrders());
                    TransactionTable.setModel(transactionData.getAllTransactions());
                    OrderTable.setModel(orderData.getAllOrders());
                    nonAdminUserTable.setModel(orderData.getAllUserOrders(userData.getUserID(currentUser)));

                    Order.automateTransactions();
                    Order.automateStatusUpdate();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        timer.start();

        GUI gui = new GUI(APP_TITLE);
        SwingUtilities.invokeLater(gui);
    }

    /**
     * Runs the GUI.
     */
    @Override
    public void run() {
        createGUI();
    }

    // ==== GUI SETUP & DISPLAY ==== //
    private void createGUI() {
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setLocationRelativeTo(null);

        loginPanel = new JPanel();
        dashboardPanel = new JSplitPane();

        initLogin();
        setVisible(true);
    }

    // <editor-fold defaultstate="collapsed" desc="Login Page">//
    private void initLogin(){
        // Constant declaration
        // Labels
        final String PASSWORD_LABEL = "Password:";
        final String EMAIL_LABEL = "User Name:";
        final String FORGOTTEN_PASSWORD = "Forgotten Password?";
        final String LOGIN = "Login";

        adminData = new Admin();
        // End of constant declaration

        // Variables declaration
        JLabel passwordLabel = new JLabel();
        JLabel emailLabel = new JLabel();

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        passwordField.addActionListener(e -> {
            if (emailField.getText().equals("") || passwordField.getText().equals("")){
                JOptionPane.showMessageDialog(this.getContentPane(), "Please fill out all inputs!","Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!(adminData.userNameExists(emailField.getText()))){
                JOptionPane.showMessageDialog(this.getContentPane(), "User DOES NOT Exists!","User Error", JOptionPane.WARNING_MESSAGE);
                return;
            }


            try {
                login(emailField.getText(), passwordField.getText());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        emailField.addActionListener(e -> {
            if (emailField.getText().equals("") || passwordField.getText().equals("")){
                JOptionPane.showMessageDialog(this.getContentPane(), "Please fill out all inputs!","Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!(adminData.userNameExists(emailField.getText()))){
                JOptionPane.showMessageDialog(this.getContentPane(), "User DOES NOT Exists!","User Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                login(emailField.getText(), passwordField.getText());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        JButton loginBtn = new JButton();
        JButton passwordBtn = new JButton();

        passwordBtn.addActionListener(e -> JOptionPane.showMessageDialog(null, "Please contact your System Administrator to reset your password."));
        // End of Variables declaration

        passwordLabel.setText(PASSWORD_LABEL);
        emailLabel.setText(EMAIL_LABEL);

        passwordBtn.setText(FORGOTTEN_PASSWORD);
        loginBtn.setText(LOGIN);

        GroupLayout PanelLayout = new GroupLayout(loginPanel);
        loginPanel.setLayout(PanelLayout);

        PanelLayout.setHorizontalGroup(
                PanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(PanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(PanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(emailLabel)
                                        .addComponent(passwordLabel))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(PanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addGroup(PanelLayout.createSequentialGroup()
                                                .addComponent(loginBtn)
                                                .addGap(18, 18, 18)
                                                .addComponent(passwordBtn))
                                        .addComponent(passwordField)
                                        .addComponent(emailField))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelLayout.setVerticalGroup(
                PanelLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(PanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(PanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(emailLabel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(emailField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(PanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(passwordLabel))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(PanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(loginBtn)
                                        .addComponent(passwordBtn))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        loginPanel.setBorder(BorderFactory.createEtchedBorder());
        setLayout(new GridBagLayout());
        getContentPane().add(loginPanel, new GridBagConstraints());

        getContentPane().repaint();
        loginPanel.setVisible(true);

        loginBtn.addActionListener(e -> {
            if (emailField.getText().equals("") || passwordField.getText().equals("")){
                JOptionPane.showMessageDialog(this.getContentPane(), "Please fill out all inputs!","Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!(adminData.userNameExists(emailField.getText()))){
                JOptionPane.showMessageDialog(this.getContentPane(), "User DOES NOT Exists!","User Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                login(emailField.getText(), passwordField.getText());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Login Function">//
    private String login(String userName, String password) throws SQLException {
        userData = new User();
        try {
            if (userData.userLoginValidation(userName, password)){
                currentUser = userName;
                initDashboard();
                getContentPane().remove(loginPanel);
                getContentPane().add(dashboardPanel, BorderLayout.CENTER);
                getContentPane().validate();
                return currentUser;

            } else {
                JOptionPane.showMessageDialog(this.getContentPane(), "Wrong Password!","Password Error", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Login Forgotten">//

    /**
     * Holds methods for forgotten login within the GUI.
     */
    private class LogoInForgotten extends JFrame{

        private JTextField NameField;
        private JPasswordField PwdField;
        private JPasswordField PwdConfirmField;

        private Admin adminData;

        public LogoInForgotten(String title) throws HeadlessException {
            super(title);

            adminData = new Admin();

            int WIDTH = 590;
            int HEIGHT = 250;
            int CONTENT_HGAP = 5;
            int CONTENT_VGAP = 10;
            int CONTENT_WIDTH = 530;

            NameField = new JTextField();
            PwdField = new JPasswordField();
            PwdConfirmField = new JPasswordField();

            // <editor-fold defaultstate="collapsed" desc="Page Heading">//
            JLabel Heading = new JLabel("Reset Password");
            Heading.setFont(HEADER);
            Heading.setPreferredSize(new Dimension(CONTENT_WIDTH, 64));
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Details">//
            JLabel NameLabel = new JLabel("User Name: ");
            NameLabel.setFont(CAPTION);
            JPanel NamePanel = new JPanel();
            NamePanel.setLayout(new BorderLayout());
            NamePanel.add(NameLabel, BorderLayout.BEFORE_LINE_BEGINS);
            NamePanel.add(NameField, BorderLayout.CENTER);

            JLabel PwdLabel = new JLabel("New Password: ");
            PwdLabel.setFont(CAPTION);
            JPanel PwdPanel = new JPanel();
            PwdPanel.setLayout(new BorderLayout());
            PwdPanel.add(PwdLabel, BorderLayout.BEFORE_LINE_BEGINS);
            PwdPanel.add(PwdField, BorderLayout.CENTER);

            JLabel PwdConfirmLabel = new JLabel("Confirm Password: ");
            PwdConfirmLabel.setFont(CAPTION);
            JPanel PwdConfirmPanel = new JPanel();
            PwdConfirmPanel.setLayout(new BorderLayout());
            PwdConfirmPanel.add(PwdConfirmLabel, BorderLayout.BEFORE_LINE_BEGINS);
            PwdConfirmPanel.add(PwdConfirmField, BorderLayout.CENTER);


            NameLabel.setPreferredSize(new Dimension(CONTENT_WIDTH / 5, TEXT_HEIGHT));
            NameField.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_WIDTH / 5), TEXT_HEIGHT));

            PwdLabel.setPreferredSize(new Dimension(CONTENT_WIDTH / 5, TEXT_HEIGHT));
            PwdField.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_WIDTH / 5), TEXT_HEIGHT));

            PwdConfirmLabel.setPreferredSize(new Dimension(CONTENT_WIDTH / 5, TEXT_HEIGHT));
            PwdConfirmField.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_WIDTH / 5), TEXT_HEIGHT));
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Form">//
            JPanel FormPnl = new JPanel();
            FormPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 100));
            FormPnl.setLayout(new FlowLayout( FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            FormPnl.add(NamePanel);
            FormPnl.add(PwdPanel);
            FormPnl.add(PwdConfirmPanel);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Controls">//
            JButton BtnCancel = new JButton("Cancel");
            JButton BtnSubmit = new JButton("Save");

            BtnCancel.setFont(CAPTION);
            BtnSubmit.setFont(CAPTION);
            BtnSubmit.setBackground(SystemColor.activeCaption);
            BtnSubmit.setForeground(SystemColor.activeCaptionText);

            BtnCancel.addActionListener(e -> this.dispose());
            BtnSubmit.addActionListener(e-> Submit());

            JPanel ControlsPnl = new JPanel();
            ControlsPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 30));
            ControlsPnl.setLayout(new BorderLayout());
            ControlsPnl.add(BtnCancel, BorderLayout.LINE_START);
            ControlsPnl.add(BtnSubmit, BorderLayout.LINE_END);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Frame Content">//
            JPanel ContentPnl = new JPanel();
            ContentPnl.setBorder(BorderFactory.createEtchedBorder());
            ContentPnl.setMinimumSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            ContentPnl.add(Heading);
            ContentPnl.add(FormPnl);
            ContentPnl.add(ControlsPnl);

            // </editor-fold>//

            this.setLayout(new GridBagLayout());
            this.setMinimumSize(new Dimension(WIDTH + (CONTENT_VGAP * 2), HEIGHT + (CONTENT_VGAP * 5)));
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setVisible(true);
            this.setLocationRelativeTo(dashboardPanel);
            this.setAlwaysOnTop(true);

            this.getContentPane().add(ContentPnl, new GridBagConstraints());

        }

        private void Submit() {
            if (NameField.getText().equals("") || PwdField.getText().equals("") || PwdConfirmField.getText().equals("")){
                JOptionPane.showMessageDialog(this.getContentPane(), "Please fill out all inputs!","Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!PwdField.getText().equals(PwdConfirmField.getText())){
                JOptionPane.showMessageDialog(this.getContentPane(), "Passwords are not the same!","Password Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!(adminData.userNameExists(NameField.getText()))){
                JOptionPane.showMessageDialog(this.getContentPane(), "User DOES NOT Exists!","User Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            adminData.setUserPassword(NameField.getText(), PwdField.getText());
            this.dispose();
        }
    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Dashboard">//
    private void initDashboard() throws SQLException {
        adminData = new Admin();

        AdminPage = new JTabbedPane();
        UserPage = new JPanel();
        OrgUnitPage = new JPanel();
        AssetsPage = new JPanel();

        InitUserPage();
        InitAssetsPage();
        InitOrgUnitPage();

        InitAdminPage();

        int SBAR_HGAP = 10;
        int SBAR_VGAP = 5;
        int SBAR_BTN_HEIGHT = 28;
        int SBAR_BTN_WIDTH = 28;
        int SBAR_OVERLAY_WIDTH = 200;

        JPanel Content = new JPanel();
        Content.setLayout(new BorderLayout());
        Content.add(OrgUnitPage);

        // <editor-fold defaultstate="collapsed" desc="Side Bar Overlay">//
        JPanel sBarOverlay = new JPanel();
        sBarOverlay.setBackground(null);
        sBarOverlay.setOpaque(false);
        sBarOverlay.setVisible(false);
        sBarOverlay.setEnabled(false);

        JPanel sBarOverlayContent = new JPanel();
        sBarOverlayContent.setPreferredSize(new Dimension(SBAR_OVERLAY_WIDTH, Short.MAX_VALUE));
        sBarOverlayContent.setBackground(SBAR_OVERLAY_COLOR);

        JLabel overlayClose = new JLabel();
        overlayClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                sBarOverlay.setVisible(false);
                sBarOverlay.setEnabled(false);
            }
        });

        sBarOverlay.setVisible(false);
        sBarOverlay.setEnabled(false);

        JPanel sBarOverlayContent_N = new JPanel();
        JPanel sBarOverlayContent_C = new JPanel();
        JPanel sBarOverlayContent_S = new JPanel();

        sBarOverlayContent_N.setBackground(null);
        sBarOverlayContent_C.setBackground(null);
        sBarOverlayContent_S.setBackground(null);

        JLabel sBarLabel01 = new JLabel("Home");
        JLabel sBarLabel02 = new JLabel("Marketplace");
        JLabel sBarLabel03 = new JLabel("User Account");
        JLabel sBarLabelAdmin = new JLabel("Admin");
        JLabel sBarLabelLogOut = new JLabel("Log Out");
        JLabel sBarLabelRefresh = new JLabel("Refresh");


        sBarLabel01.setFont(TITLE);
        sBarLabel02.setFont(TITLE);
        sBarLabel03.setFont(TITLE);
        sBarLabelAdmin.setFont(TITLE);
        sBarLabelLogOut.setFont(TITLE);
        sBarLabelRefresh.setFont(TITLE);

        sBarLabel01.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sBarLabel02.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sBarLabel03.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sBarLabelAdmin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sBarLabelLogOut.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sBarLabelRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        sBarOverlayContent_N.setPreferredSize(new Dimension(SBAR_OVERLAY_WIDTH - (SBAR_HGAP * 2), SBAR_BTN_HEIGHT + (SBAR_VGAP * 2)));
        sBarLabel01.setPreferredSize(new Dimension(SBAR_OVERLAY_WIDTH - (SBAR_HGAP * 2), SBAR_BTN_HEIGHT));
        sBarLabel02.setPreferredSize(new Dimension(SBAR_OVERLAY_WIDTH - (SBAR_HGAP * 2), SBAR_BTN_HEIGHT));
        sBarLabel03.setPreferredSize(new Dimension(SBAR_OVERLAY_WIDTH - (SBAR_HGAP * 2), SBAR_BTN_HEIGHT));
        sBarLabelAdmin.setPreferredSize(new Dimension(SBAR_OVERLAY_WIDTH - (SBAR_HGAP * 2), SBAR_BTN_HEIGHT));
        sBarLabelLogOut.setPreferredSize(new Dimension(SBAR_OVERLAY_WIDTH - (SBAR_HGAP * 2), SBAR_BTN_HEIGHT));
        sBarLabelRefresh.setPreferredSize(new Dimension(SBAR_OVERLAY_WIDTH - (SBAR_HGAP * 2), SBAR_BTN_HEIGHT));

        sBarLabel01.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Content.removeAll();
                Content.add(OrgUnitPage);
                sBarOverlay.setVisible(false);
                sBarOverlay.setEnabled(false);
                Content.validate();
                Content.repaint();
            }
        });

        sBarLabel02.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Content.removeAll();
                Content.add(AssetsPage);
                sBarOverlay.setVisible(false);
                sBarOverlay.setEnabled(false);
                Content.validate();
                Content.repaint();
            }
        });

        sBarLabel03.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Content.removeAll();
                Content.add(UserPage);
                sBarOverlay.setVisible(false);
                sBarOverlay.setEnabled(false);
                Content.validate();
                Content.repaint();
            }
        });

        sBarLabelAdmin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Content.removeAll();
                Content.add(AdminPage);
                sBarOverlay.setVisible(false);
                sBarOverlay.setEnabled(false);
                Content.validate();
                Content.repaint();
            }


        });

        sBarLabelRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try{
                    UserTable.setModel(userData.getAllUser());
                    AssetTable.setModel(assetData.getAllAssets());
                    UnitTable.setModel(orgData.getAllUnits());
                    OrgDashTable.setModel(orderData.getAllOrgOrders(userData.getUserOrg(currentUser)));
                    MarketPlaceTable.setModel(orderData.getAllDashOrders());
                    TransactionTable.setModel(transactionData.getAllTransactions());
                    nonAdminUserTable.setModel(orderData.getAllUserOrders(userData.getUserID(currentUser)));
                    OrderTable.setModel(orderData.getAllOrders());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
        });

        sBarLabelLogOut.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if( JOptionPane.showConfirmDialog(null, "Confirm Log out") == 0){
                    dispose();
                    GUI gui = new GUI(APP_TITLE);
                    SwingUtilities.invokeLater(gui);


                }

            }

        });

        sBarOverlayContent_C.setLayout(new FlowLayout(FlowLayout.CENTER, SBAR_HGAP, SBAR_VGAP));
        sBarOverlayContent_C.add(sBarLabel01);
        sBarOverlayContent_C.add(sBarLabel02);
        sBarOverlayContent_C.add(sBarLabel03);
        sBarOverlayContent_C.add(sBarLabelRefresh);
        sBarOverlayContent_C.add(sBarLabelLogOut);

        sBarOverlayContent_S.setLayout(new FlowLayout(FlowLayout.CENTER, SBAR_HGAP, SBAR_VGAP));
        //remove admin settings option if user logged on is not Admin
        if(adminData.getAccessLvl(currentUser)==1) {
            sBarOverlayContent_S.add(sBarLabelAdmin);
        }
        sBarOverlayContent.setLayout(new BorderLayout());
        sBarOverlayContent.add(sBarOverlayContent_N, BorderLayout.NORTH);
        sBarOverlayContent.add(sBarOverlayContent_C, BorderLayout.CENTER);
        sBarOverlayContent.add(sBarOverlayContent_S, BorderLayout.SOUTH);

        sBarOverlay.setLayout(new BorderLayout());
        sBarOverlay.add(sBarOverlayContent, BorderLayout.BEFORE_LINE_BEGINS);
        sBarOverlay.add(overlayClose, BorderLayout.CENTER);
        // </editor-fold>//

        // <editor-fold defaultstate="collapsed" desc="Body">//
        JLayeredPane body = new JLayeredPane();
        body.setMinimumSize(new Dimension(WIDTH - SBAR_WIDTH, HEIGHT));

        body.setLayer(Content, javax.swing.JLayeredPane.DEFAULT_LAYER);
        body.setLayer(sBarOverlay, javax.swing.JLayeredPane.MODAL_LAYER);

        GroupLayout bodyLayout = new GroupLayout(body);
        body.setLayout(bodyLayout);
        bodyLayout.setHorizontalGroup(
                bodyLayout.createParallelGroup()
                        .addComponent(Content)
                        .addComponent(sBarOverlay)
        );
        bodyLayout.setVerticalGroup(
                bodyLayout.createParallelGroup()
                        .addComponent(Content)
                        .addComponent(sBarOverlay)
        );
        // </editor-fold>//

        // <editor-fold defaultstate="collapsed" desc="Side Bar">//
        JPanel sBar = new JPanel();
        sBar.setBackground(SBAR_COLOR);

        ImageIcon iconMenu = new ImageIcon("src/project/resources/icons/menu_23.png");
        ImageIcon iconSettings = new ImageIcon("src/project/resources/icons/settings_23.png");
        ImageIcon iconMarket = new ImageIcon("src/project/resources/icons/market_23.png");
        ImageIcon iconHome = new ImageIcon("src/project/resources/icons/home_23.png");
        ImageIcon iconAccount = new ImageIcon("src/project/resources/icons/account_23.png");
        ImageIcon iconLogout = new ImageIcon("src/project/resources/icons/logout_23.png");
        ImageIcon iconSync = new ImageIcon("src/project/resources/icons/sync_23.png");

        JButton overlayToggle = new JButton(iconMenu);
        overlayToggle.setFocusPainted(false);
        overlayToggle.setPreferredSize(new Dimension(SBAR_BTN_WIDTH, SBAR_BTN_HEIGHT));
        overlayToggle.addActionListener((e -> {
            if (sBarOverlay.isEnabled()) {
                sBarOverlay.setVisible(false);
                sBarOverlay.setEnabled(false);
            }
            else {
                sBarOverlay.setVisible(true);
                sBarOverlay.setEnabled(true);
            }
            Content.validate();
            Content.repaint();
        }));

        JButton sBarBtn01 = new JButton(iconHome);
        sBarBtn01.setFocusPainted(false);
        sBarBtn01.setPreferredSize(new Dimension(SBAR_WIDTH - (SBAR_HGAP * 2), SBAR_BTN_HEIGHT));
        sBarBtn01.addActionListener((e -> {
            Content.removeAll();
            Content.add(OrgUnitPage);
            Content.validate();
            Content.repaint();
        }));

        JButton sBarBtn02 = new JButton(iconMarket);
        sBarBtn02.setFocusPainted(false);
        sBarBtn02.setPreferredSize(new Dimension(SBAR_WIDTH - (SBAR_HGAP * 2), SBAR_BTN_HEIGHT));
        sBarBtn02.addActionListener((e -> {
            Content.removeAll();
            Content.add(AssetsPage);
            Content.validate();
            Content.repaint();
        }));

        JButton sBarBtn03 = new JButton(iconAccount);
        sBarBtn03.setFocusPainted(false);
        sBarBtn03.setPreferredSize(new Dimension(SBAR_WIDTH - (SBAR_HGAP * 2), SBAR_BTN_HEIGHT));
        sBarBtn03.addActionListener((e -> {
            Content.removeAll();
            Content.add(UserPage);
            Content.validate();
            Content.repaint();
        }));

        JButton sBarBtnSettings = new JButton(iconSettings);
        sBarBtnSettings.setFocusPainted(false);
        sBarBtnSettings.setPreferredSize(new Dimension(SBAR_WIDTH - (SBAR_HGAP * 2), SBAR_BTN_HEIGHT));
        sBarBtnSettings.addActionListener((e -> {
            Content.removeAll();
            Content.add(AdminPage);
            Content.validate();
            Content.repaint();
        }));

        JButton sBarBtnRefresh = new JButton(iconSync);
        sBarBtnRefresh.setFocusPainted(false);
        sBarBtnRefresh.setPreferredSize(new Dimension(SBAR_WIDTH - (SBAR_HGAP * 2), SBAR_BTN_HEIGHT));
        sBarBtnRefresh.addActionListener((e -> {
            try{
            UserTable.setModel(userData.getAllUser());
            AssetTable.setModel(assetData.getAllAssets());
            UnitTable.setModel(orgData.getAllUnits());
            OrgDashTable.setModel(orderData.getAllOrgOrders(userData.getUserOrg(currentUser)));
            MarketPlaceTable.setModel(orderData.getAllDashOrders());
            TransactionTable.setModel(transactionData.getAllTransactions());
            nonAdminUserTable.setModel(orderData.getAllUserOrders(userData.getUserID(currentUser)));
            OrderTable.setModel(orderData.getAllOrders());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }));

        JButton sBarBtnLogOut = new JButton(iconLogout);
        sBarBtnLogOut.setFocusPainted(false);
        sBarBtnLogOut.setPreferredSize(new Dimension(SBAR_WIDTH - (SBAR_HGAP * 2), SBAR_BTN_HEIGHT));
        sBarBtnLogOut.addActionListener(e->{
                if( JOptionPane.showConfirmDialog(null, "Confirm Log out") == 0){

                   this.removeAll();
                   dispose();
                   GUI gui = new GUI(APP_TITLE);
                   SwingUtilities.invokeLater(gui);


                }

            }
        );

        JPanel sBar_N = new JPanel();
        JPanel sBar_C = new JPanel();
        JPanel sBar_S = new JPanel();
        sBar_N.setBackground(null);
        sBar_C.setBackground(null);
        sBar_S.setBackground(null);

        sBar_N.setLayout(new FlowLayout(FlowLayout.CENTER, SBAR_HGAP,SBAR_VGAP));
        sBar_N.add(overlayToggle);

        sBar_C.setLayout(new FlowLayout(FlowLayout.CENTER, SBAR_HGAP,SBAR_VGAP));
        sBar_C.add(sBarBtn01);
        sBar_C.add(sBarBtn02);
        sBar_C.add(sBarBtn03);





        sBar_S.setLayout(new FlowLayout(FlowLayout.CENTER, SBAR_HGAP,SBAR_VGAP));
        if (adminData.getAccessLvl(currentUser)==1) {
            sBar_S.add(sBarBtnSettings);
        }


        sBar_C.add(sBarBtnRefresh,BorderLayout.SOUTH );
        sBar_C.add(sBarBtnLogOut,BorderLayout.SOUTH );



        sBar.setLayout(new BorderLayout());
        sBar.add(sBar_N, BorderLayout.NORTH);
        sBar.add(sBar_C, BorderLayout.CENTER);
        sBar.add(sBar_S, BorderLayout.SOUTH);





        // </editor-fold>//

        getContentPane().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                dashboardPanel.setDividerLocation(SBAR_WIDTH);
            }
        });

        setLayout(new BorderLayout());
        dashboardPanel.setBorder(null);
        dashboardPanel.setDividerSize(2);
        dashboardPanel.setDividerLocation(SBAR_WIDTH);
        dashboardPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        dashboardPanel.setRightComponent(body);
        dashboardPanel.setLeftComponent(sBar);
        dashboardPanel.setVisible(true);
    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Create an Order">//

    /**
     * Holds methods for creating an order within the GUI.
     */
    private class CreateOrder extends JFrame {

        private  JComboBox<String> ItemSelector;
        private  JSlider QtSlider;
        private  JTextField PriceInput;
        private  JCheckBox SellCheck;


        private int SELECTED_QT;
        private int SELECTED_PRICE;

        ArrayList<String> ASSETS = new ArrayList<>();

        public CreateOrder(String title) throws HeadlessException, SQLException {
            super(title);
            userData = new User();

            int CREATE_WIDTH = 590;
            int CREATE_HEIGHT = 450;
            int CONTENT_HGAP = 5;
            int CONTENT_VGAP = 10;
            int CONTENT_WIDTH = 530;

            // <editor-fold defaultstate="collapsed" desc="HEADING">//
            JLabel Heading = new JLabel("Create an Order");
            Heading.setFont(HEADER);
            Heading.setPreferredSize(new Dimension(CONTENT_WIDTH, 64));
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="SELECTORS">//
            ItemSelector = new JComboBox<>();
            ASSETS = new ArrayList<>(assetData.getAssetList());
            ASSETS.add(0,"-- Select an Asset --");
            ItemSelector.setModel(new DefaultComboBoxModel(ASSETS.toArray()));
            ItemSelector.setPreferredSize(new Dimension(CONTENT_WIDTH, 32));
            ItemSelector.setFont(CAPTIONALT);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="QUANTITY">//
            JPanel Qt = new JPanel();
            Qt.setBorder(BorderFactory.createEtchedBorder());

            JPanel QtTotalContainer = new JPanel();
            JLabel QtHeading = new JLabel("Quantity");
            QtHeading.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_HGAP * 2), 30));
            JLabel QtValue = new JLabel(String.valueOf(getSELECTED_QT()));
            QtValue.setPreferredSize(new Dimension(375, 20));
            JLabel QtTotal = new JLabel("Total Credits: ");
            JLabel QtTotalValue = new JLabel(String.valueOf(getTotalPrice()));
            QtHeading.setFont(SUBTITLE);
            QtValue.setFont(CAPTION);
            QtTotal.setFont(CAPTION);
            QtTotalValue.setFont(CAPTION);

            QtSlider = new JSlider();



            //QtSlider.setMaximum(200);
            QtSlider.setMinimum(1);
            //QtSlider.setValue(1);
            QtSlider.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_HGAP * 2), TEXT_HEIGHT));
            QtSlider.addChangeListener(e -> {
                setSELECTED_QT(QtSlider.getValue());
                QtValue.setText(String.valueOf(getSELECTED_QT()));
                QtTotalValue.setText(String.valueOf(getTotalPrice()));
            });

            QtTotalContainer.setLayout(new BorderLayout());
            QtTotalContainer.add(QtTotal, BorderLayout.WEST);
            QtTotalContainer.add(QtTotalValue, BorderLayout.CENTER);

            Qt.setLayout(new FlowLayout(FlowLayout.CENTER));
            Qt.setPreferredSize(new Dimension(CONTENT_WIDTH, 100));
            Qt.add(QtHeading);
            Qt.add(QtValue);
            Qt.add(QtTotalContainer);
            Qt.add(QtSlider);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="PRICE">//
            JPanel Price = new JPanel();
            Price.setBorder(BorderFactory.createEtchedBorder());

            JLabel PriceHeading = new JLabel("Price Per Unit");
            PriceHeading.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_HGAP * 2), 30));
            JLabel PriceInputHeading = new JLabel("Your Price (Per Unit):");
            PriceHeading.setFont(SUBTITLE);
            PriceInputHeading.setFont(CAPTION);

            SellCheck = new JCheckBox("Sell Order");
            SellCheck.setFont(CAPTION);

            ItemListener checkBoxListener = new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    SellCheck = (JCheckBox) e.getSource();
                    if (SellCheck.isSelected()) {
                        ArrayList<String> USERASSETS = null;
                        try {
                            USERASSETS = new ArrayList<>(assetData.getOrgAssets(userData.getUserOrgId(currentUser)));
                            ItemSelector.removeAll();
                            USERASSETS.add(0, "-- Select an Asset to Sell--");
                            ItemSelector.setModel(new DefaultComboBoxModel(USERASSETS.toArray()));

                            System.out.print("STATE CHANGE");
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                    }
                    if (!SellCheck.isSelected()) {
                        ArrayList<String> UNITS = null;
                        ArrayList<String> ALLASSETS = null;
                        try {
                            ALLASSETS = new ArrayList<>(assetData.getAssetList());
                            ALLASSETS.add(0, "-- Select a an Asset to Buy--");
                            ItemSelector.removeAll();
                            ItemSelector.setModel(new DefaultComboBoxModel(ALLASSETS.toArray()));
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }



                    }
                }
            };

            SellCheck.addItemListener(checkBoxListener);

            if(SellCheck.isSelected()){

                ItemSelector.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int assetId = 0;
                        try {

                            assetId = assetData.assetId((String) ItemSelector.getSelectedItem());
                            System.out.println(assetId);
                            int totalQty = assetData.getOrgAssetQty(userData.getUserOrgId(currentUser), assetId);

                            QtSlider.setMaximum(totalQty);

                            QtSlider.setValue(0);


                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                });}
            else{
                QtSlider.setMaximum(999);
                QtSlider.setValue(1);
            }


            PriceInput = new JTextField("Credits");
            PriceInput.setFont(CAPTION);
            PriceInput.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if ("Credits".equals(PriceInput.getText())) PriceInput.setText("");
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if ("".equals(PriceInput.getText())) PriceInput.setText("Credits");
                    else setSELECTED_PRICE(Integer.parseInt(PriceInput.getText()));
                }
            });

            JPanel PriceContainer = new JPanel();
            PriceContainer.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_HGAP * 4), TEXT_HEIGHT));
            PriceContainer.setLayout(new BorderLayout());
            PriceContainer.add(PriceInput, BorderLayout.CENTER);
            PriceContainer.add(SellCheck, BorderLayout.AFTER_LINE_ENDS);

            PriceInputHeading.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_HGAP * 4),30));

            Price.setPreferredSize(new Dimension(CONTENT_WIDTH, 125));
            Price.add(PriceHeading);
            //Price.add(PriceStatsContainer);
            Price.add(PriceInputHeading);
            Price.add(PriceContainer);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="CONTROLS">//
            JButton BtnCancel = new JButton("Cancel");
            JButton BtnSubmit = new JButton("Submit");
            BtnCancel.setFont(CAPTION);
            BtnSubmit.setFont(CAPTION);
            BtnSubmit.setBackground(SystemColor.activeCaption);
            BtnSubmit.setForeground(SystemColor.activeCaptionText);
            BtnCancel.addActionListener(e -> this.dispose());
            BtnSubmit.addActionListener(e -> {
                try {
                    Submit();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });

            JPanel Controls = new JPanel();
            Controls.setPreferredSize(new Dimension(CONTENT_WIDTH, 30));
            Controls.setLayout(new BorderLayout());
            Controls.add(BtnCancel, BorderLayout.LINE_START);
            Controls.add(BtnSubmit, BorderLayout.LINE_END);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="FRAME CONTENT">//
            JPanel Content = new JPanel();
            Content.setBorder(BorderFactory.createEtchedBorder());
            Content.setMinimumSize(new Dimension(CREATE_WIDTH, CREATE_HEIGHT));
            Content.setPreferredSize(new Dimension(CREATE_WIDTH, CREATE_HEIGHT));
            Content.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            Content.add(Heading);
            Content.add(ItemSelector);
            Content.add(Price);
            Content.add(Qt);
            Content.add(Controls);
            // </editor-fold>//

            this.setLayout(new GridBagLayout());
            this.setMinimumSize(new Dimension(CREATE_WIDTH, CREATE_HEIGHT));
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setVisible(true);
            this.setLocationRelativeTo(dashboardPanel);
            this.setAlwaysOnTop(true);

            this.getContentPane().add(Content, new GridBagConstraints());
        }

        private void Submit() throws SQLException {

            if (    Objects.equals(ItemSelector.getSelectedItem(), ItemSelector.getItemAt(0))){
                JOptionPane.showMessageDialog(this.getContentPane(), "Please select all Dropdowns!","Missing Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (    PriceInput.getText().equals("") || PriceInput.getText().equals("Credits")){
                JOptionPane.showMessageDialog(this.getContentPane(), "Please add a Price!","Missing Price", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (getTotalPrice() > userData.getUserCredits(currentUser).intValue() && !SellCheck.isSelected()) {
                // Get credits
                Integer userID = userData.getUserID(currentUser);
                Integer orgID = OrganisationalUnit.getOrgUnitID(userID);
                Double credits = OrganisationalUnit.getCredits(orgID);

                JOptionPane.showMessageDialog(this.getContentPane(), "You don't have enough credits!\n Available Credits: " + credits, "Not enough credits", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Integer assetID = 0;
            String assetName = (String) ItemSelector.getSelectedItem();
            if (assetName != null){
                assetID = Asset.getAssetID(assetName);
            }
            if (assetID == null){
                assetID = 0;
            }

            Integer userID;
            userID = userData.getUserID(currentUser);

            if (SellCheck.isSelected()) {
                if (JOptionPane.showConfirmDialog(this.getContentPane(), "Is this Sell order correct?") == 0){
                    orderData = new Order(userID, assetID, Order.Type.SELL, SELECTED_QT, getTotalPrice());
                    orderData.addOrder(orderData);
                    OrgDashTable.setModel(orderData.getAllOrgOrders(userData.getUserOrg(currentUser)));

                    this.dispose();
                }
            }

            if (!SellCheck.isSelected()) {
                if (JOptionPane.showConfirmDialog(this.getContentPane(), "Is this Buy order correct?") == 0){
                    orderData = new Order(userID, assetID, Order.Type.BUY, SELECTED_QT, getTotalPrice());
                    orderData.addOrder(orderData);
                    OrgDashTable.setModel(orderData.getAllOrgOrders(userData.getUserOrg(currentUser)));

                    this.dispose();
                }
            }

        }

        private int getTotalPrice() {
            return getSELECTED_QT() * getSELECTED_PRICE();
        }

        private void setSELECTED_QT(int SELECTED_QT) {
            this.SELECTED_QT = SELECTED_QT;
        }

        private int getSELECTED_QT() {
            return SELECTED_QT;
        }

        private int getSELECTED_PRICE() {
            return SELECTED_PRICE;
        }

        private void setSELECTED_PRICE(int SELECTED_PRICE) {
            this.SELECTED_PRICE = SELECTED_PRICE;
        }
    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="AdminPage">//
    private void InitAdminPage() throws SQLException {
        AdminPage.setBackground(PAGE_COLOR);

        AdminUsers = new JPanel();
        AdminAssets = new JPanel();
        AdminOrders = new JPanel();
        AdminUnits = new JPanel();
        AdminTransactions = new JPanel();

        InitAdminUsers();
        InitAdminUnits();
        InitAdminAssets();
        InitAdminOrders();
        InitAdminTransactions();

        AdminPage.addTab("Units", AdminUnits);
        AdminPage.addTab("Users", AdminUsers);
        AdminPage.addTab("Assets", AdminAssets);
        AdminPage.addTab("Orders", AdminOrders);
        AdminPage.addTab("Transaction", AdminTransactions);

    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Admin Units Tab">//
    private void InitAdminUnits() throws SQLException {
        orgData = new OrganisationalUnit();
        AdminUnits.setBackground(PAGE_COLOR);

        JLabel PageHeading = new JLabel("Units");
        PageHeading.setFont(HEADER);

        JButton btnCreate = new JButton("Create");
        //libby adding stuff

        btnCreate.addActionListener(e -> {
            EditStatus = false;
            try {
                new AdminCreateUnit("Create Unit");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        });

        JButton btnEdit = new JButton("Edit");

        btnEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(UnitTable.getSelectedRow()<0){
                    JOptionPane.showMessageDialog(null, "Please select a record to edit");
                }else
                    {try
                    {EditStatus = true;
                    new AdminCreateUnit("Edit Unit");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }}
            }
        });

        JButton btnRemove = new JButton("Remove");
        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(UnitTable.getSelectedRow()<0){
                    JOptionPane.showMessageDialog(null, "Please select a record to remove");
                }
                else{
                    try{
                        int orgID = OrganisationalUnit.getOrg(selectedUnit.name);
                        Boolean usersExist = OrganisationalUnit.usersExist(orgID);
                        System.out.println(usersExist);
                        if (usersExist) {
                            JOptionPane.showMessageDialog(null, "This organisation cannot be removed, there are existing users.");
                        }
                        else {
                            new AdminRemoveUnit("Remove Unit");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });

        /*
        btnRemove.addActionListener(e ->{
            try{
                    new AdminRemoveUnit("Remove Unit");
                } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            });*/

        //btnCreate.addActionListener(e -> new AdminCreateUnit("Create Unit"));

        JPanel Tools = new JPanel();
        Tools.setBorder(BorderFactory.createEtchedBorder());
        Tools.setMinimumSize(new Dimension(0, 85));
        Tools.setPreferredSize(new Dimension(0, 85));

        Tools.add(btnCreate);
        Tools.add(btnEdit);
        Tools.add(btnRemove);

        JScrollPane ScrollPane = new JScrollPane();
        UnitTable.setModel(orgData.getAllUnits());

        ScrollPane.setViewportView(UnitTable);

        GroupLayout layout = new GroupLayout(AdminUnits);
        AdminUnits.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(PageHeading, GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                                        .addComponent(Tools, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(ScrollPane, GroupLayout.Alignment.TRAILING))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(PageHeading, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Tools, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ScrollPane, GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                                .addContainerGap())
        );


        UnitTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println(UnitTable.getSelectedRow());
                selectedUnit = orgData.getOrgUnit(unitsTableSelection(e));
                System.out.print(selectedUnit.name);
            }
        });

    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Admin Units Remove">//

    /**
     * Holds methods for removing an organisational unit from within the GUI.
     */
    private static class AdminRemoveUnit extends JFrame {
        //private static JComboBox <String> UnitSelector;
        public AdminRemoveUnit(String title) throws HeadlessException, SQLException {
           //super(title);
           // UnitSelector = new JComboBox<>();

           // ArrayList<String> UNITS = new ArrayList<>(userData.getAllOrganisations());
           // UNITS.add(0, "-- Select an Organisations Unit to REMOVE --");

            int WIDTH = 590;
            int HEIGHT = 220;
            int CONTENT_HGAP = 5;
            int CONTENT_VGAP = 10;
            int CONTENT_WIDTH = 530;

            JLabel Heading = new JLabel();
            Heading = new JLabel("Admin -Remove an Organisational Unit");
            Heading.setFont(HEADER);

            Heading.setPreferredSize(new Dimension(CONTENT_WIDTH, 64));
            JPanel FormPnl = new JPanel();

            JLabel removeUnitLabel = new JLabel("You are removing organisation with the name " + selectedUnit.name);
            ;
            removeUnitLabel.setFont(CAPTION);
            FormPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 80));
            FormPnl.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            FormPnl.add(removeUnitLabel);

//            FormPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 80));
//            FormPnl.setLayout(new FlowLayout( FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));
//            UnitSelector.setModel(new DefaultComboBoxModel(UNITS.toArray()));
//            UnitSelector.setPreferredSize(new Dimension(CONTENT_WIDTH, 32));
//            UnitSelector.setFont(CAPTIONALT);
//
//            FormPnl.add(UnitSelector);

            JButton BtnCancel = new JButton("Cancel");
            JButton BtnSubmit = new JButton("Remove");
            BtnCancel.setFont(CAPTION);
            BtnSubmit.setFont(CAPTION);
            BtnSubmit.setBackground(SystemColor.activeCaption);
            BtnSubmit.setForeground(SystemColor.activeCaptionText);
            BtnCancel.addActionListener(e -> this.dispose());
            BtnSubmit.addActionListener(e -> {
                this.dispose();
                try {
                    UnitTable.setModel(orgData.getAllUnits());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                EditStatus = false;
            });
            BtnSubmit.addActionListener(e-> {
                try {
                    Submit();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });

            JPanel ControlsPnl = new JPanel();
            ControlsPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 30));
            ControlsPnl.setLayout(new BorderLayout());
            ControlsPnl.add(BtnCancel, BorderLayout.LINE_START);
            ControlsPnl.add(BtnSubmit, BorderLayout.LINE_END);

            JPanel ContentPnl = new JPanel();
            ContentPnl.setBorder(BorderFactory.createEtchedBorder());
            ContentPnl.setMinimumSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            ContentPnl.add(Heading);
            ContentPnl.add(FormPnl);
            ContentPnl.add(ControlsPnl);

            this.setLayout(new GridBagLayout());
            this.setMinimumSize(new Dimension(WIDTH + (CONTENT_VGAP * 2), HEIGHT + (CONTENT_VGAP * 5)));
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setVisible(true);
            this.setLocationRelativeTo(dashboardPanel);
            this.setAlwaysOnTop(true);

            this.getContentPane().add(ContentPnl, new GridBagConstraints());

        }
        private void Submit() throws SQLException {
//            if (Objects.equals(UnitSelector.getSelectedItem(), UnitSelector.getItemAt(0))){
//                JOptionPane.showMessageDialog(this.getContentPane(), "Please select all Dropdowns!","Missing Selection", JOptionPane.WARNING_MESSAGE);
//                return;
//            }
            if (JOptionPane.showConfirmDialog(this.getContentPane(), "Are you sure you want to remove this unit?") == 0){
                    //int removedId = orgData.getOrg((String) UnitSelector.getSelectedItem());
                    int removedId = OrganisationalUnit.getOrg(selectedUnit.name);
                    adminData.removeOrgUnit(removedId);
                    UnitTable.setModel(orgData.getAllUnits());
                this.dispose();
                EditStatus = false;
            }
        }
    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Admin Create a Unit">//
    /**
     * Holds methods for creating an organisational unit from within the GUI.
     */
    private class AdminCreateUnit extends JFrame {
        private  JTextField nameField;
        private  JTextField creditField;
        private  JComboBox <String> UnitSelector;

        public AdminCreateUnit(String title) throws HeadlessException, SQLException {
            super(title);
            nameField = new JTextField();
            creditField = new JTextField();
            UnitSelector = new JComboBox<>();

            int WIDTH = 590;
            int HEIGHT = 220;
            int CONTENT_HGAP = 5;
            int CONTENT_VGAP = 10;
            int CONTENT_WIDTH = 530;

            JLabel Heading = new JLabel();

            // <editor-fold defaultstate="collapsed" desc="Page Heading">//
            //JLabel Heading = new JLabel("Admin - Create an Organisational Unit");

            if(EditStatus == false) {
                Heading = new JLabel("Admin -Create an Organisational Unit");
            }else{
                Heading = new JLabel("Admin - Edit an Organisational Unit");
            }
            ArrayList<String> UNITS = new ArrayList<>(userData.getAllOrganisations());

            if(EditStatus) {
                UNITS.add(0, "-- Select an Organisations Unit --");
            }

            Heading.setFont(HEADER);
            Heading.setPreferredSize(new Dimension(CONTENT_WIDTH, 64));
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Details">//


            JLabel NameLabel = new JLabel("Unit Name: ");
            NameLabel.setFont(CAPTION);
            JPanel NamePanel = new JPanel();
            NamePanel.setLayout(new BorderLayout());
            NamePanel.add(NameLabel, BorderLayout.BEFORE_LINE_BEGINS);
            NamePanel.add(nameField, BorderLayout.CENTER);


            JLabel CreditLabel;
            if(EditStatus==false){
                CreditLabel = new JLabel("Credits: ");
            }
            else{
                CreditLabel = new JLabel("New Credits: ");
            }
            CreditLabel.setFont(CAPTION);
            JPanel CreditPanel = new JPanel();
            CreditPanel.setLayout(new BorderLayout());
            CreditPanel.add(CreditLabel, BorderLayout.BEFORE_LINE_BEGINS);
            CreditPanel.add(creditField, BorderLayout.CENTER);

            NameLabel.setPreferredSize(new Dimension(CONTENT_WIDTH / 7, TEXT_HEIGHT));
            CreditLabel.setPreferredSize(new Dimension(CONTENT_WIDTH / 7, TEXT_HEIGHT));
            nameField.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_WIDTH / 7), TEXT_HEIGHT));
            creditField.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_WIDTH / 7), TEXT_HEIGHT));
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Form">//
            JPanel FormPnl = new JPanel();
            FormPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 80));
            FormPnl.setLayout(new FlowLayout( FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            UnitSelector.setModel(new DefaultComboBoxModel(UNITS.toArray()));
            UnitSelector.setPreferredSize(new Dimension(CONTENT_WIDTH, 32));
            UnitSelector.setFont(CAPTIONALT);

            if (EditStatus==false) {
                FormPnl.add(NamePanel);
            }else{
                FormPnl.add(UnitSelector);
            }
            FormPnl.add(CreditPanel);

            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Controls">//
            JButton BtnCancel = new JButton("Cancel");
            JButton BtnSubmit;
            if(EditStatus == false) {
                BtnSubmit = new JButton("Create");
            }else{
                BtnSubmit = new JButton("Save");
            }
            BtnCancel.setFont(CAPTION);
            BtnSubmit.setFont(CAPTION);
            BtnSubmit.setBackground(SystemColor.activeCaption);
            BtnSubmit.setForeground(SystemColor.activeCaptionText);
            BtnCancel.addActionListener(e -> this.dispose());
            //change this up
            BtnSubmit.addActionListener(e -> {
                        this.dispose();
                try {
                    UnitTable.setModel(orgData.getAllUnits());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                EditStatus = false;
                    });
            BtnSubmit.addActionListener(e-> {
                try {
                    Submit();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });


            JPanel ControlsPnl = new JPanel();
            ControlsPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 30));
            ControlsPnl.setLayout(new BorderLayout());
            ControlsPnl.add(BtnCancel, BorderLayout.LINE_START);
            ControlsPnl.add(BtnSubmit, BorderLayout.LINE_END);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Frame Content">//
            JPanel ContentPnl = new JPanel();
            ContentPnl.setBorder(BorderFactory.createEtchedBorder());
            ContentPnl.setMinimumSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            ContentPnl.add(Heading);
            ContentPnl.add(FormPnl);
            ContentPnl.add(ControlsPnl);

            // </editor-fold>//

            this.setLayout(new GridBagLayout());
            this.setMinimumSize(new Dimension(WIDTH + (CONTENT_VGAP * 2), HEIGHT + (CONTENT_VGAP * 5)));
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setVisible(true);
            this.setLocationRelativeTo(dashboardPanel);
            this.setAlwaysOnTop(true);

            this.getContentPane().add(ContentPnl, new GridBagConstraints());
            /*if(EditStatus == true){
                UnitSelector.setSelectedIndex(userData.getOrgUnit() - 1);
            }*/

            if(EditStatus == true){
                UnitSelector.setSelectedItem (String.valueOf(selectedUnit.name));
                creditField.setText(String.valueOf(selectedUnit.intCred));
            }
        }

        private void Submit() throws SQLException {

            if (EditStatus == false) {
                if (nameField.getText().equals("") || creditField.getText().equals("")) {
                    JOptionPane.showMessageDialog(this.getContentPane(), "Please fill out all inputs!", "Missing Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            if (EditStatus==true){
                if(creditField.getText().equals("")){
                    JOptionPane.showMessageDialog(this.getContentPane(), "Please fill out all inputs!","Missing Input", JOptionPane.WARNING_MESSAGE);
                }

            }


            if (Objects.equals(UnitSelector.getSelectedItem(), UnitSelector.getItemAt(0)) && EditStatus){
                JOptionPane.showMessageDialog(this.getContentPane(), "Please select all Dropdowns!","Missing Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            System.out.println(nameField.getText());
            System.out.println(creditField.getText());

            if (JOptionPane.showConfirmDialog(this.getContentPane(), "Is this unit correct?") == 0){

                int org = 0;
                try {
                    org = OrganisationalUnit.getOrg((String) UnitSelector.getSelectedItem());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                if(EditStatus==false){
                    adminData.createOrgUnit(nameField.getText(), Double.parseDouble(creditField.getText()));

                }else{
                    System.out.print(UnitSelector.getSelectedIndex() + 1);
                    adminData.editUnitCredits(orgData.getOrg(UnitSelector.getSelectedItem().toString()) ,
                            Double.parseDouble(creditField.getText()));
                    EditStatus = false;
                    UnitTable.setModel(orgData.getAllUnits());
                };

                this.dispose();
                EditStatus = false;
            }
        }
    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Admin Users Tab">//
    private void InitAdminUsers() throws SQLException {
        AdminUsers.setBackground(PAGE_COLOR);

        JLabel PageHeading = new JLabel("Users");
        PageHeading.setFont(HEADER);

        JButton btnCreate = new JButton("Create");
        //Open new Frame for user creation
        btnCreate.addActionListener(e -> {
            EditStatus = false;
            try {
                new AdminCreateUser("Admin - Create User");

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        //Open new Frame for user edit
        JButton btnEdit = new JButton("Edit");

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(UserTable.getSelectedRow() < 0)
                {
                    JOptionPane.showMessageDialog(null, "Please select a record to edit");
                }else{
                    EditStatus = true;
                    try {
                        new AdminCreateUser("Admin - Edit User");

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });


        JButton btnRemove = new JButton("Remove");
        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(UserTable.getSelectedRow() < 0){
                    JOptionPane.showMessageDialog(null, "Please select a record to remove");
                }else{
                    try {
                        int userID = selectedUserID;
                        Boolean completed = Order.checkOrderStatusUser(userID);
                        ArrayList<Integer> orders = Order.getUserOrders(userID);
                        if (completed) {
                            JOptionPane.showMessageDialog(null, "This user cannot be removed, they have completed orders.");
                        }
                        else if (!orders.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "This user cannot be removed, they have ongoing orders.");
                        }
                        else {
                            new AdminRemoveUser("Admin - Remove User");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });


        JPanel Tools = new JPanel();
        Tools.setBorder(BorderFactory.createEtchedBorder());
        Tools.setMinimumSize(new Dimension(0, 85));
        Tools.setPreferredSize(new Dimension(0, 85));

        Tools.add(btnCreate);
        Tools.add(btnEdit);
        Tools.add(btnRemove);

        JScrollPane ScrollPane = new JScrollPane();
        UserTable.setModel(userData.getAllUser());

        ScrollPane.setViewportView(UserTable);

        GroupLayout layout = new GroupLayout(AdminUsers);
        AdminUsers.setLayout(layout);
        UserTable.addMouseListener(new MouseAdapter(){
            public void mouseClicked (MouseEvent e){
                try {
                    userData.getThisUser(userTableSelection(e));
                    selectedUserID = userData.getUserID(userTableSelection(e));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(PageHeading, GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                                        .addComponent(Tools, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(ScrollPane, GroupLayout.Alignment.TRAILING))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(PageHeading, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Tools, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ScrollPane, GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                                .addContainerGap())
        );

    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="User Table Selection">//

    /**
     * Gets the selection on the user table.
     *
     * @param e The mouse click event.
     * @return The ID of the user selected.
     */
    public String userTableSelection(MouseEvent e){
        int index = UserTable.getSelectedRow();
        TableModel model = UserTable.getModel();
        String userName = model.getValueAt(index,2).toString();

        return userName;

    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Admin Create a User">//
    /**
     * Holds methods for creating a user from within the GUI.
     */
    private static class AdminCreateUser extends JFrame {

        private static JTextField fNameField;
        private static JTextField lNameField;
        private static JTextField uNameField;

        private static JPasswordField passField;
        private static JPasswordField passConfirmField;

        private static JComboBox<String> UnitSelector;
        private static JComboBox<String> LevelSelector;

        public AdminCreateUser(String title) throws HeadlessException, SQLException {
            super(title);
            fNameField = new JTextField();
            lNameField = new JTextField();
            uNameField = new JTextField();
            passField = new JPasswordField();
            passConfirmField = new JPasswordField();
            UnitSelector = new JComboBox<>();
            LevelSelector = new JComboBox<>();
            int WIDTH = 590;
            int HEIGHT = 375;
            int CONTENT_HGAP = 5;
            int CONTENT_VGAP = 10;
            int CONTENT_WIDTH = 530;

            JLabel Heading = new JLabel();
            JButton BtnSubmit = new JButton();

            ArrayList<String> UNITS = new ArrayList<>(userData.getAllOrganisations());
            ArrayList<String> LEVEL = new ArrayList<>(userData.getAllPermissions());
            if(EditStatus == false) {
                UNITS.add(0, "-- Select an Organisations Unit --");
                LEVEL.add(0, "-- Select a Permissions Level --");
            }

            // <editor-fold defaultstate="collapsed" desc="Page Heading">//
            if(EditStatus == false) {
                Heading = new JLabel("Admin - Create a User");
            }else{
                Heading = new JLabel("Admin - Edit a User");
            }
            Heading.setFont(HEADER);
            Heading.setPreferredSize(new Dimension(CONTENT_WIDTH, 64));
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="User Details">//

            JLabel fNameLabel = new JLabel("First Name: ");
            fNameLabel.setFont(CAPTION);
            JPanel fNamePanel = new JPanel();
            fNamePanel.setLayout(new BorderLayout());
            fNamePanel.add(fNameLabel, BorderLayout.BEFORE_LINE_BEGINS);
            fNamePanel.add(fNameField, BorderLayout.CENTER);

            JLabel lNameLabel = new JLabel("Last Name: ");
            lNameLabel.setFont(CAPTION);
            JPanel lNamePanel = new JPanel();
            lNamePanel.setLayout(new BorderLayout());
            lNamePanel.add(lNameLabel, BorderLayout.BEFORE_LINE_BEGINS);
            lNamePanel.add(lNameField, BorderLayout.CENTER);

            JPanel NameContainer = new JPanel();
            NameContainer.setLayout(new BorderLayout());
            NameContainer.add(fNamePanel, BorderLayout.WEST);
            NameContainer.add(lNamePanel, BorderLayout.EAST);

            JLabel uNameLabel = new JLabel("Username: ");
            uNameLabel.setFont(CAPTION);
            JPanel uNamePanel = new JPanel();
            uNamePanel.setLayout(new BorderLayout());
            uNamePanel.add(uNameLabel, BorderLayout.BEFORE_LINE_BEGINS);
            uNamePanel.add(uNameField, BorderLayout.CENTER);

            JLabel passLabel = new JLabel("Password: ");
            passLabel.setFont(CAPTION);
            JPanel passPanel = new JPanel();
            passPanel.setLayout(new BorderLayout());
            passPanel.add(passLabel, BorderLayout.BEFORE_LINE_BEGINS);
            passPanel.add(passField, BorderLayout.CENTER);

            JLabel passConfirmLabel = new JLabel("Confirm Password: ");
            passConfirmLabel.setFont(CAPTION);
            JPanel passConfirmPanel = new JPanel();
            passConfirmPanel.setLayout(new BorderLayout());
            passConfirmPanel.add(passConfirmLabel, BorderLayout.BEFORE_LINE_BEGINS);
            passConfirmPanel.add(passConfirmField, BorderLayout.CENTER);

            fNameField.setPreferredSize(new Dimension(CONTENT_WIDTH / 3, TEXT_HEIGHT));
            lNameField.setPreferredSize(new Dimension(CONTENT_WIDTH / 3, TEXT_HEIGHT));
            fNameLabel.setPreferredSize(new Dimension(CONTENT_WIDTH / 7, TEXT_HEIGHT));
            lNameLabel.setPreferredSize(new Dimension(CONTENT_WIDTH / 7, TEXT_HEIGHT));

            uNameLabel.setPreferredSize(new Dimension(CONTENT_WIDTH / 7, TEXT_HEIGHT));

            passLabel.setPreferredSize(new Dimension(CONTENT_WIDTH / 5, TEXT_HEIGHT));
            passConfirmLabel.setPreferredSize(new Dimension(CONTENT_WIDTH / 5, TEXT_HEIGHT));

            NameContainer.setPreferredSize(new Dimension(CONTENT_WIDTH, TEXT_HEIGHT));
            uNamePanel.setPreferredSize(new Dimension(CONTENT_WIDTH, TEXT_HEIGHT));
            passPanel.setPreferredSize(new Dimension(CONTENT_WIDTH, TEXT_HEIGHT));
            passConfirmPanel.setPreferredSize(new Dimension(CONTENT_WIDTH, TEXT_HEIGHT));
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Unit Selector">//
            UnitSelector.setModel(new DefaultComboBoxModel(UNITS.toArray()));
            UnitSelector.setPreferredSize(new Dimension(CONTENT_WIDTH, 32));
            UnitSelector.setFont(CAPTIONALT);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Access Level Selector">//
            LevelSelector.setModel(new DefaultComboBoxModel(LEVEL.toArray()));
            LevelSelector.setPreferredSize(new Dimension(CONTENT_WIDTH, 32));
            LevelSelector.setFont(CAPTIONALT);


            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Form">//
            JPanel FormPnl = new JPanel();
            FormPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 150));
            FormPnl.setLayout(new FlowLayout( FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            FormPnl.add(uNamePanel);
            FormPnl.add(NameContainer);
            FormPnl.add(passPanel);
            FormPnl.add(passConfirmPanel);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Controls">//
            JButton BtnCancel = new JButton("Cancel");
            if(EditStatus == false) {
                BtnSubmit = new JButton("Create");
            }else{
                BtnSubmit = new JButton("Save");
            }
            BtnCancel.setFont(CAPTION);
            BtnSubmit.setFont(CAPTION);
            BtnSubmit.setBackground(SystemColor.activeCaption);
            BtnSubmit.setForeground(SystemColor.activeCaptionText);
            BtnCancel.addActionListener(e -> {
                this.dispose();
                EditStatus = false;
            });
            BtnSubmit.addActionListener(e -> {
                try {
                    Submit();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });

            JPanel ControlsPnl = new JPanel();
            ControlsPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 30));
            ControlsPnl.setLayout(new BorderLayout());
            ControlsPnl.add(BtnCancel, BorderLayout.LINE_START);
            ControlsPnl.add(BtnSubmit, BorderLayout.LINE_END);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Frame Content">//
            JPanel ContentPnl = new JPanel();
            ContentPnl.setBorder(BorderFactory.createEtchedBorder());
            ContentPnl.setMinimumSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            ContentPnl.add(Heading);
            ContentPnl.add(UnitSelector);
            ContentPnl.add(LevelSelector);
            ContentPnl.add(FormPnl);
            ContentPnl.add(ControlsPnl);

            // </editor-fold>//

            this.setLayout(new GridBagLayout());
            this.setMinimumSize(new Dimension(WIDTH + (CONTENT_VGAP * 2), HEIGHT + (CONTENT_VGAP * 5)));
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setVisible(true);
            this.setLocationRelativeTo(dashboardPanel);
            this.setAlwaysOnTop(true);

            this.getContentPane().add(ContentPnl, new GridBagConstraints());

            if(EditStatus == true) {
                OrganisationalUnit temp = orgData.getOrgUnit(userData.getOrgUnit());
                fNameField.setText(userData.getFirstName());
                lNameField.setText(userData.getLastName());
                uNameField.setText(userData.getUserName());
                UnitSelector.setSelectedItem(temp.name);
                LevelSelector.setSelectedItem(userData.getPermType(userData.permissionID));
            }
        }

        private void Submit() throws SQLException {

            if (    Objects.equals(UnitSelector.getSelectedItem(), UnitSelector.getItemAt(0)) && !EditStatus ||
                    Objects.equals(LevelSelector.getSelectedItem(), LevelSelector.getItemAt(0)) && !EditStatus
            ){
                JOptionPane.showMessageDialog(this.getContentPane(), "Please select all Dropdowns!","Missing Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (    fNameField.getText().equals("") ||
                    lNameField.getText().equals("") ||
                    uNameField.getText().equals("")
            ){
                JOptionPane.showMessageDialog(this.getContentPane(), "Please fill out all inputs!","Missing Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!Arrays.equals(passField.getPassword(), passConfirmField.getPassword())) {
                JOptionPane.showMessageDialog(this.getContentPane(), "Passwords are not the same!","Password Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(adminData.userNameExists(uNameField.getText()) && !EditStatus){
                JOptionPane.showMessageDialog(this.getContentPane(), "This username already exists. " +
                                "Pleaser enter a unique username. ",
                        "Username Already Exists", JOptionPane.WARNING_MESSAGE);
                return;

            }

            if (JOptionPane.showConfirmDialog(this.getContentPane(), "Is this User Creation correct?") == 0) {

                int access = 0;
                try {
                    access = userData.getPermId((String) LevelSelector.getSelectedItem());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                int org = 0;
                try {
                    org = OrganisationalUnit.getOrg((String) UnitSelector.getSelectedItem());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                if (!EditStatus) {
                    adminData.createUser(fNameField.getText(),
                            lNameField.getText(),
                            uNameField.getText(),
                            passField.getText(),
                            org, access);
                } else {
                    if (passField.getText().equals("")) {
                        adminData.editUser(fNameField.getText(),
                                lNameField.getText(),
                                uNameField.getText(),
                                orgData.getOrg(UnitSelector.getSelectedItem().toString()),
                                userData.getPermId(LevelSelector.getSelectedItem().toString()),
                                userData.getUserName()
                        );
                    } else {
                        adminData.editUser(fNameField.getText(),
                                lNameField.getText(),
                                uNameField.getText(),
                                orgData.getOrg(UnitSelector.getSelectedItem().toString()),
                                userData.getPermId(LevelSelector.getSelectedItem().toString()),
                                userData.getUserName());
                        adminData.setUserPassword(uNameField.getText(), passField.getText());
                    }
                    EditStatus = false;
                }


                UserTable.setModel(userData.getAllUser());
                this.dispose();
                EditStatus = false;
            }
        }
    }

    /**
     * Holds methods for removing a user from within the GUI.
     */
    private static class AdminRemoveUser extends JFrame {
        public AdminRemoveUser(String title) throws HeadlessException, SQLException {
            int WIDTH = 590;
            int HEIGHT = 220;
            int CONTENT_HGAP = 5;
            int CONTENT_VGAP = 10;
            int CONTENT_WIDTH = 530;

            JLabel Heading = new JLabel();
            Heading = new JLabel("Admin - Remove a user");
            Heading.setFont(HEADER);

            Heading.setPreferredSize(new Dimension(CONTENT_WIDTH, 64));
            JPanel FormPnl = new JPanel();

            JLabel removeUserLabel = new JLabel("Would you like to remove user: " + userData.getUserName());;
            removeUserLabel.setFont(CAPTION);
            FormPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 80));
            FormPnl.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            FormPnl.add(removeUserLabel);

            JButton BtnCancel = new JButton("Cancel");
            JButton BtnSubmit = new JButton("Remove");
            BtnCancel.setFont(CAPTION);
            BtnSubmit.setFont(CAPTION);
            BtnSubmit.setBackground(SystemColor.activeCaption);
            BtnSubmit.setForeground(SystemColor.activeCaptionText);
            BtnCancel.addActionListener(e -> this.dispose());
            BtnSubmit.addActionListener(e -> {
                try {
                    Submit();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            JPanel ControlsPnl = new JPanel();
            ControlsPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 30));
            ControlsPnl.setLayout(new BorderLayout());
            ControlsPnl.add(BtnCancel, BorderLayout.LINE_START);
            ControlsPnl.add(BtnSubmit, BorderLayout.LINE_END);

            JPanel ContentPnl = new JPanel();
            ContentPnl.setBorder(BorderFactory.createEtchedBorder());
            ContentPnl.setMinimumSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            ContentPnl.add(Heading);
            ContentPnl.add(FormPnl);
            ContentPnl.add(ControlsPnl);

            this.setLayout(new GridBagLayout());
            this.setMinimumSize(new Dimension(WIDTH + (CONTENT_VGAP * 2), HEIGHT + (CONTENT_VGAP * 5)));
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setVisible(true);
            this.setLocationRelativeTo(dashboardPanel);
            this.setAlwaysOnTop(true);

            this.getContentPane().add(ContentPnl, new GridBagConstraints());
        }

        private void Submit() throws SQLException {

            if (JOptionPane.showConfirmDialog(this.getContentPane(), "Are you sure you want to remove this user?") == 0){
                adminData.removeUser(userData.getUserName());
                UserTable.setModel(userData.getAllUser());
                this.dispose();
                EditStatus = false;
            }
        }
        }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Admin Assets Tab">//
    private void InitAdminAssets() throws SQLException {
        assetData = new Asset();


        AdminAssets.setBackground(PAGE_COLOR);

        JLabel PageHeading = new JLabel("Assets");
        PageHeading.setFont(HEADER);

        JButton btnCreate = new JButton("Create");
        JButton btnEdit = new JButton("Edit");
        JButton btnRemove = new JButton("Remove");

        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (AssetTable.getSelectedRow() < 0) {
                    JOptionPane.showMessageDialog(null, "Please select a record to remove");
                }else{
                    try {
                        int assetID = selectedAsset.assetId;
                        Boolean completed = Order.checkOrderStatusAsset(assetID);
                        ArrayList<Integer> orders = Order.getAssetOrders(assetID);
                        if (completed) {
                            JOptionPane.showMessageDialog(null, "This asset cannot be removed, there are completed orders.");
                        }
                        else if (!orders.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "This asset cannot be removed, there still orders on it.");
                        }
                        else {
                            new AdminRemoveAsset("Admin - Remove Asset");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }

            }
        });


        btnCreate.addActionListener(e -> {
            EditStatus = false;
            try {
                new AdminCreateAsset("Create Asset");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (AssetTable.getSelectedRow() < 0) {
                    JOptionPane.showMessageDialog(null, "Please select a record to edit");
                } else {

                    EditStatus = true;
                    try {
                        new AdminCreateAsset("Edit Asset");
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                }
            }
        });



        JPanel Tools = new JPanel();
        Tools.setBorder(BorderFactory.createEtchedBorder());
        Tools.setMinimumSize(new Dimension(0, 85));
        Tools.setPreferredSize(new Dimension(0, 85));

        Tools.add(btnCreate);
        Tools.add(btnEdit);
        Tools.add(btnRemove);

        JScrollPane ScrollPane = new JScrollPane();
        AssetTable.setModel(assetData.getAllAssets());


        ScrollPane.setViewportView(AssetTable);

        GroupLayout layout = new GroupLayout(AdminAssets);
        AdminAssets.setLayout(layout);

        //enable table selection
        AssetTable.addMouseListener(new MouseAdapter(){


            public void mouseClicked (MouseEvent e){

                selectedAsset = assetData.getAsset(assetTableSelection(e));

            }


        });

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(PageHeading, GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                                        .addComponent(Tools, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(ScrollPane, GroupLayout.Alignment.TRAILING))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(PageHeading, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Tools, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ScrollPane, GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                                .addContainerGap())
        );

    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Admin Create an Assets">//
    /**
     * Holds methods for creating an asset from within the GUI.
     */
    private static class AdminCreateAsset extends JFrame {
        private static JTextField nameField;
        private static JTextField quantityField;

        private static JComboBox<String> UnitSelector;

        public AdminCreateAsset(String title) throws HeadlessException, SQLException {
            super(title);
            nameField = new JTextField();
            quantityField = new JTextField();
            UnitSelector = new JComboBox<>();

            int WIDTH = 590;
            int HEIGHT = 300;
            int CONTENT_HGAP = 5;
            int CONTENT_VGAP = 10;
            int CONTENT_WIDTH = 530;

            ArrayList<String> UNITS = new ArrayList<>(userData.getAllOrganisations());
            UNITS.add(0,"-- Select an Organisations Unit --");

            // <editor-fold defaultstate="collapsed" desc="Page Heading">//
            JLabel Heading = new JLabel();
            if(!EditStatus) {
                Heading = new JLabel("Admin - Create an Asset");
            }
            else{
                Heading = new JLabel("Admin - Edit an Asset");
            }

            Heading.setFont(HEADER);
            Heading.setPreferredSize(new Dimension(CONTENT_WIDTH, 64));
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Details">//

            JLabel NameLabel = new JLabel("Asset Name: ");
            NameLabel.setFont(CAPTION);
            JPanel NamePanel = new JPanel();
            NamePanel.setLayout(new BorderLayout());
            NamePanel.add(NameLabel, BorderLayout.BEFORE_LINE_BEGINS);
            NamePanel.add(nameField, BorderLayout.CENTER);

            JLabel QtLabel = new JLabel("Quantity: ");
            QtLabel.setFont(CAPTION);
            JPanel QtPanel = new JPanel();
            QtPanel.setLayout(new BorderLayout());
            QtPanel.add(QtLabel, BorderLayout.BEFORE_LINE_BEGINS);
            QtPanel.add(quantityField, BorderLayout.CENTER);

            NameLabel.setPreferredSize(new Dimension(CONTENT_WIDTH / 7, TEXT_HEIGHT));
            QtLabel.setPreferredSize(new Dimension(CONTENT_WIDTH / 7, TEXT_HEIGHT));
            nameField.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_WIDTH / 7), TEXT_HEIGHT));
            quantityField.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_WIDTH / 7), TEXT_HEIGHT));
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Unit Selector">//
            UnitSelector.setModel(new DefaultComboBoxModel(UNITS.toArray()));
            UnitSelector.setPreferredSize(new Dimension(CONTENT_WIDTH, 32));
            UnitSelector.setFont(CAPTIONALT);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Form">//
            JPanel FormPnl = new JPanel();
            FormPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 120));
            FormPnl.setLayout(new FlowLayout( FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            FormPnl.add(NamePanel);
            FormPnl.add(QtPanel);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Controls">//
            JButton BtnCancel = new JButton("Cancel");
            JButton BtnSubmit = new JButton();
            if(!EditStatus){
            BtnSubmit = new JButton("Create");
            }else{
                BtnSubmit = new JButton("Save");
            }

            BtnCancel.setFont(CAPTION);
            BtnSubmit.setFont(CAPTION);
            BtnSubmit.setBackground(SystemColor.activeCaption);
            BtnSubmit.setForeground(SystemColor.activeCaptionText);
            BtnCancel.addActionListener(e ->{
                this.dispose();
                EditStatus = false;
                });
            BtnSubmit.addActionListener(e -> {
                try {
                    Submit();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });

            JPanel ControlsPnl = new JPanel();
            ControlsPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 30));
            ControlsPnl.setLayout(new BorderLayout());
            ControlsPnl.add(BtnCancel, BorderLayout.LINE_START);
            ControlsPnl.add(BtnSubmit, BorderLayout.LINE_END);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Frame Content">//
            JPanel ContentPnl = new JPanel();
            ContentPnl.setBorder(BorderFactory.createEtchedBorder());
            ContentPnl.setMinimumSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            ContentPnl.add(Heading);
            ContentPnl.add(UnitSelector);
            ContentPnl.add(FormPnl);
            ContentPnl.add(ControlsPnl);

            // </editor-fold>//

            this.setLayout(new GridBagLayout());
            this.setMinimumSize(new Dimension(WIDTH + (CONTENT_VGAP * 2), HEIGHT + (CONTENT_VGAP * 5)));
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setVisible(true);
            this.setLocationRelativeTo(dashboardPanel);
            this.setAlwaysOnTop(true);

            this.getContentPane().add(ContentPnl, new GridBagConstraints());

            if(EditStatus == true){
                nameField.setText(selectedAsset.name);
                quantityField.setText(String.valueOf(selectedAsset.quantity));
                OrganisationalUnit temp=orgData.getOrgUnit(selectedAsset.orgUnitID);
                UnitSelector.setSelectedItem(temp.name);
            }
        }


        private void Submit() throws SQLException {

            if (Objects.equals(UnitSelector.getSelectedItem(), UnitSelector.getItemAt(0)) && !EditStatus){
                JOptionPane.showMessageDialog(this.getContentPane(), "Please select a Unit!","Missing Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if ( nameField.getText().equals("") || quantityField.getText().equals("")){
                JOptionPane.showMessageDialog(this.getContentPane(), "Please fill out all inputs!","Missing Input", JOptionPane.WARNING_MESSAGE);
                return;
            }


            int org = 0;

            try {
                org = OrganisationalUnit.getOrg((String) UnitSelector.getSelectedItem());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            if(!EditStatus) {

                if (JOptionPane.showConfirmDialog(this.getContentPane(), "Is this Asset correct?") == 0) {
                    adminData.addAsset(nameField.getText(), Integer.parseInt(quantityField.getText()), org);
                    AssetTable.setModel(assetData.getAllAssets());
                    this.dispose();
                }
            }
            else{

                    if (JOptionPane.showConfirmDialog(this.getContentPane(), "Are these details correct?") == 0) {
                        assetData.updateAsset(nameField.getText(), Integer.parseInt(quantityField.getText()), org, selectedAsset.assetId);
                        AssetTable.setModel(assetData.getAllAssets());
                        this.dispose();
                    }

            }
        }
    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Admin Remove an Asset">//
    /**
     * Holds methods for removing an asset from within the GUI.
     */
    private static class AdminRemoveAsset extends JFrame {
        public AdminRemoveAsset(String title) throws HeadlessException, SQLException {
            int WIDTH = 590;
            int HEIGHT = 220;
            int CONTENT_HGAP = 5;
            int CONTENT_VGAP = 10;
            int CONTENT_WIDTH = 530;

            JLabel Heading = new JLabel();
            Heading = new JLabel("Admin - Remove an Asset");
            Heading.setFont(HEADER);

            Heading.setPreferredSize(new Dimension(CONTENT_WIDTH, 64));
            JPanel FormPnl = new JPanel();

            JLabel removeAssetLabel = new JLabel("Would you like to remove this asset:\n" + "Asset ID: " + selectedAsset.assetId +" "+ "Name: " + selectedAsset.name +" "+ "QTY: " + selectedAsset.quantity);
            ;
            removeAssetLabel.setFont(CAPTION);
            FormPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 80));
            FormPnl.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            FormPnl.add(removeAssetLabel);

            JButton BtnCancel = new JButton("Cancel");
            JButton BtnSubmit = new JButton("Remove");
            BtnCancel.setFont(CAPTION);
            BtnSubmit.setFont(CAPTION);
            BtnSubmit.setBackground(SystemColor.activeCaption);
            BtnSubmit.setForeground(SystemColor.activeCaptionText);
            BtnCancel.addActionListener(e -> this.dispose());
            BtnSubmit.addActionListener(e -> {
                try {
                    Submit();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            JPanel ControlsPnl = new JPanel();
            ControlsPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 30));
            ControlsPnl.setLayout(new BorderLayout());
            ControlsPnl.add(BtnCancel, BorderLayout.LINE_START);
            ControlsPnl.add(BtnSubmit, BorderLayout.LINE_END);

            JPanel ContentPnl = new JPanel();
            ContentPnl.setBorder(BorderFactory.createEtchedBorder());
            ContentPnl.setMinimumSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            ContentPnl.add(Heading);
            ContentPnl.add(FormPnl);
            ContentPnl.add(ControlsPnl);

            this.setLayout(new GridBagLayout());
            this.setMinimumSize(new Dimension(WIDTH + (CONTENT_VGAP * 2), HEIGHT + (CONTENT_VGAP * 5)));
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setVisible(true);
            this.setLocationRelativeTo(dashboardPanel);
            this.setAlwaysOnTop(true);

            this.getContentPane().add(ContentPnl, new GridBagConstraints());
        }
        private void Submit() throws SQLException {

            if (JOptionPane.showConfirmDialog(this.getContentPane(), "Are you sure you want to remove this asset?") == 0){
                assetData.removeAsset(selectedAsset.assetId);
                AssetTable.setModel(assetData.getAllAssets());
                this.dispose();
                EditStatus = false;
            }
        }

    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Admin Orders Tab">//
    private void InitAdminOrders() throws SQLException {
        orderData = new Order();
        userData = new User();
        AdminOrders.setBackground(PAGE_COLOR);

        JLabel PageHeading = new JLabel("Orders");
        PageHeading.setFont(HEADER);

        JButton btnCreate = new JButton("Create");
        btnCreate.addActionListener(e -> {
            try {
                //userData.getUserCredits(currentUser);
                EditStatus = false;
                new AdminCreateOrder("Admin - Create Order");


            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        JButton btnEdit = new JButton("Edit");
        //int temp = OrderTable.getSelectedRow();
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(OrderTable.getSelectedRow() < 0)
                {
                    JOptionPane.showMessageDialog(null, "Please select a record to edit");
                }
                else{
                    try {
                        EditStatus=true;

                        int orderID = selectedOrder.orderId;
                        Integer status = Order.getOrderStatus(orderID);
                        if (status == 2) {
                            JOptionPane.showMessageDialog(null, "This order cannot be edited, it has been completed.");
                        }
                        else {
                            new AdminCreateOrder("Admin - Edit Order");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
        JButton btnRemove = new JButton("Remove");
        btnRemove.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(OrderTable.getSelectedRow());
                if (OrderTable.getSelectedRow() < 0) {
                    JOptionPane.showMessageDialog(null, "Please select a record to remove");
                } else {
                    try {
                        int orderID = selectedOrder.orderId;
                        Integer status = Order.getOrderStatus(orderID);
                        if (status == 2) {
                            JOptionPane.showMessageDialog(null, "This order cannot be removed, it has been completed.");
                        }
                        else {
                            new AdminRemoveOrder("Admin - Remove Order");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }

        });

        JPanel Tools = new JPanel();
        Tools.setBorder(BorderFactory.createEtchedBorder());
        Tools.setMinimumSize(new Dimension(0, 85));
        Tools.setPreferredSize(new Dimension(0, 85));

        Tools.add(btnCreate);
        Tools.add(btnEdit);
        Tools.add(btnRemove);

        JScrollPane ScrollPane = new JScrollPane();
        OrderTable.setModel(orderData.getAllOrders());

        ScrollPane.setViewportView(OrderTable);

        GroupLayout layout = new GroupLayout(AdminOrders);
        AdminOrders.setLayout(layout);

        OrderTable.addMouseListener(new MouseAdapter(){
            public void mouseReleased (MouseEvent e){
                selectedOrder = orderData.getOrder(orderTableSelection(e));
            }


        });

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(PageHeading, GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                                        .addComponent(Tools, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(ScrollPane, GroupLayout.Alignment.TRAILING))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(PageHeading, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Tools, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ScrollPane, GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                                .addContainerGap())
        );

    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Admin Create an Order">//
    /**
     * Holds methods for creating an order from within the GUI.
     */
    private class AdminCreateOrder extends JFrame {

        private  JComboBox<String> ItemSelector;
        private  JComboBox<String> UnitSelector;

        private  JSlider QtSlider;

        private  JCheckBox SellCheck;
        private  JTextField PriceInput;

        private int SELECTED_QT;
        private int SELECTED_PRICE;

        ArrayList<String> ITEMS = new ArrayList<>();
        ArrayList<String> UNITS = new ArrayList<>();



        public AdminCreateOrder(String title) throws HeadlessException, SQLException {
            super(title);

            int CREATE_WIDTH = 590;
            int CREATE_HEIGHT = 500;
            int CONTENT_HGAP = 5;
            int CONTENT_VGAP = 10;
            int CONTENT_WIDTH = 530;



            // <editor-fold defaultstate="collapsed" desc="HEADING">//
            JLabel Heading = new JLabel();
            if(EditStatus == true) {
                Heading = new JLabel("Admin - Edit an Order");
            }else{
                Heading = new JLabel("Admin - Create an Order");
            }
            Heading.setFont(HEADER);
            Heading.setPreferredSize(new Dimension(CONTENT_WIDTH, 64));
            // </editor-fold>//

            ItemListener checkBoxListener = new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    SellCheck = (JCheckBox) e.getSource();
                    if (SellCheck.isSelected()) {
                        ArrayList<String> ORGUNITS = new ArrayList<>(userData.getAllUserNames());
                        ORGUNITS.add(0, "-- Select a User --");
                        UnitSelector.removeAll();
                        UnitSelector.setModel(new DefaultComboBoxModel(ORGUNITS.toArray()));
                        ArrayList<String> ORGASSETS = new ArrayList<>();
                        ORGASSETS.add(0, "-- Select a User First --");
                        ItemSelector.setModel(new DefaultComboBoxModel(ORGASSETS.toArray()));
                        if(EditStatus == true){
                            ArrayList<String> ITEMS = null;
                            try {
                                ITEMS = new ArrayList<>(assetData.getOrgAssets(orgData.getOrgUnitID(selectedOrder.userID)));
                                ITEMS.add(0, "-- Select an Asset --");
                                ItemSelector.setModel(new DefaultComboBoxModel(ITEMS.toArray()));
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }

                        }

                        System.out.print("STATE CHANGE");


                    }
                    if (!SellCheck.isSelected()) {
                        ArrayList<String> UNITS = null;
                        ArrayList<String> ALLUSERS = new ArrayList<>(userData.getAllUserNames());
                        ALLUSERS.add(0, "-- Select a User --");
                        UnitSelector.removeAll();
                        UnitSelector.setModel(new DefaultComboBoxModel(ALLUSERS.toArray()));



                    }
                }
            };
            ItemSelector = new JComboBox<>();
            UnitSelector = new JComboBox<>();
            ArrayList<String> UNITS = new ArrayList<>(userData.getAllUserNames());
            UNITS.add(0, "-- Select a User --");
            UnitSelector.setModel(new DefaultComboBoxModel(UNITS.toArray()));
            ArrayList<String> ITEMS = new ArrayList<>(assetData.getAssetList());
            ITEMS.add(0, "-- Select an Asset --");
            ItemSelector.setModel(new DefaultComboBoxModel(ITEMS.toArray()));


            // <editor-fold defaultstate="collapsed" desc="SELECTORS">//
            if (!EditStatus) {
                ItemSelector = new JComboBox<>();
                ItemSelector.setModel(new DefaultComboBoxModel(ITEMS.toArray()));
                ItemSelector.setPreferredSize(new Dimension(CONTENT_WIDTH, 32));
                ItemSelector.setFont(CAPTIONALT);

                UnitSelector = new JComboBox<>();
                UnitSelector.setModel(new DefaultComboBoxModel(UNITS.toArray()));
                UnitSelector.setPreferredSize(new Dimension(CONTENT_WIDTH, 32));
                UnitSelector.setFont(CAPTIONALT);
            } else {
                ItemSelector = new JComboBox<>();
                UnitSelector.setVisible(false);
                ArrayList<String> ALLITEMS = new ArrayList<>(assetData.getAssetList());
                ItemSelector.setModel(new DefaultComboBoxModel(ALLITEMS.toArray()));
                ItemSelector.setPreferredSize(new Dimension(CONTENT_WIDTH, 32));
                ItemSelector.setFont(CAPTIONALT);
            }



            //initilising qty slider
            QtSlider = new JSlider();
            // <editor-fold defaultstate="collapsed" desc="QUANTITY">//
            JPanel Qt = new JPanel();
            Qt.setBorder(BorderFactory.createEtchedBorder());

            JPanel QtTotalContainer = new JPanel();
            JLabel QtHeading = new JLabel("Quantity");
            QtHeading.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_HGAP * 2), 30));
            JLabel QtValue = new JLabel(String.valueOf(getSELECTED_QT()));
            QtValue.setPreferredSize(new Dimension(375, 20));
            JLabel QtTotal = new JLabel("Total Credits: ");
            JLabel QtTotalValue = new JLabel(String.valueOf(getTotalPrice()));
            QtHeading.setFont(SUBTITLE);
            QtValue.setFont(CAPTION);
            QtTotal.setFont(CAPTION);
            QtTotalValue.setFont(CAPTION);

            SellCheck = new JCheckBox("Sell Order");
            SellCheck.setFont(CAPTION);
            SellCheck.addItemListener(checkBoxListener);

                if (!EditStatus) {
                    UnitSelector.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            System.out.print("dropbox");
                            //grab selection from dropdown box
                            String user;
                            user = UnitSelector.getSelectedItem().toString();
                            if (user == null) {
                                ItemSelector.setModel(new DefaultComboBoxModel(ITEMS.toArray()));
                                ItemSelector.setPreferredSize(new Dimension(CONTENT_WIDTH, 32));
                                ItemSelector.setFont(CAPTIONALT);
                            } else {
                                try {

                                    if (SellCheck.isSelected()) {
                                        ArrayList<String> ITEMS = new ArrayList<>(assetData.getOrgAssets(userData.getUserOrgId(user)));
                                        ITEMS.add(0, "-- Select an Asset --");
                                        ItemSelector.setModel(new DefaultComboBoxModel(ITEMS.toArray()));
                                        ItemSelector.setPreferredSize(new Dimension(CONTENT_WIDTH, 32));
                                        ItemSelector.setFont(CAPTIONALT);
                                        ;
                                        ItemSelector.addActionListener(new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                int assetId = 0;
                                                try {
                                                    assetId = assetData.assetId((String) ItemSelector.getSelectedItem());
                                                    int totalQty = assetData.getOrgAssetQty(userData.getUserOrgId(user), assetId);

                                                    QtSlider.setMaximum(totalQty);
                                                    QtSlider.setValue(0);


                                                } catch (SQLException throwables) {
                                                    throwables.printStackTrace();
                                                }


                                            }

                                        });
                                    } else {
                                        QtSlider.setMaximum(999);
                                        QtSlider.setValue(1);
                                    }


                                    /*else{
                                    ItemSelector.removeAll();
                                    UnitSelector.removeAll();
                                    ArrayList<String> USER = new ArrayList<>(userData.getAllUserNames());
                                    USER.add(0, "-- Select a User --");
                                    UnitSelector.setModel(new DefaultComboBoxModel(UNITS.toArray()));
                                    ArrayList<String> ITEMS = new ArrayList<>(assetData.getAssetList());
                                    ITEMS.add(0, "-- Select an Asset --");
                                    ItemSelector.setModel(new DefaultComboBoxModel(ITEMS.toArray()));
                                    ItemSelector.setPreferredSize(new Dimension(CONTENT_WIDTH, 32));
                                    ItemSelector.setFont(CAPTIONALT);

                                }*/


                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }


                            }

                        }
                    });
                }

        // </editor-fold>//


            QtSlider.setMinimum(1);
            QtSlider.setValue(100);
            QtSlider.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_HGAP * 2), TEXT_HEIGHT));
            QtSlider.addChangeListener(e -> {
                setSELECTED_QT(QtSlider.getValue());
                QtValue.setText(String.valueOf(getSELECTED_QT()));
                QtTotalValue.setText(String.valueOf(getTotalPrice()));
            });

            QtTotalContainer.setLayout(new BorderLayout());
            QtTotalContainer.add(QtTotal, BorderLayout.WEST);
            QtTotalContainer.add(QtTotalValue, BorderLayout.CENTER);

            Qt.setLayout(new FlowLayout(FlowLayout.CENTER));
            Qt.setPreferredSize(new Dimension(CONTENT_WIDTH, 100));
            Qt.add(QtHeading);
            Qt.add(QtValue);
            Qt.add(QtTotalContainer);
            Qt.add(QtSlider);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="PRICE">//
            JPanel Price = new JPanel();
            Price.setBorder(BorderFactory.createEtchedBorder());

            JLabel PriceHeading = new JLabel("Price Per Unit");
            PriceHeading.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_HGAP * 2), 30));
            JLabel PriceInputHeading = new JLabel("Your Price (Per Unit):");
            PriceHeading.setFont(SUBTITLE);
            PriceInputHeading.setFont(CAPTION);


            PriceInput = new JTextField("Credits");
            PriceInput.setFont(CAPTION);
            PriceInput.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if ("Credits".equals(PriceInput.getText())) PriceInput.setText("");
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if ("".equals(PriceInput.getText())) PriceInput.setText("Credits");
                    else setSELECTED_PRICE(Integer.parseInt(PriceInput.getText()));
                }
            });

            JPanel PriceContainer = new JPanel();
            PriceContainer.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_HGAP * 4), TEXT_HEIGHT));
            PriceContainer.setLayout(new BorderLayout());
            PriceContainer.add(PriceInput, BorderLayout.CENTER);
            if(!EditStatus) {
                PriceContainer.add(SellCheck, BorderLayout.AFTER_LINE_ENDS);
            }

            PriceInputHeading.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_HGAP * 4),30));

            Price.setPreferredSize(new Dimension(CONTENT_WIDTH, 125));
            Price.add(PriceHeading);
           // Price.add(PriceStatsContainer);
            Price.add(PriceInputHeading);
            Price.add(PriceContainer);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="CONTROLS">//
            JButton BtnSubmit = new JButton();
            JButton BtnCancel = new JButton("Cancel");
            if(EditStatus == true) {
                BtnSubmit = new JButton("Save");
            }else{
                BtnSubmit = new JButton("Submit");
            }
            BtnCancel.setFont(CAPTION);
            BtnSubmit.setFont(CAPTION);
            BtnSubmit.setBackground(SystemColor.activeCaption);
            BtnSubmit.setForeground(SystemColor.activeCaptionText);
            BtnCancel.addActionListener(e -> this.dispose());
            BtnSubmit.addActionListener(e -> {
                try {
                    Submit();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });

            JPanel Controls = new JPanel();
            Controls.setPreferredSize(new Dimension(CONTENT_WIDTH, 30));
            Controls.setLayout(new BorderLayout());
            Controls.add(BtnCancel, BorderLayout.LINE_START);
            Controls.add(BtnSubmit, BorderLayout.LINE_END);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="FRAME CONTENT">//
            JPanel Content = new JPanel();
            Content.setBorder(BorderFactory.createEtchedBorder());
            Content.setMinimumSize(new Dimension(CREATE_WIDTH, CREATE_HEIGHT));
            Content.setPreferredSize(new Dimension(CREATE_WIDTH, CREATE_HEIGHT));
            Content.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            Content.add(Heading);
            Content.add(UnitSelector);
            Content.add(ItemSelector);
            Content.add(Price);
            Content.add(Qt);
            Content.add(Controls);
            // </editor-fold>//

            this.setLayout(new GridBagLayout());
            this.setMinimumSize(new Dimension(CREATE_WIDTH, CREATE_HEIGHT));
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setVisible(true);
            this.setLocationRelativeTo(dashboardPanel);
            this.setAlwaysOnTop(true);

            this.getContentPane().add(Content, new GridBagConstraints());

            if(EditStatus ==true){
                PriceInput.setText(String.valueOf(getEditCredits()));
                setSELECTED_PRICE(selectedOrder.price);
                QtSlider.setValue(selectedOrder.quantity);
                System.out.print(assetData.getAssetName(selectedOrder.assetID));
                System.out.print(selectedOrder.assetID);
                ItemSelector.setSelectedItem(assetData.getAssetName(selectedOrder.assetID));
                QtTotalValue.setText(String.valueOf(selectedOrder.price));
                if(String.valueOf(selectedOrder.type) == "SELL"){
                    SellCheck.setSelected(true);

                }
            }
        }

        private void Submit() throws SQLException {
            userData = new User();

            if (    Objects.equals(UnitSelector.getSelectedItem(), UnitSelector.getItemAt(0)) && !EditStatus ||
                    Objects.equals(ItemSelector.getSelectedItem(), ItemSelector.getItemAt(0)) && !EditStatus
            ){
                JOptionPane.showMessageDialog(this.getContentPane(), "Please select all Dropdowns!","Missing Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (    PriceInput.getText().equals("") || PriceInput.getText().equals("Credits")){
                JOptionPane.showMessageDialog(this.getContentPane(), "Please add a Price!","Missing Price", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!EditStatus && getTotalPrice() > userData.getUserCredits(UnitSelector.getSelectedItem().toString()).intValue() && !SellCheck.isSelected()) {
                // Get credits
                Integer userID = userData.getUserID(UnitSelector.getSelectedItem().toString());
                Integer orgID = OrganisationalUnit.getOrgUnitID(userID);
                Double credits = OrganisationalUnit.getCredits(orgID);

                JOptionPane.showMessageDialog(this.getContentPane(), "You don't have enough credits!\n Available Credits: " + credits, "Not enough credits", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Integer assetID = 0;
            String assetName = (String) ItemSelector.getSelectedItem();
            if (assetName != null){
                assetID = Asset.getAssetID(assetName);
            }
            if (assetID == null){
                assetID = 0;
            }

            Integer userID;
            userID = userData.getUserID(UnitSelector.getSelectedItem().toString());

            if (SellCheck.isSelected() && !EditStatus) {
                if (JOptionPane.showConfirmDialog(this.getContentPane(), "Is this Sell order correct?") == 0) {
                    String org = ItemSelector.getSelectedItem().toString();
                    orderData = new Order(userID, assetID, Order.Type.SELL, SELECTED_QT, getTotalPrice());
                    orderData.addOrder(orderData);
                    OrderTable.setModel(orderData.getAllOrders());
                    this.dispose();
                }
            }

            if (!SellCheck.isSelected() && !EditStatus) {
                if (JOptionPane.showConfirmDialog(this.getContentPane(), "Is this Buy order correct?") == 0) {
                    orderData = new Order(userID, assetID, Order.Type.BUY, SELECTED_QT, getTotalPrice());
                    orderData.addOrder(orderData);
                    OrderTable.setModel(orderData.getAllOrders());
                    this.dispose();
                }
            }

            if (EditStatus == true) {
                if (JOptionPane.showConfirmDialog(this.getContentPane(), "Is this order correct?") == 0) {
                    int tempItem = 0;
                    if (Objects.equals(ItemSelector.getSelectedItem(), ItemSelector.getItemAt(0))) {
                        tempItem = selectedOrder.assetID;
                    } else {
                        tempItem = assetID;
                    }

                    int tempType = 0;
                    if (SellCheck.isSelected()) {
                        tempType = 2;
                    }
                    if (!SellCheck.isSelected()) {
                        tempType = 1;
                    }

                    System.out.print(userID + "\n" + tempItem + "\n" + tempType + "\n" + SELECTED_QT + "\n" + getTotalPrice() + "\n" + selectedOrder.orderId);
                    orderData.updateOrder(selectedOrder.userID, tempItem, tempType, SELECTED_QT, getTotalPrice(), selectedOrder.orderId);
                    OrderTable.setModel(orderData.getAllOrders());
                    this.dispose();
                }
            }
        }

        private int getTotalPrice() {
            return getSELECTED_QT() * getSELECTED_PRICE();
        }

        private int getEditCredits() {
            return selectedOrder.price / selectedOrder.quantity;
        }

        public void setSELECTED_QT(int SELECTED_QT) {
            this.SELECTED_QT = SELECTED_QT;
        }

        public int getSELECTED_QT() {
            return SELECTED_QT;
        }

        public int getSELECTED_PRICE() {
            return SELECTED_PRICE;
        }

        public void setSELECTED_PRICE(int SELECTED_PRICE) {
            this.SELECTED_PRICE = SELECTED_PRICE;
        }
    }

    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Admin Order Remove">//

    /**
     * Holds methods for removing an order from within the GUI.
     */
    private static class AdminRemoveOrder extends JFrame {
        public AdminRemoveOrder(String title) throws HeadlessException, SQLException {
            int WIDTH = 590;
            int HEIGHT = 220;
            int CONTENT_HGAP = 5;
            int CONTENT_VGAP = 10;
            int CONTENT_WIDTH = 530;

            JLabel Heading = new JLabel();
            Heading = new JLabel("Admin - Remove an Order");
            Heading.setFont(HEADER);

            Heading.setPreferredSize(new Dimension(CONTENT_WIDTH, 64));
            JPanel FormPnl = new JPanel();

            JLabel removeOrderLabel = new JLabel("You are deleting order with order ID " + selectedOrder.orderId);
            removeOrderLabel.setFont(CAPTION);
            FormPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 80));
            FormPnl.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            FormPnl.add(removeOrderLabel);

            JButton BtnCancel = new JButton("Cancel");
            JButton BtnSubmit = new JButton("Remove");
            BtnCancel.setFont(CAPTION);
            BtnSubmit.setFont(CAPTION);
            BtnSubmit.setBackground(SystemColor.activeCaption);
            BtnSubmit.setForeground(SystemColor.activeCaptionText);
            BtnCancel.addActionListener(e -> this.dispose());
            BtnSubmit.addActionListener(e -> {
                try {
                    Submit();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            JPanel ControlsPnl = new JPanel();
            ControlsPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 30));
            ControlsPnl.setLayout(new BorderLayout());
            ControlsPnl.add(BtnCancel, BorderLayout.LINE_START);
            ControlsPnl.add(BtnSubmit, BorderLayout.LINE_END);

            JPanel ContentPnl = new JPanel();
            ContentPnl.setBorder(BorderFactory.createEtchedBorder());
            ContentPnl.setMinimumSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            ContentPnl.add(Heading);
            ContentPnl.add(FormPnl);
            ContentPnl.add(ControlsPnl);

            this.setLayout(new GridBagLayout());
            this.setMinimumSize(new Dimension(WIDTH + (CONTENT_VGAP * 2), HEIGHT + (CONTENT_VGAP * 5)));
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setVisible(true);
            this.setLocationRelativeTo(dashboardPanel);
            this.setAlwaysOnTop(true);

            this.getContentPane().add(ContentPnl, new GridBagConstraints());
        }

        private void Submit() throws SQLException {

            if (JOptionPane.showConfirmDialog(this.getContentPane(), "Are you sure you want to remove this order?") == 0){
                orderData.removeOrder(selectedOrder.orderId);
                OrderTable.setModel(orderData.getAllOrders());
                this.dispose();
                EditStatus = false;
            }
        }
    }
    //</editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Admin Transactions Tab">//
    private void InitAdminTransactions() throws SQLException {
        transactionData = new Transaction();
        userData = new User();
        AdminTransactions.setBackground(PAGE_COLOR);

        JLabel PageHeading = new JLabel("Transactions");
        PageHeading.setFont(HEADER);

        JPanel Tools = new JPanel();
        Tools.setMinimumSize(new Dimension(0, 85));
        Tools.setPreferredSize(new Dimension(0, 85));

        JScrollPane ScrollPane = new JScrollPane();
        TransactionTable.setModel(transactionData.getAllTransactions());

        ScrollPane.setViewportView(TransactionTable);

        GroupLayout layout = new GroupLayout(AdminTransactions);
        AdminTransactions.setLayout(layout);

        TransactionTable.addMouseListener(new MouseAdapter(){
            public void mouseClicked (MouseEvent e){
                selectedTransaction = transactionData.getTransaction(transactionTableSelection(e));
            }


        });

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(PageHeading, GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                                        .addComponent(Tools, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(ScrollPane, GroupLayout.Alignment.TRAILING))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(PageHeading, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Tools, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ScrollPane, GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                                .addContainerGap())
        );

    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="User Page">//
    private void InitUserPage() throws SQLException {
        orderData = new Order();
        UserPage.setBackground(PAGE_COLOR);

        JLabel PageHeading = new JLabel("User Account");
        PageHeading.setFont(HEADER);

        JButton btnCreate = new JButton("Create an Order");
        btnCreate.addActionListener(e ->{EditStatus = false;
            try {
                new CreateOrder("Create Order");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        JButton btnRemove = new JButton("Remove an Order");
        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(nonAdminUserTable.getSelectedRow() < 0){
                    JOptionPane.showMessageDialog(null, "Please select a record to remove");
                }else{
                    try {
                        int orderID = selectedOrder.orderId;
                        Integer status = Order.getOrderStatus(orderID);
                        if (status == 2) {
                            JOptionPane.showMessageDialog(null, "This order cannot be removed, it has been completed.");
                        }
                        else {
                            new UserRemoveOrder("Remove Order");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
        JButton btnResetPw = new JButton("Change Password");
        btnResetPw.addActionListener(e->{
            new PasswordReset("Reset your Password");
        });

        JPanel Tools = new JPanel();
        Tools.setBorder(BorderFactory.createEtchedBorder());
        Tools.setMinimumSize(new Dimension(0, 85));
        Tools.setPreferredSize(new Dimension(0, 85));

        Tools.add(btnCreate);
        Tools.add(btnRemove);
        Tools.add(btnResetPw);

        JScrollPane ScrollPane = new JScrollPane();
        nonAdminUserTable  = new JTable();
        nonAdminUserTable.setModel(orderData.getAllUserOrders(userData.getUserID(currentUser)));

        ScrollPane.setViewportView(nonAdminUserTable);

        GroupLayout layout = new GroupLayout(UserPage);
        UserPage.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(PageHeading, GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                                        .addComponent(Tools, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(ScrollPane, GroupLayout.Alignment.TRAILING))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(PageHeading, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Tools, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ScrollPane, GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                                .addContainerGap())
        );
        nonAdminUserTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedOrder = orderData.getOrder(nonAdminUserTableSelection(e));
                }
        });
    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Order Remove">//

    /**
     * Holds methods for removing order from within the GUI.
     */
    private static class UserRemoveOrder extends JFrame {
        public UserRemoveOrder(String title) throws HeadlessException, SQLException {
            int WIDTH = 590;
            int HEIGHT = 220;
            int CONTENT_HGAP = 5;
            int CONTENT_VGAP = 10;
            int CONTENT_WIDTH = 530;

            JLabel Heading = new JLabel();
            Heading = new JLabel("Remove an Order");
            Heading.setFont(HEADER);

            Heading.setPreferredSize(new Dimension(CONTENT_WIDTH, 64));
            JPanel FormPnl = new JPanel();

            JLabel removeOrderLabel = new JLabel("You are deleting order with order ID " + selectedOrder.orderId);
            removeOrderLabel.setFont(CAPTION);
            FormPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 80));
            FormPnl.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            FormPnl.add(removeOrderLabel);

            JButton BtnCancel = new JButton("Cancel");
            JButton BtnSubmit = new JButton("Remove");
            BtnCancel.setFont(CAPTION);
            BtnSubmit.setFont(CAPTION);
            BtnSubmit.setBackground(SystemColor.activeCaption);
            BtnSubmit.setForeground(SystemColor.activeCaptionText);
            BtnCancel.addActionListener(e -> this.dispose());
            BtnSubmit.addActionListener(e -> {
                try {
                    Submit();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            JPanel ControlsPnl = new JPanel();
            ControlsPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 30));
            ControlsPnl.setLayout(new BorderLayout());
            ControlsPnl.add(BtnCancel, BorderLayout.LINE_START);
            ControlsPnl.add(BtnSubmit, BorderLayout.LINE_END);

            JPanel ContentPnl = new JPanel();
            ContentPnl.setBorder(BorderFactory.createEtchedBorder());
            ContentPnl.setMinimumSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            ContentPnl.add(Heading);
            ContentPnl.add(FormPnl);
            ContentPnl.add(ControlsPnl);

            this.setLayout(new GridBagLayout());
            this.setMinimumSize(new Dimension(WIDTH + (CONTENT_VGAP * 2), HEIGHT + (CONTENT_VGAP * 5)));
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setVisible(true);
            this.setLocationRelativeTo(dashboardPanel);
            this.setAlwaysOnTop(true);

            this.getContentPane().add(ContentPnl, new GridBagConstraints());
        }

        private void Submit() throws SQLException {

            if (JOptionPane.showConfirmDialog(this.getContentPane(), "Are you sure you want to remove this order?") == 0){
                orderData.removeOrder(selectedOrder.orderId);
                nonAdminUserTable.setModel(orderData.getAllUserOrders(userData.getUserID(currentUser)));
                this.dispose();

            }
        }
    }
    //</editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Password Reset">//

    /**
     * Holds methods for resetting the password within the GUI.
     */
    private class PasswordReset extends JFrame{

        //private JTextField NameField;
        private JPasswordField PwdField;
        private JPasswordField PwdConfirmField;

        private Admin adminData;

        public PasswordReset(String title) throws HeadlessException {
            super(title);

            adminData = new Admin();

            int WIDTH = 590;
            int HEIGHT = 250;
            int CONTENT_HGAP = 5;
            int CONTENT_VGAP = 10;
            int CONTENT_WIDTH = 530;

            //NameField = new JTextField();
            PwdField = new JPasswordField();
            PwdConfirmField = new JPasswordField();

            // <editor-fold defaultstate="collapsed" desc="Page Heading">//
            JLabel Heading = new JLabel("Reset Password");
            Heading.setFont(HEADER);
            Heading.setPreferredSize(new Dimension(CONTENT_WIDTH, 64));
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Details">//
            JLabel NameLabel = new JLabel("User Name: ");
            NameLabel.setFont(CAPTION);
            JPanel NamePanel = new JPanel();
            NamePanel.setLayout(new BorderLayout());
            NamePanel.add(NameLabel, BorderLayout.BEFORE_LINE_BEGINS);
            //NamePanel.add(NameField, BorderLayout.CENTER);

            JLabel PwdLabel = new JLabel("New Password: ");
            PwdLabel.setFont(CAPTION);
            JPanel PwdPanel = new JPanel();
            PwdPanel.setLayout(new BorderLayout());
            PwdPanel.add(PwdLabel, BorderLayout.BEFORE_LINE_BEGINS);
            PwdPanel.add(PwdField, BorderLayout.CENTER);

            JLabel PwdConfirmLabel = new JLabel("Confirm Password: ");
            PwdConfirmLabel.setFont(CAPTION);
            JPanel PwdConfirmPanel = new JPanel();
            PwdConfirmPanel.setLayout(new BorderLayout());
            PwdConfirmPanel.add(PwdConfirmLabel, BorderLayout.BEFORE_LINE_BEGINS);
            PwdConfirmPanel.add(PwdConfirmField, BorderLayout.CENTER);


            NameLabel.setPreferredSize(new Dimension(CONTENT_WIDTH / 5, 22));
            //NameField.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_WIDTH / 5), 22));

            PwdLabel.setPreferredSize(new Dimension(CONTENT_WIDTH / 5, 22));
            PwdField.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_WIDTH / 5), 22));

            PwdConfirmLabel.setPreferredSize(new Dimension(CONTENT_WIDTH / 5, 22));
            PwdConfirmField.setPreferredSize(new Dimension(CONTENT_WIDTH - (CONTENT_WIDTH / 5), 22));
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Form">//
            JPanel FormPnl = new JPanel();
            FormPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 100));
            FormPnl.setLayout(new FlowLayout( FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            FormPnl.add(NamePanel);
            FormPnl.add(PwdPanel);
            FormPnl.add(PwdConfirmPanel);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Controls">//
            JButton BtnCancel = new JButton("Cancel");
            JButton BtnSubmit = new JButton("Save");

            BtnCancel.setFont(CAPTION);
            BtnSubmit.setFont(CAPTION);
            BtnSubmit.setBackground(SystemColor.activeCaption);
            BtnSubmit.setForeground(SystemColor.activeCaptionText);

            BtnCancel.addActionListener(e -> this.dispose());
            BtnSubmit.addActionListener(e-> Submit());

            JPanel ControlsPnl = new JPanel();
            ControlsPnl.setPreferredSize(new Dimension(CONTENT_WIDTH, 30));
            ControlsPnl.setLayout(new BorderLayout());
            ControlsPnl.add(BtnCancel, BorderLayout.LINE_START);
            ControlsPnl.add(BtnSubmit, BorderLayout.LINE_END);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="Frame Content">//
            JPanel ContentPnl = new JPanel();
            ContentPnl.setBorder(BorderFactory.createEtchedBorder());
            ContentPnl.setMinimumSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            ContentPnl.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            ContentPnl.add(Heading);
            ContentPnl.add(FormPnl);
            ContentPnl.add(ControlsPnl);

            // </editor-fold>//

            this.setLayout(new GridBagLayout());
            this.setMinimumSize(new Dimension(WIDTH + (CONTENT_VGAP * 2), HEIGHT + (CONTENT_VGAP * 5)));
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setVisible(true);
            this.setLocationRelativeTo(dashboardPanel);
            this.setAlwaysOnTop(true);

            this.getContentPane().add(ContentPnl, new GridBagConstraints());

        }

        private void Submit() {


            if (!PwdField.getText().equals(PwdConfirmField.getText())){
                JOptionPane.showMessageDialog(this.getContentPane(), "Passwords are not the same!","Password Error", JOptionPane.WARNING_MESSAGE);
                return;
            }


            adminData.setUserPassword(currentUser, PwdField.getText());
            this.dispose();
        }
    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Market Page">//
    private void InitAssetsPage() throws SQLException {
        orderData = new Order();
        AssetsPage.setBackground(PAGE_COLOR);

        JLabel PageHeading = new JLabel("Marketplace");
        PageHeading.setFont(HEADER);

        JButton btnCreate = new JButton("Create an Order");
        btnCreate.addActionListener(e ->{EditStatus = false;
            try {
                new CreateOrder("Create Order");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        JPanel Tools = new JPanel();
        Tools.setBorder(BorderFactory.createEtchedBorder());
        Tools.setMinimumSize(new Dimension(0, 85));
        Tools.setPreferredSize(new Dimension(0, 85));

        Tools.add(btnCreate);

        JScrollPane ScrollPane = new JScrollPane();

        MarketPlaceTable = new JTable();
        MarketPlaceTable.setModel(orderData.getAllDashOrders());

        ScrollPane.setViewportView(MarketPlaceTable);

        GroupLayout layout = new GroupLayout(AssetsPage);
        AssetsPage.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(PageHeading, GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                                        .addComponent(Tools, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(ScrollPane, GroupLayout.Alignment.TRAILING))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(PageHeading, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Tools, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ScrollPane, GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                                .addContainerGap())
        );
    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Organisational Unit Page">//
    private void InitOrgUnitPage() throws SQLException {

        orderData = new Order();

        // <editor-fold defaultstate="collapsed" desc="Heading">//
        JLabel pageHeading = new JLabel(userData.getOrgName());
        pageHeading.setFont(HEADER);
        // </editor-fold>//

        // <editor-fold defaultstate="collapsed" desc="Unit Details">//
        JLabel detailsHeading = new JLabel(" Credits");
        detailsHeading.setFont(TITLE);

        JLabel availableLabel = new JLabel("Available:");
        availableLabel.setFont(SUBTITLEALT);

        JLabel availableValue = new JLabel(userData.getUserCredits(currentUser).toString());
        availableValue.setFont(SUBTITLEALT);

        JPanel availableCredits = new JPanel();
        availableCredits.setBackground(null);
        availableCredits.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 0));
        availableCredits.add(availableLabel);
        availableCredits.add(availableValue);

        JLabel balanceLabel = new JLabel("Pending:");
        balanceLabel.setFont(SUBTITLEALT);

        JLabel balanceValue = new JLabel("-"+orgData.getPending(userData.getUserOrgId(currentUser)).toString());
        balanceValue.setFont(SUBTITLEALT);

        JPanel balanceCredits = new JPanel();
        balanceCredits.setBackground(null);
        balanceCredits.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 0));
        balanceCredits.add(balanceLabel);
        balanceCredits.add(balanceValue);

        JPanel detailsContent = new JPanel();
        detailsContent.setBackground(null);
        detailsContent.setLayout(new FlowLayout(FlowLayout.LEADING, 20, 0));
        detailsContent.add(availableCredits);
        detailsContent.add(balanceCredits);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setBorder(BorderFactory.createEtchedBorder());
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.add(detailsHeading, BorderLayout.NORTH);
        detailsPanel.add(detailsContent, BorderLayout.CENTER);
        // </editor-fold>//

        // <editor-fold defaultstate="collapsed" desc="Unit Orders">//
        JPanel ordersPanel = new JPanel();

        JScrollPane ScrollPane = new JScrollPane();
        System.out.println();
        OrgDashTable.setModel(orderData.getAllOrgOrders(userData.getUserOrg(currentUser)));

        ScrollPane.setViewportView(OrgDashTable);

        ordersPanel.setLayout(new BorderLayout());
        ordersPanel.add(ScrollPane, BorderLayout.CENTER);
        // </editor-fold>//

        // <editor-fold defaultstate="collapsed" desc="Controls">//
        JButton btnHistory = new JButton("History");
        JButton btnCreateOrder = new JButton("Create Order");
        btnCreateOrder.addActionListener(e ->{EditStatus = false;
            try {
                new CreateOrder("Create Order");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        btnHistory.addActionListener(e -> {
            try {
                new History("Order History");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        // </editor-fold>//

        // <editor-fold defaultstate="collapsed" desc="Page Layout">//
        OrgUnitPage.setBackground(PAGE_COLOR);
        GroupLayout layout = new GroupLayout(OrgUnitPage);
        OrgUnitPage.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(pageHeading)
                                .addComponent(detailsPanel)
                                .addComponent(ordersPanel)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnHistory)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
                                        .addComponent(btnCreateOrder)
                                )
                        )
                        .addContainerGap()
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(pageHeading)
                                .addGap(18)
                                .addComponent(detailsPanel, GroupLayout.DEFAULT_SIZE, 80, 80)
                                .addGap(18)
                                .addComponent(ordersPanel)
                                .addGap(18)
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(btnHistory)
                                        .addComponent(btnCreateOrder)
                                )
                                .addContainerGap()
                        )
        );
        // </editor-fold>//

    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Order History">//

    /**
     * Holds methods for the order history within the GUI.
     */
    private class History extends  JFrame {

        public History(String title) throws HeadlessException, SQLException {
            super(title);

            Order orderData = new Order();

            int FRAME_WIDTH = 590;
            int FRAME_HEIGHT = 500;
            int CONTENT_HGAP = 5;
            int CONTENT_VGAP = 10;
            int CONTENT_WIDTH = 530;



            // <editor-fold defaultstate="collapsed" desc="HEADING">//
            JLabel Heading = new JLabel("Order History");
            Heading.setFont(HEADER);
            Heading.setPreferredSize(new Dimension(CONTENT_WIDTH, 64));
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="TABLE">//
            JScrollPane ScrollPane = new JScrollPane();
            JTable historyTable = new JTable();
            historyTable.setModel(orderData.getOrgOrderHistory(userData.getUserOrg(currentUser)));

            ScrollPane.setViewportView(historyTable);

            ScrollPane.setPreferredSize(new Dimension(CONTENT_WIDTH, 325));
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="CONTROLS">//
            JButton BtnClose = new JButton("Close");
            BtnClose.setFont(CAPTION);
            BtnClose.addActionListener(e -> this.dispose());

            JPanel Controls = new JPanel();
            Controls.setPreferredSize(new Dimension(CONTENT_WIDTH, 30));
            Controls.setLayout(new BorderLayout());
            Controls.add(BtnClose, BorderLayout.LINE_END);
            // </editor-fold>//

            // <editor-fold defaultstate="collapsed" desc="FRAME CONTENT">//
            JPanel Content = new JPanel();
            Content.setBorder(BorderFactory.createEtchedBorder());
            Content.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
            Content.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
            Content.setLayout(new FlowLayout(FlowLayout.CENTER, CONTENT_HGAP, CONTENT_VGAP));

            Content.add(Heading);
            Content.add(ScrollPane);
            Content.add(Controls);
            // </editor-fold>//

            this.setLayout(new GridBagLayout());
            this.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setVisible(true);
            this.setLocationRelativeTo(dashboardPanel);
            this.setAlwaysOnTop(true);

            this.getContentPane().add(Content, new GridBagConstraints());
        }
    }
    // </editor-fold>//

    // <editor-fold defaultstate="collapsed" desc="Table Selection Methods">//

    /**
     * Gets the selection on the asset table.
     *
     * @param e The mouse click event.
     * @return The ID of the asset selected.
     */
    public int assetTableSelection(MouseEvent e){
        int index = AssetTable.getSelectedRow();
        TableModel model = AssetTable.getModel();
        int assetId = (int) model.getValueAt(index,0);


        return assetId;

    }

    /**
     * Gets the selection on the order table.
     *
     * @param e The mouse click event.
     * @return The ID of the order selected.
     */
    public int orderTableSelection(MouseEvent e){
        int index = OrderTable.getSelectedRow();
        TableModel model = OrderTable.getModel();
        int orderId = (int) model.getValueAt(index,0);

        return orderId;

    }

    /**
     * Gets the selection on the transaction table.
     *
     * @param e The mouse click event.
     * @return The ID of the transaction selected.
     */
    public int transactionTableSelection(MouseEvent e){
        int index = TransactionTable.getSelectedRow();
        TableModel model = TransactionTable.getModel();
        int transactionId = (int) model.getValueAt(index,0);

        return transactionId;

    }

    /**
     * Gets the selection on the organisational unit table.
     *
     * @param e The mouse click event.
     * @return The ID of the organization selected.
     */
    public String unitsTableSelection(MouseEvent e){
        int index = UnitTable.getSelectedRow();
        TableModel model = UnitTable.getModel();
        String UnitName = (String) model.getValueAt(index,0);
        System.out.println(UnitName);
        return UnitName;

    }

    /**
     * Gets the selection on the (non admin) user table.
     *
     * @param e The mouse click event.
     * @return The ID of the user selected.
     */
    public int nonAdminUserTableSelection(MouseEvent e){
        int index = nonAdminUserTable.getSelectedRow();
        TableModel model = nonAdminUserTable.getModel();
        int orderId = (int) model.getValueAt(index,0);

        return orderId;

    }

    //</editor-fold>
}