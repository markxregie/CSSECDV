/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Controller.SQLite;
import Model.Product;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author beepxD
 */
public class MgmtProduct extends javax.swing.JPanel {

    public SQLite sqlite;
    public DefaultTableModel tableModel;
    private String username;
    
    public MgmtProduct(SQLite sqlite) {
        initComponents();
        this.sqlite = sqlite;
        tableModel = (DefaultTableModel)table.getModel();
        table.getTableHeader().setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 14));

        // Add mouse listener for double-click to purchase
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    purchaseProduct();
                }
            }
        });

//        UNCOMMENT TO DISABLE BUTTONS
//        purchaseBtn.setVisible(false);
//        addBtn.setVisible(false);
//        editBtn.setVisible(false);
//        deleteBtn.setVisible(false);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String getCurrentTimestamp() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new java.util.Date());
    }

    private void updateProductStock(String productName, int quantityPurchased) {
        Product product = sqlite.getProduct(productName);
        if (product != null) {
            int newStock = product.getStock() - quantityPurchased;
            if (newStock < 0) newStock = 0;
            String sql = "UPDATE product SET stock = " + newStock + " WHERE name = '" + productName + "'";
            try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:database.db");
                 java.sql.Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sql);
            } catch (Exception ex) {
                System.out.println("Error updating product stock: " + ex.getMessage());
            }
        }
    }

    private void refreshTable() {
        // Clear and reload product table
        for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
            tableModel.removeRow(i);
        }
        java.util.ArrayList<Product> products = sqlite.getProduct();
        for (Product p : products) {
            tableModel.addRow(new Object[]{p.getName(), p.getStock(), p.getPrice()});
        }
    }

    private void refreshHistory() {
        // If mgmtHistory panel is accessible, refresh it
        // This requires a reference to mgmtHistory panel, which may not be available here
        // So this can be handled in ClientHome or Frame after purchase
    }

    private void savePurchase(String productName, int quantity) {
        String timestamp = getCurrentTimestamp();
        sqlite.addHistory(username, productName, quantity, timestamp);
    }

    private void purchaseBtnActionPerformed(java.awt.event.ActionEvent evt) {
        if (table.getSelectedRow() >= 0) {
            String productName = (String) tableModel.getValueAt(table.getSelectedRow(), 0);
            JTextField stockFld = new JTextField("0");
            designer(stockFld, "PRODUCT STOCK");

            Object[] message = {
                "How many " + productName + " do you want to purchase?", stockFld
            };

            int result = javax.swing.JOptionPane.showConfirmDialog(null, message, "PURCHASE PRODUCT", javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE, null);

            if (result == javax.swing.JOptionPane.OK_OPTION) {
                try {
                    int quantity = Integer.parseInt(stockFld.getText());
                    if (quantity <= 0) {
                        javax.swing.JOptionPane.showMessageDialog(null, "Please enter a positive quantity.", "Invalid Quantity", javax.swing.JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Product product = sqlite.getProduct(productName);
                    if (product == null) {
                        javax.swing.JOptionPane.showMessageDialog(null, "Product not found.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (quantity > product.getStock()) {
                        javax.swing.JOptionPane.showMessageDialog(null, "Insufficient stock.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Save purchase to history
                    savePurchase(productName, quantity);
                    // Update product stock in database
                    updateProductStock(productName, quantity);
                    // Refresh product table
                    refreshTable();
                    // Show success message
                    javax.swing.JOptionPane.showMessageDialog(null, "Purchase successful!", "Success", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException e) {
                    javax.swing.JOptionPane.showMessageDialog(null, "Invalid quantity entered.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void setClientMode(boolean isClient) {
        if (isClient) {
            addBtn.setVisible(false);
            editBtn.setVisible(false);
            deleteBtn.setVisible(false);
            purchaseBtn.setVisible(true);
        } else {
            addBtn.setVisible(true);
            editBtn.setVisible(true);
            deleteBtn.setVisible(true);
            purchaseBtn.setVisible(true);
        }
    }

    public void setManagerMode(boolean isManager) {
        if (isManager) {
            purchaseBtn.setVisible(false);
        } else {
            purchaseBtn.setVisible(true);
        }
    }

    public void setStaffMode(boolean isStaff) {
        if (isStaff) {
            purchaseBtn.setVisible(false);
            addBtn.setVisible(true);
            editBtn.setVisible(true);
            deleteBtn.setVisible(true);
        } else {
            purchaseBtn.setVisible(true);
            addBtn.setVisible(true);
            editBtn.setVisible(true);
            deleteBtn.setVisible(true);
        }
    }

    public void init(){
        //      CLEAR TABLE
        for(int nCtr = tableModel.getRowCount(); nCtr > 0; nCtr--){
            tableModel.removeRow(0);
        }
        
//      LOAD CONTENTS
        ArrayList<Product> products = sqlite.getProduct();
        for(int nCtr = 0; nCtr < products.size(); nCtr++){
            tableModel.addRow(new Object[]{
                products.get(nCtr).getName(), 
                products.get(nCtr).getStock(), 
                products.get(nCtr).getPrice()});
        }
    }
    
    public void designer(JTextField component, String text){
        component.setSize(70, 600);
        component.setFont(new java.awt.Font("Tahoma", 0, 18));
        component.setBackground(new java.awt.Color(240, 240, 240));
        component.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        component.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true), text, javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12)));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        purchaseBtn = new javax.swing.JButton();
        addBtn = new javax.swing.JButton();
        editBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();

        table.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Name", "Stock", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setMinWidth(50);
            table.getColumnModel().getColumn(1).setMaxWidth(100);
            table.getColumnModel().getColumn(2).setMaxWidth(100);
        }

        purchaseBtn.setBackground(new java.awt.Color(255, 255, 255));
        purchaseBtn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        purchaseBtn.setText("PURCHASE");
        purchaseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                purchaseBtnActionPerformed(evt);
            }
        });

        addBtn.setBackground(new java.awt.Color(255, 255, 255));
        addBtn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        addBtn.setText("ADD");
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });

        editBtn.setBackground(new java.awt.Color(255, 255, 255));
        editBtn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        editBtn.setText("EDIT");
        editBtn.setToolTipText("");
        editBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBtnActionPerformed(evt);
            }
        });

        deleteBtn.setBackground(new java.awt.Color(255, 255, 255));
        deleteBtn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        deleteBtn.setText("DELETE");
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(purchaseBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(addBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(editBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(deleteBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(deleteBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(purchaseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    public void purchaseProduct() {
        if (table.getSelectedRow() >= 0) {
            String productName = (String) tableModel.getValueAt(table.getSelectedRow(), 0);
            JTextField stockFld = new JTextField("0");
            designer(stockFld, "PRODUCT STOCK");

            Object[] message = {
                "How many " + productName + " do you want to purchase?", stockFld
            };

            int result = JOptionPane.showConfirmDialog(null, message, "PURCHASE PRODUCT", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    int quantity = Integer.parseInt(stockFld.getText());
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(null, "Please enter a positive quantity.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Product product = sqlite.getProduct(productName);
                    if (product == null) {
                        JOptionPane.showMessageDialog(null, "Product not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (quantity > product.getStock()) {
                        JOptionPane.showMessageDialog(null, "Insufficient stock.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Save purchase to history
                    savePurchase(productName, quantity);
                    // Update product stock in database
                    updateProductStock(productName, quantity);
                    // Refresh product table
                    refreshTable();
                    // Show success message
                    JOptionPane.showMessageDialog(null, "Purchase successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid quantity entered.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        JTextField nameFld = new JTextField();
        JTextField stockFld = new JTextField();
        JTextField priceFld = new JTextField();

        designer(nameFld, "PRODUCT NAME");
        designer(stockFld, "PRODUCT STOCK");
        designer(priceFld, "PRODUCT PRICE");

        Object[] message = {
            "Insert New Product Details:", nameFld, stockFld, priceFld
        };

        int result = JOptionPane.showConfirmDialog(null, message, "ADD PRODUCT", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameFld.getText().trim();
            String stockStr = stockFld.getText().trim();
            String priceStr = priceFld.getText().trim();

            if (name.isEmpty() || stockStr.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int stock = Integer.parseInt(stockStr);
                double price = Double.parseDouble(priceStr);

                if (stock < 0 || price < 0) {
                    JOptionPane.showMessageDialog(null, "Stock and price must be non-negative.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = sqlite.addProduct(name, stock, price);
                if (success) {
                    refreshTable();
                    JOptionPane.showMessageDialog(null, "Product added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to add product. It may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid number format for stock or price.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_addBtnActionPerformed

    private void editBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBtnActionPerformed
        if(table.getSelectedRow() >= 0){
            String originalName = (String) tableModel.getValueAt(table.getSelectedRow(), 0);
            JTextField nameFld = new JTextField(originalName);
            JTextField stockFld = new JTextField(tableModel.getValueAt(table.getSelectedRow(), 1) + "");
            JTextField priceFld = new JTextField(tableModel.getValueAt(table.getSelectedRow(), 2) + "");

            designer(nameFld, "PRODUCT NAME");
            designer(stockFld, "PRODUCT STOCK");
            designer(priceFld, "PRODUCT PRICE");

            Object[] message = {
                "Edit Product Details:", nameFld, stockFld, priceFld
            };

            int result = JOptionPane.showConfirmDialog(null, message, "EDIT PRODUCT", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);

            if (result == JOptionPane.OK_OPTION) {
                String newName = nameFld.getText().trim();
                String stockStr = stockFld.getText().trim();
                String priceStr = priceFld.getText().trim();

                if (newName.isEmpty() || stockStr.isEmpty() || priceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int stock = Integer.parseInt(stockStr);
                    double price = Double.parseDouble(priceStr);

                    if (stock < 0 || price < 0) {
                        JOptionPane.showMessageDialog(null, "Stock and price must be non-negative.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    boolean success = sqlite.updateProduct(originalName, newName, stock, price);
                    if (success) {
                        refreshTable();
                        JOptionPane.showMessageDialog(null, "Product updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update product.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid number format for stock or price.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_editBtnActionPerformed

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        if(table.getSelectedRow() >= 0){
            int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + tableModel.getValueAt(table.getSelectedRow(), 0) + "?", "DELETE PRODUCT", JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                String name = (String) tableModel.getValueAt(table.getSelectedRow(), 0);
                boolean success = sqlite.deleteProduct(name);
                if (success) {
                    refreshTable();
                    JOptionPane.showMessageDialog(null, "Product deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete product.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_deleteBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JButton editBtn;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton purchaseBtn;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
