import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class JDBC_GUI extends JFrame {
    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/guidbms";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // GUI components
    private JTextArea resultArea;
    private JButton insertButton, updateButton, deleteButton, selectButton, alterButton, showAllButton;

    private Connection connection;

    public JDBC_GUI() {
        // Initialize GUI
        setTitle("JDBC CRUD Operations");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Background Image Panel
        BackgroundPanel backgroundPanel = new BackgroundPanel("image.jpg");
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 6));
        insertButton = new JButton("Insert");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        selectButton = new JButton("Select");
        alterButton = new JButton("Alter");
        showAllButton = new JButton("Show All");

        buttonPanel.add(insertButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(selectButton);
        buttonPanel.add(alterButton);
        buttonPanel.add(showAllButton);

        backgroundPanel.add(buttonPanel, BorderLayout.NORTH);

        // Result Area
        resultArea = new JTextArea();
        backgroundPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Event Listeners
        insertButton.addActionListener(e -> showInsertDialog());
        updateButton.addActionListener(e -> showUpdateDialog());
        deleteButton.addActionListener(e -> showDeleteDialog());
        selectButton.addActionListener(e -> selectData());
        alterButton.addActionListener(e -> showAlterDialog());
        showAllButton.addActionListener(e -> showAllData());

        // Establish Database Connection
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            resultArea.append("Database connected successfully!\n");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // Show Insert Dialog
    private void showInsertDialog() {
        JDialog insertDialog = new JDialog(this, "Insert Data", true);
        insertDialog.setSize(300, 200);
        insertDialog.setLayout(new GridLayout(4, 2));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();

        insertDialog.add(new JLabel("ID:"));
        insertDialog.add(idField);
        insertDialog.add(new JLabel("Name:"));
        insertDialog.add(nameField);
        insertDialog.add(new JLabel("Age:"));
        insertDialog.add(ageField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            insertData(idField.getText(), nameField.getText(), ageField.getText());
            insertDialog.dispose();
        });
        insertDialog.add(submitButton);

        insertDialog.setVisible(true);
    }

    // Insert data into the database
    private void insertData(String id, String name, String age) {
        String sql = "INSERT INTO datatable (id, name, age) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            stmt.setString(2, name);
            stmt.setInt(3, Integer.parseInt(age));
            int rows = stmt.executeUpdate();
            resultArea.append(rows + " row(s) inserted.\n");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // Show Update Dialog
    private void showUpdateDialog() {
        JDialog updateDialog = new JDialog(this, "Update Data", true);
        updateDialog.setSize(300, 200);
        updateDialog.setLayout(new GridLayout(4, 2));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();

        updateDialog.add(new JLabel("ID (to update):"));
        updateDialog.add(idField);
        updateDialog.add(new JLabel("New Name:"));
        updateDialog.add(nameField);
        updateDialog.add(new JLabel("New Age:"));
        updateDialog.add(ageField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            updateData(idField.getText(), nameField.getText(), ageField.getText());
            updateDialog.dispose();
        });
        updateDialog.add(submitButton);

        updateDialog.setVisible(true);
    }

    // Update data in the database
    private void updateData(String id, String name, String age) {
        String sql = "UPDATE datatable SET name = ?, age = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, Integer.parseInt(age));
            stmt.setInt(3, Integer.parseInt(id));
            int rows = stmt.executeUpdate();
            resultArea.append(rows + " row(s) updated.\n");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // Show Delete Dialog
    private void showDeleteDialog() {
        JDialog deleteDialog = new JDialog(this, "Delete Data", true);
        deleteDialog.setSize(300, 100);
        deleteDialog.setLayout(new GridLayout(2, 2));

        JTextField idField = new JTextField();

        deleteDialog.add(new JLabel("ID (to delete):"));
        deleteDialog.add(idField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            deleteData(idField.getText());
            deleteDialog.dispose();
        });
        deleteDialog.add(submitButton);

        deleteDialog.setVisible(true);
    }

    // Delete data from the database
    private void deleteData(String id) {
        String sql = "DELETE FROM datatable WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(id));
            int rows = stmt.executeUpdate();
            resultArea.append(rows + " row(s) deleted.\n");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // Show Alter Dialog
    private void showAlterDialog() {
        JDialog alterDialog = new JDialog(this, "Alter Table", true);
        alterDialog.setSize(300, 150);
        alterDialog.setLayout(new GridLayout(2, 2));

        JTextField alterCommandField = new JTextField();

        alterDialog.add(new JLabel("SQL Alter Command:"));
        alterDialog.add(alterCommandField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            alterTable(alterCommandField.getText());
            alterDialog.dispose();
        });
        alterDialog.add(submitButton);

        alterDialog.setVisible(true);
    }

    // Alter table structure
    private void alterTable(String sql) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            resultArea.append("Table altered successfully.\n");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // Select and display data from the database
    private void selectData() {
        String sql = "SELECT * FROM datatable";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            resultArea.append("ID\tName\tAge\n");
            while (rs.next()) {
                resultArea.append(rs.getInt("id") + "\t" + rs.getString("name") + "\t" + rs.getInt("age") + "\n");
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // Show all data from the database
    private void showAllData() {
        resultArea.setText(""); // Clear the result area
        selectData();
    }

    // Display SQL error messages
    private void showError(SQLException ex) {
        resultArea.append("Error: " + ex.getMessage() + "\n");
    }

    // Background Panel class for custom painting
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                System.err.println("Background image not found: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new JDBC_GUI().setVisible(true);
        });
    }
}
