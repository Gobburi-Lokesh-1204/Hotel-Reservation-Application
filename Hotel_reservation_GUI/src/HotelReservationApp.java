import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
public class HotelReservationApp extends JFrame implements ActionListener {
        private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
        private static final String username = "root";
        private static final String password = "******";

        private static final String VALID_USERNAME = "John";
        private static final String VALID_PASSWORD = "******";

        private static final Logger logger = Logger.getLogger(HotelReservationApp.class.getName());

        private Connection con;
        private JTextField userField;
        private JPasswordField passField;
        private CardLayout cardLayout;
        private JPanel mainPanel, loginPanel, menuPanel;

        public HotelReservationApp() {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(url, username, password);
            } catch (ClassNotFoundException | SQLException e) {
                logger.log(Level.SEVERE, "Exception while connecting to database", e);
                JOptionPane.showMessageDialog(null, "Database Connection Error", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            cardLayout = new CardLayout();
            mainPanel = new JPanel(cardLayout);
            initLoginPanel();
            initMenuPanel();

            // Add panels to main panel
            mainPanel.add(loginPanel, "Login");
            mainPanel.add(menuPanel, "Menu");

            // Set the main panel as the content pane
            setContentPane(mainPanel);

            // Show the login panel initially
            cardLayout.show(mainPanel, "Login");

            setTitle("Hotel Reservation System");
            setSize(300, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
        }

        private void initLoginPanel() {
            loginPanel = new JPanel();
            loginPanel.setLayout(new GridLayout(4, 2));
             loginPanel.setBackground(Color.yellow);
             loginPanel.setLayout(new FlowLayout());
            JLabel userLabel = new JLabel("Username:");
            JLabel passLabel = new JLabel("Password:");

            userField = new JTextField(20);
            passField = new JPasswordField(20);

            JButton loginButton = new JButton("Login");
            loginButton.addActionListener(this);

            loginPanel.add(userLabel);
            loginPanel.add(userField);
            loginPanel.add(passLabel);
            loginPanel.add(passField);
            loginPanel.add(new JLabel(""));
            loginPanel.add(loginButton);
        }

        private void initMenuPanel() {
            menuPanel = new JPanel();
            menuPanel.setLayout(new GridLayout(7, 1));

            JLabel title = new JLabel("Hotel Reservation System", SwingConstants.CENTER);

            JButton reserveButton = new JButton("Reserve a room");
            JButton viewButton = new JButton("View Reservations");
            JButton getRoomButton = new JButton("Get Room number");
            JButton updateButton = new JButton("Update reservations");
            JButton deleteButton = new JButton("Delete Reservations");
            JButton exitButton = new JButton("Exit");

            Dimension buttonSize = new Dimension(20, 20);
            reserveButton.setPreferredSize(buttonSize);
            viewButton.setPreferredSize(buttonSize);
            getRoomButton.setPreferredSize(buttonSize);
            updateButton.setPreferredSize(buttonSize);
            deleteButton.setPreferredSize(buttonSize);
            exitButton.setPreferredSize(buttonSize);

            reserveButton.addActionListener(this);
            viewButton.addActionListener(this);
            getRoomButton.addActionListener(this);
            updateButton.addActionListener(this);
            deleteButton.addActionListener(this);
            exitButton.addActionListener(this);

            menuPanel.add(title);
            menuPanel.add(reserveButton);
            menuPanel.add(viewButton);
            menuPanel.add(getRoomButton);
            menuPanel.add(updateButton);
            menuPanel.add(deleteButton);
            menuPanel.add(exitButton);
        }

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("Login")) {
                String enteredUsername = userField.getText();
                String enteredPassword = new String(passField.getPassword());

                if (VALID_USERNAME.equals(enteredUsername) && VALID_PASSWORD.equals(enteredPassword)) {
                    cardLayout.show(mainPanel, "Menu");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (command.equals("Reserve a room")) {
                reserveRoom();
            } else if (command.equals("View Reservations")) {
                viewReservations();
            } else if (command.equals("Get Room number")) {
                getRoomNumber();
            } else if (command.equals("Update reservations")) {
                updateReservations();
            } else if (command.equals("Delete Reservations")) {
                deleteReservations();
            } else if (command.equals("Exit")) {
                System.exit(0);
            }
        }

        private void reserveRoom() {
            JTextField nameField = new JTextField();
            JTextField roomField = new JTextField();
            JTextField contactField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Guest Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Room Number:"));
            panel.add(roomField);
            panel.add(new JLabel("Contact Number:"));
            panel.add(contactField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Reserve Room", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText();
                int roomNumber;
                try {
                    roomNumber = Integer.parseInt(roomField.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid room number", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String contact = contactField.getText();

                String query = "INSERT INTO reservations (guest_name, room_number, contact_number) VALUES (?, ?, ?)";
                try (PreparedStatement st = con.prepareStatement(query)) {
                    st.setString(1, name);
                    st.setInt(2, roomNumber);
                    st.setString(3, contact);
                    int affectedRows = st.executeUpdate();
                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(this, "Reservation Successful");
                    } else {
                        JOptionPane.showMessageDialog(this, "Reservation Failed");
                    }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "SQL Exception occurred", e);
                    JOptionPane.showMessageDialog(this, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void viewReservations() {
            String query = "SELECT reserve_id, guest_name, room_number, contact_number, reserved_date FROM reservations";
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(query)) {

                List<String[]> data = new ArrayList<>();
                while (rs.next()) {
                    data.add(new String[]{
                            String.valueOf(rs.getInt("reserve_id")),
                            rs.getString("guest_name"),
                            String.valueOf(rs.getInt("room_number")),
                            rs.getString("contact_number"),
                            rs.getString("reserved_date")
                    });
                }

                String[][] dataArray = data.toArray(new String[0][]);
                JTable table = new JTable(dataArray, new String[]{"Reservation Id", "Guest Name", "Room Number", "Contact Number", "Reserved Date"});
                JOptionPane.showMessageDialog(this, new JScrollPane(table), "Current Reservations", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                logger.log(Level.SEVERE, "SQL Exception occurred", e);
                JOptionPane.showMessageDialog(this, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void getRoomNumber() {
            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Reservation ID:"));
            panel.add(idField);
            panel.add(new JLabel("Guest Name:"));
            panel.add(nameField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Get Room Number", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                int id;
                try {
                    id = Integer.parseInt(idField.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid reservation ID", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String name = nameField.getText();

                String sql = "SELECT room_number FROM reservations WHERE reserve_id=? AND guest_name=?";
                try (PreparedStatement pep = con.prepareStatement(sql)) {
                    pep.setInt(1, id);
                    pep.setString(2, name);

                    ResultSet rs = pep.executeQuery();
                    if (!rs.isBeforeFirst()) {
                        JOptionPane.showMessageDialog(this, "Room number not found for entered ID and name", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        while (rs.next()) {
                            JOptionPane.showMessageDialog(this, "Room Number for Guest " + name + " is: " + rs.getInt("room_number"));
                        }
                    }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "SQL Exception occurred", e);
                    JOptionPane.showMessageDialog(this, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void updateReservations() {
            JTextField idField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField roomField = new JTextField();
            JTextField contactField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Reservation ID to update:"));
            panel.add(idField);
            panel.add(new JLabel("Guest Name:"));
            panel.add(nameField);
            panel.add(new JLabel("New Room Number:"));
            panel.add(roomField);
            panel.add(new JLabel("Contact Number:"));
            panel.add(contactField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Update Reservations", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                int id;
                try {
                    id = Integer.parseInt(idField.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid reservation ID", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String name = nameField.getText();
                int roomNumber;
                try {
                    roomNumber = Integer.parseInt(roomField.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid room number", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String contact = contactField.getText();

                if (!reservationExists(id)) {
                    JOptionPane.showMessageDialog(this, "Reservation not found for the given ID and guest name", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = "UPDATE reservations SET guest_name=?, room_number=?, contact_number=? WHERE reserve_id=?";
                try (PreparedStatement st = con.prepareStatement(sql)) {
                    st.setString(1, name);
                    st.setInt(2, roomNumber);
                    st.setString(3, contact);
                    st.setInt(4, id);

                    int affectedRows = st.executeUpdate();
                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(this, "Reservation updated successfully");
                    } else {
                        JOptionPane.showMessageDialog(this, "Reservation update failed");
                    }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "SQL Exception occurred", e);
                    JOptionPane.showMessageDialog(this, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void deleteReservations() {
            JTextField idField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Reservation ID to delete:"));
            panel.add(idField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Delete Reservations", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                int id;
                try {
                    id = Integer.parseInt(idField.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid reservation ID", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!reservationExists(id)) {
                    JOptionPane.showMessageDialog(this, "Reservation not found for the given ID", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = "DELETE FROM reservations WHERE reserve_id=?";
                try (PreparedStatement st = con.prepareStatement(sql)) {
                    st.setInt(1, id);

                    int affectedRows = st.executeUpdate();
                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(this, "Reservation deleted successfully");
                    } else {
                        JOptionPane.showMessageDialog(this, "Reservation deletion failed");
                    }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "SQL Exception occurred", e);
                    JOptionPane.showMessageDialog(this, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private boolean reservationExists(int id) {
            String sql = "SELECT reserve_id FROM reservations WHERE reserve_id=?";
            try (PreparedStatement st = con.prepareStatement(sql)) {
                st.setInt(1, id);
                ResultSet rs = st.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "SQL Exception occurred", e);
                JOptionPane.showMessageDialog(this, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                new HotelReservationApp().setVisible(true);
            });
        }
    }
