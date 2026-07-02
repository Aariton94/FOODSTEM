/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package foodstem.frame;

import foodstem.service.InventoryService;
import java.awt.CardLayout;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.sql.Connection;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.SwingConstants;


/**
 *
 * @author andrei
 */
public class ManagmentSystem extends javax.swing.JFrame {
    
    private final InventoryService inventoryService = new InventoryService();
    private StringBuilder currentAmount = new StringBuilder();

    private static final String CARD_WRAP = "WRAP";
    private static final String CARD_DESSERT = "DESSERT";
    private static final String CARD_SODA = "SODA";
    private static final String CARD_FRIES = "FRIES";
    private static final String CARD_BURGER = "BURGER";
    private static final String CARD_SANDWICH = "SANDWICH";
    
    
    /**
     * Creates new form ManagmentSystem
     */
public ManagmentSystem() {
    initComponents();
    setLocationRelativeTo(null);
    setResizable(false);
    
    // FORCE DIMENSIONS
    jparentMain.setPreferredSize(new java.awt.Dimension(650, 650));
    jPanel3.setPreferredSize(new java.awt.Dimension(420, 650));
    
    // Set manual layout
    getContentPane().setLayout(new java.awt.BorderLayout(5, 5));
    getContentPane().add(jPanel1, java.awt.BorderLayout.WEST);
    
    JPanel centerPanel = new JPanel(new java.awt.BorderLayout(5, 5));
    centerPanel.add(jPanel2, java.awt.BorderLayout.NORTH);
    centerPanel.add(jparentMain, java.awt.BorderLayout.CENTER);
    getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);
    
    getContentPane().add(jPanel3, java.awt.BorderLayout.EAST);
    
    // ADD KEY LISTENER
    jtextAmount.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyReleased(java.awt.event.KeyEvent evt) {
            calculateRest();
        }
    });
    
    setupCards();
    setupCategoryPanels();
    loadCategoryToPanel("Wrap", jPanelWrap);
    showCard(CARD_WRAP);
}
    


    
    
private void setupCards() {
    jparentMain.removeAll();

    jparentMain.add(jScrollPane_Wrap, CARD_WRAP);
    jparentMain.add(jScrollPane_Dessert, CARD_DESSERT);
    jparentMain.add(jScrollPane_Soda, CARD_SODA);
    jparentMain.add(jScrollPane_Fries, CARD_FRIES);
    jparentMain.add(jScrollPane_Burger, CARD_BURGER);
    jparentMain.add(jScrollPane_Sandwich, CARD_SANDWICH);

    
    jparentMain.revalidate();
    jparentMain.repaint();
    
}

private void showCard(String cardName) {
    CardLayout cl = (CardLayout) jparentMain.getLayout();
    cl.show(jparentMain, cardName);
}

private void setupCategoryPanels() {
    // Set layout
    jPanelWrap.setLayout(new java.awt.GridLayout(0, 2, 10, 10));
    jPanelBurger.setLayout(new java.awt.GridLayout(0, 2, 10, 10));
    jPanelFries.setLayout(new java.awt.GridLayout(0, 2, 10, 10));
    jPanelDessert.setLayout(new java.awt.GridLayout(0, 2, 10, 10));
    jPanelSoda.setLayout(new java.awt.GridLayout(0, 2, 10, 10));
    jPanelSandwich.setLayout(new java.awt.GridLayout(0, 2, 10, 10));
    
    // Set background
    jPanelWrap.setBackground(new java.awt.Color(241, 240, 232));
    jPanelBurger.setBackground(new java.awt.Color(241, 240, 232));
    jPanelFries.setBackground(new java.awt.Color(241, 240, 232));
    jPanelDessert.setBackground(new java.awt.Color(241, 240, 232));
    jPanelSoda.setBackground(new java.awt.Color(241, 240, 232));
    jPanelSandwich.setBackground(new java.awt.Color(241, 240, 232));
}

private void setupGrid(JPanel panel) {
    panel.setLayout(new java.awt.GridLayout(0, 2, 10, 10));
    panel.setBackground(new java.awt.Color(241, 240, 232));
}


private void loadCategoryToPanel(String category, JPanel targetPanel) {
    targetPanel.removeAll();
    
    // Reset layout
    targetPanel.setLayout(new java.awt.GridLayout(0, 2, 10, 10));
    targetPanel.setBackground(new java.awt.Color(241, 240, 232));

    java.util.List<Object[]> items = inventoryService.getItemsByCategory(category);

    if (items.isEmpty()) {
        JLabel emptyLabel = new JLabel("No products available in " + category);
        emptyLabel.setFont(new java.awt.Font("Helvetica Neue", java.awt.Font.BOLD, 16));
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        targetPanel.removeAll();
        targetPanel.setLayout(new java.awt.BorderLayout());
        targetPanel.add(emptyLabel, java.awt.BorderLayout.CENTER);
    } else {
        for (Object[] row : items) {
            
            int itemId = (int) row[0];
            String name = (String) row[1];
            float price = (float) row[2];
            int stock = (int) row[3];

            targetPanel.add(createProductCard(itemId, name, price, stock));
        }
    }

    targetPanel.revalidate();
    targetPanel.repaint();
}

private JPanel createProductCard(int itemId, String name, float price, int stock) {
    JPanel card = new JPanel();
    card.setBackground(new java.awt.Color(245, 245, 245));
    card.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)));
    card.setLayout(new javax.swing.BoxLayout(card, javax.swing.BoxLayout.Y_AXIS));

    JLabel nameLabel = new JLabel(name);
    nameLabel.setFont(new java.awt.Font("Helvetica Neue", java.awt.Font.BOLD, 13));

    JLabel priceLabel = new JLabel(String.format("$%.2f", price));
    JLabel stockLabel = new JLabel("Stock: " + stock);

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> {
        if (stock <= 0) {
            JOptionPane.showMessageDialog(this, "Out of stock!");
            return;
        }
        DefaultTableModel model = (DefaultTableModel) jTable_1.getModel();
        model.addRow(new Object[]{name, 1, price});
        updateTotal(); 
    });

    nameLabel.setAlignmentX(LEFT_ALIGNMENT);
    priceLabel.setAlignmentX(LEFT_ALIGNMENT);
    stockLabel.setAlignmentX(LEFT_ALIGNMENT);
    addButton.setAlignmentX(LEFT_ALIGNMENT);

    card.add(nameLabel);
    card.add(priceLabel);
    card.add(stockLabel);
    card.add(Box.createVerticalStrut(5));
    card.add(addButton);

    return card;
}

private void updateTotal() {
    System.out.println("=== updateTotal() called ==="); 
    
    DefaultTableModel model = (DefaultTableModel) jTable_1.getModel();
    int rowCount = model.getRowCount();
    System.out.println("Rows in table: " + rowCount); 
    
    double sum = 0;
    for (int i = 0; i < rowCount; i++) {
        Object priceObj = model.getValueAt(i, 2);
        System.out.println("Row " + i + " price: " + priceObj); 
        
        if (priceObj instanceof Double) {
            sum += (Double) priceObj;
        } else if (priceObj instanceof Float) {
            sum += (Float) priceObj;
        } else if (priceObj instanceof Number) {
            sum += ((Number) priceObj).doubleValue();
        }
    }
    
    System.out.println("Total sum: " + sum); 
    
    jtxtTotal.setText("$" + String.format("%.2f", sum));
    jtxtTotal.revalidate();
    jtxtTotal.repaint();
    
    System.out.println("jtxtTotal text set to: " + jtxtTotal.getText()); 
    
    calculateRest();
}
    
private void calculateRest() {
    try {
        String totalText = jtxtTotal.getText().replaceAll("[^\\d.]", "");
        String amountText = jtextAmount.getText().trim().replaceAll("[^\\d.]", "");
        
        
        if (totalText.isEmpty() || amountText.isEmpty()) {
            jtextRest.setText("");
            return;
        }
        
        double total = Double.parseDouble(totalText);
        double amount = Double.parseDouble(amountText);
        double rest = amount - total;
        
        //Show rest
        if (rest >= 0) {
            jtextRest.setText("$" + String.format("%.2f", rest));
        } else {
            // Dacă suma este insuficientă, nu afișa nimic
            jtextRest.setText("");
        }
    } catch (NumberFormatException e) {
        jtextRest.setText("");
    }
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        javax.swing.JButton jButton_Inventory = new javax.swing.JButton();
        javax.swing.JButton jButton_ManagmentSystem = new javax.swing.JButton();
        javax.swing.JLabel jLabel11 = new javax.swing.JLabel();
        javax.swing.JButton jButton_Exit = new javax.swing.JButton();
        jPanel29 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jtxtTotal = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jtextAmount = new javax.swing.JTextField();
        jtextRest = new javax.swing.JTextField();
        jremove_button = new javax.swing.JButton();
        jButton_pay = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton_01 = new javax.swing.JButton();
        jButton_02 = new javax.swing.JButton();
        jButton_03 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jButton_fries = new javax.swing.JButton();
        jButton_dessert = new javax.swing.JButton();
        jButton_soda = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jButton_burger = new javax.swing.JButton();
        jButton_wrap = new javax.swing.JButton();
        jButton_sandwich = new javax.swing.JButton();
        jparentMain = new javax.swing.JPanel();
        jScrollPane_Wrap = new javax.swing.JScrollPane();
        jPanelWrap = new javax.swing.JPanel();
        jScrollPane_Dessert = new javax.swing.JScrollPane();
        jPanelDessert = new javax.swing.JPanel();
        jScrollPane_Sandwich = new javax.swing.JScrollPane();
        jPanelSandwich = new javax.swing.JPanel();
        jScrollPane_Fries = new javax.swing.JScrollPane();
        jPanelFries = new javax.swing.JPanel();
        jScrollPane_Soda = new javax.swing.JScrollPane();
        jPanelSoda = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jButton_burger9 = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jButton_burger10 = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jButton_burger11 = new javax.swing.JButton();
        jScrollPane_Burger = new javax.swing.JScrollPane();
        jPanelBurger = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(82, 109, 130));

        jButton_Inventory.setBackground(new java.awt.Color(204, 204, 204));
        jButton_Inventory.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jButton_Inventory.setText("Inventory");
        jButton_Inventory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_InventoryActionPerformed(evt);
            }
        });

        jButton_ManagmentSystem.setBackground(new java.awt.Color(204, 204, 204));
        jButton_ManagmentSystem.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jButton_ManagmentSystem.setText("Home");
        jButton_ManagmentSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_ManagmentSystemActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Helvetica Neue", 1, 26)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(227, 225, 217));
        jLabel11.setText("Home");

        jButton_Exit.setBackground(new java.awt.Color(204, 204, 204));
        jButton_Exit.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jButton_Exit.setText("Exit");
        jButton_Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_ExitActionPerformed(evt);
            }
        });

        jPanel29.setBackground(new java.awt.Color(241, 240, 232));

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 122, Short.MAX_VALUE)
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 14, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton_Exit, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jButton_Inventory, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_ManagmentSystem, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(51, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addGap(62, 62, 62)
                .addComponent(jButton_ManagmentSystem, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_Inventory, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_Exit, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(82, 109, 130));

        jTable_1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product", "Quantity", "Price"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable_1);

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel2.setText("TOTAL:");

        jtxtTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtTotalActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel7.setText("AMOUNT :");

        jLabel8.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel8.setText("REST :");

        jtextAmount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtextAmountActionPerformed(evt);
            }
        });

        jtextRest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtextRestActionPerformed(evt);
            }
        });

        jremove_button.setBackground(new java.awt.Color(204, 204, 204));
        jremove_button.setText("Remove");
        jremove_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jremove_buttonActionPerformed(evt);
            }
        });

        jButton_pay.setBackground(new java.awt.Color(204, 204, 204));
        jButton_pay.setText("Pay");
        jButton_pay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_payActionPerformed(evt);
            }
        });

        jButton12.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton12.setText("5");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton14.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton14.setText("4");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton_01.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton_01.setText("1");
        jButton_01.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_01ActionPerformed(evt);
            }
        });

        jButton_02.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton_02.setText("2");
        jButton_02.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_02ActionPerformed(evt);
            }
        });

        jButton_03.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton_03.setText("3");
        jButton_03.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_03ActionPerformed(evt);
            }
        });

        jButton18.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton18.setText("6");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton19.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton19.setText("7");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton20.setText("8");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton21.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton21.setText("9");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jButton22.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton22.setText("0");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jButton23.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jButton23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/icons8-back-20.png"))); // NOI18N
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jtxtTotal))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtextRest)
                                    .addComponent(jtextAmount)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                                .addComponent(jremove_button)
                                .addGap(67, 67, 67)
                                .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jButton_01, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton_02, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton_03, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton_pay, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jtxtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtextAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtextRest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(jButton_pay, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jremove_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_03, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_02, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_01, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(89, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(241, 240, 232));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton_fries.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jButton_fries.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/icons8-fries-30.png"))); // NOI18N
        jButton_fries.setText("Fries");
        jButton_fries.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_friesActionPerformed(evt);
            }
        });

        jButton_dessert.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jButton_dessert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/icons8-dessert-30.png"))); // NOI18N
        jButton_dessert.setText("Dessert");
        jButton_dessert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_dessertActionPerformed(evt);
            }
        });

        jButton_soda.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jButton_soda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/icons8-soda-30.png"))); // NOI18N
        jButton_soda.setText("Soda");
        jButton_soda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_sodaActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(227, 225, 217));
        jLabel9.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(102, 102, 102));
        jLabel9.setText("Select what to put in basket");

        jButton_burger.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jButton_burger.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/icons8-burger-30.png"))); // NOI18N
        jButton_burger.setText("Burger");
        jButton_burger.setVerifyInputWhenFocusTarget(false);
        jButton_burger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_burgerActionPerformed(evt);
            }
        });

        jButton_wrap.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jButton_wrap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/icons8-wrap-30.png"))); // NOI18N
        jButton_wrap.setText("Wrap");
        jButton_wrap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_wrapActionPerformed(evt);
            }
        });

        jButton_sandwich.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jButton_sandwich.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/icons8-sandwich-30.png"))); // NOI18N
        jButton_sandwich.setText("Sandwich");
        jButton_sandwich.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton_sandwich.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_sandwichActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton_burger, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_wrap, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_fries, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 115, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton_sandwich, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_soda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_dessert, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(70, 70, 70))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_sandwich)
                    .addComponent(jButton_burger, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton_soda)
                    .addComponent(jButton_wrap))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_dessert)
                    .addComponent(jButton_fries, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(25, 25, 25))
        );

        jparentMain.setBackground(new java.awt.Color(204, 204, 204));
        jparentMain.setLayout(new java.awt.CardLayout());

        jPanelWrap.setBackground(new java.awt.Color(241, 240, 232));
        jPanelWrap.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanelWrapLayout = new javax.swing.GroupLayout(jPanelWrap);
        jPanelWrap.setLayout(jPanelWrapLayout);
        jPanelWrapLayout.setHorizontalGroup(
            jPanelWrapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 687, Short.MAX_VALUE)
        );
        jPanelWrapLayout.setVerticalGroup(
            jPanelWrapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 867, Short.MAX_VALUE)
        );

        jScrollPane_Wrap.setViewportView(jPanelWrap);

        jparentMain.add(jScrollPane_Wrap, "card7");

        jPanelDessert.setBackground(new java.awt.Color(241, 240, 232));
        jPanelDessert.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanelDessertLayout = new javax.swing.GroupLayout(jPanelDessert);
        jPanelDessert.setLayout(jPanelDessertLayout);
        jPanelDessertLayout.setHorizontalGroup(
            jPanelDessertLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 687, Short.MAX_VALUE)
        );
        jPanelDessertLayout.setVerticalGroup(
            jPanelDessertLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 867, Short.MAX_VALUE)
        );

        jScrollPane_Dessert.setViewportView(jPanelDessert);

        jparentMain.add(jScrollPane_Dessert, "card2");

        jPanelSandwich.setBackground(new java.awt.Color(241, 240, 232));
        jPanelSandwich.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanelSandwichLayout = new javax.swing.GroupLayout(jPanelSandwich);
        jPanelSandwich.setLayout(jPanelSandwichLayout);
        jPanelSandwichLayout.setHorizontalGroup(
            jPanelSandwichLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 687, Short.MAX_VALUE)
        );
        jPanelSandwichLayout.setVerticalGroup(
            jPanelSandwichLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 867, Short.MAX_VALUE)
        );

        jScrollPane_Sandwich.setViewportView(jPanelSandwich);

        jparentMain.add(jScrollPane_Sandwich, "card2");

        jPanelFries.setBackground(new java.awt.Color(241, 240, 232));
        jPanelFries.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanelFriesLayout = new javax.swing.GroupLayout(jPanelFries);
        jPanelFries.setLayout(jPanelFriesLayout);
        jPanelFriesLayout.setHorizontalGroup(
            jPanelFriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 687, Short.MAX_VALUE)
        );
        jPanelFriesLayout.setVerticalGroup(
            jPanelFriesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 867, Short.MAX_VALUE)
        );

        jScrollPane_Fries.setViewportView(jPanelFries);

        jparentMain.add(jScrollPane_Fries, "card2");

        jPanelSoda.setBackground(new java.awt.Color(241, 240, 232));
        jPanelSoda.setForeground(new java.awt.Color(255, 255, 255));

        jPanel15.setBackground(new java.awt.Color(241, 240, 232));
        jPanel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel37.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(51, 51, 51));
        jLabel37.setText("Orange Soda");

        jLabel38.setForeground(new java.awt.Color(102, 102, 102));
        jLabel38.setText("$1.99");

        jLabel39.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(102, 102, 102));
        jLabel39.setText("Sweet and tangy with natural extracts of orange and tangerine, finished with a hint of vanilla.");

        jButton_burger9.setBackground(new java.awt.Color(204, 204, 204));
        jButton_burger9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/icons8-juice-70-2.png"))); // NOI18N
        jButton_burger9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_burger9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel37)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel38)))
                .addGap(38, 38, 38)
                .addComponent(jButton_burger9, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                .addGap(41, 41, 41))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel39)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_burger9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel16.setBackground(new java.awt.Color(241, 240, 232));
        jPanel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel40.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(51, 51, 51));
        jLabel40.setText("Ginger Ale");

        jLabel41.setForeground(new java.awt.Color(102, 102, 102));
        jLabel41.setText("$1.99");

        jLabel42.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(102, 102, 102));
        jLabel42.setText("A sparkling beverage with fresh ginger zest, lemon, and a drop of honey.");

        jButton_burger10.setBackground(new java.awt.Color(204, 204, 204));
        jButton_burger10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/icons8-juice-70-3.png"))); // NOI18N
        jButton_burger10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_burger10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel40)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel41)))
                .addGap(38, 38, 38)
                .addComponent(jButton_burger10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(41, 41, 41))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel42)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_burger10, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel17.setBackground(new java.awt.Color(241, 240, 232));
        jPanel17.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel43.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(51, 51, 51));
        jLabel43.setText("Classic Cola");

        jLabel44.setForeground(new java.awt.Color(102, 102, 102));
        jLabel44.setText("$1.99");

        jLabel45.setFont(new java.awt.Font("Helvetica Neue", 0, 12)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(102, 102, 102));
        jLabel45.setText("The traditional fizzy drink with hints of vanilla, cinnamon, and a subtle cherry note.");

        jButton_burger11.setBackground(new java.awt.Color(204, 204, 204));
        jButton_burger11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/icons8-cola-70.png"))); // NOI18N
        jButton_burger11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_burger11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel43)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel44)))
                .addGap(38, 38, 38)
                .addComponent(jButton_burger11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(41, 41, 41))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel45)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_burger11, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelSodaLayout = new javax.swing.GroupLayout(jPanelSoda);
        jPanelSoda.setLayout(jPanelSodaLayout);
        jPanelSodaLayout.setHorizontalGroup(
            jPanelSodaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSodaLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanelSodaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanelSodaLayout.setVerticalGroup(
            jPanelSodaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSodaLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        jScrollPane_Soda.setViewportView(jPanelSoda);

        jparentMain.add(jScrollPane_Soda, "card2");

        jPanelBurger.setBackground(new java.awt.Color(241, 240, 232));
        jPanelBurger.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanelBurgerLayout = new javax.swing.GroupLayout(jPanelBurger);
        jPanelBurger.setLayout(jPanelBurgerLayout);
        jPanelBurgerLayout.setHorizontalGroup(
            jPanelBurgerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 687, Short.MAX_VALUE)
        );
        jPanelBurgerLayout.setVerticalGroup(
            jPanelBurgerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 867, Short.MAX_VALUE)
        );

        jScrollPane_Burger.setViewportView(jPanelBurger);

        jparentMain.add(jScrollPane_Burger, "card2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jparentMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jparentMain, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents




    private void jButton_ManagmentSystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_ManagmentSystemActionPerformed
    this.setVisible(false); 
    ManagmentSystem home = new ManagmentSystem();
    home.setVisible(true);  
        
    }//GEN-LAST:event_jButton_ManagmentSystemActionPerformed

    
    private void jButton_friesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_friesActionPerformed

         loadCategoryToPanel("Fries", jPanelFries);
    showCard(CARD_FRIES);
        
    }//GEN-LAST:event_jButton_friesActionPerformed

    private void jButton_dessertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_dessertActionPerformed
       
         loadCategoryToPanel("Dessert", jPanelDessert);
    showCard(CARD_DESSERT);
    }//GEN-LAST:event_jButton_dessertActionPerformed

    private void jButton_sodaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_sodaActionPerformed
        
         loadCategoryToPanel("Soda", jPanelSoda);
    showCard(CARD_SODA);
         
    }//GEN-LAST:event_jButton_sodaActionPerformed

    private void jButton_burgerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_burgerActionPerformed
 
         loadCategoryToPanel("Burger", jPanelBurger);
    showCard(CARD_BURGER);
        
    }//GEN-LAST:event_jButton_burgerActionPerformed

    private void jButton_wrapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_wrapActionPerformed
 
 loadCategoryToPanel("Wrap", jPanelWrap);
    showCard(CARD_WRAP);
        
    }//GEN-LAST:event_jButton_wrapActionPerformed

    private void jButton_sandwichActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_sandwichActionPerformed
 
         loadCategoryToPanel("Sandwich", jPanelSandwich);
    showCard(CARD_SANDWICH);
        
    }//GEN-LAST:event_jButton_sandwichActionPerformed


    private void jButton_payActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_payActionPerformed

    String totalString = jtxtTotal.getText().replaceAll("[^\\d.]", "");
    String amountString = jtextAmount.getText().replaceAll("[^\\d.]", "");

    if (totalString.isEmpty() || amountString.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please add products and enter amount!");
        return;
    }

    try {
        double amount = Double.parseDouble(amountString);
        double total = Double.parseDouble(totalString);

        if (amount < total) {
            JOptionPane.showMessageDialog(this, "Insufficient amount! Please enter more.");
            return;
        }

        double rest = amount - total;
        jtextRest.setText(String.format("$ %.2f", rest));

        DefaultTableModel model = (DefaultTableModel) jTable_1.getModel();
        
        
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/foodstem", "root", "andreiarit0n")) {
            con.setAutoCommit(false);

            for (int i = 0; i < model.getRowCount(); i++) {
                String productName = model.getValueAt(i, 0).toString();
                int quantityToDeduct = Integer.parseInt(model.getValueAt(i, 1).toString());
                
                String updateQuantitySql = "UPDATE Inventory SET Quantity = Quantity - ? WHERE Item_Name = ?";
                try (PreparedStatement pst = con.prepareStatement(updateQuantitySql)) {
                    pst.setInt(1, quantityToDeduct);
                    pst.setString(2, productName);
                    int affectedRows = pst.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Updating product quantity failed, no rows affected.");
                    }
                }
            }
            con.commit();
        }

        // Reset
        jtxtTotal.setText("");
        jtextAmount.setText("");
        jtextRest.setText("");
        currentAmount.setLength(0);
        model.setRowCount(0);

        JOptionPane.showMessageDialog(this, "Sale completed successfully! Change: $" + String.format("%.2f", rest));

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Please enter valid numbers!");
        ex.printStackTrace();
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error during sale: " + ex.getMessage());
    }


    }//GEN-LAST:event_jButton_payActionPerformed


    private void jtextRestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtextRestActionPerformed


    }//GEN-LAST:event_jtextRestActionPerformed

    private void jtextAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtextAmountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtextAmountActionPerformed

    private void jtxtTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtTotalActionPerformed
    
   updateTotal();

    
    }//GEN-LAST:event_jtxtTotalActionPerformed

    private void enterNumbers(String number) {
   //limit to 10
    if (currentAmount.length() < 10) {
        currentAmount.append(number);
        jtextAmount.setText(currentAmount.toString());
        calculateRest(); 
    }
}
    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
       enterNumbers("4");
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton_01ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_01ActionPerformed
      enterNumbers("1");
    }//GEN-LAST:event_jButton_01ActionPerformed

    private void jButton_02ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_02ActionPerformed
      enterNumbers("2");
    }//GEN-LAST:event_jButton_02ActionPerformed

    private void jButton_03ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_03ActionPerformed
    enterNumbers("3");
    }//GEN-LAST:event_jButton_03ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
     enterNumbers("5");
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
      enterNumbers("6");
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
       enterNumbers("7");
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
       enterNumbers("8");
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        enterNumbers("9");
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        enterNumbers("0");
    }//GEN-LAST:event_jButton22ActionPerformed

    
    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
     
           
    if (currentAmount.length() > 0) {
        currentAmount.deleteCharAt(currentAmount.length() - 1);
        jtextAmount.setText(currentAmount.toString());
        calculateRest();
    }
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jremove_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jremove_buttonActionPerformed

        int selectedRow = jTable_1.getSelectedRow(); // Assuming jTable_Products is your JTable's name

        if (selectedRow != -1) { // -1 means no row is selected
            // Remove the selected row from the model
            DefaultTableModel model = (DefaultTableModel) jTable_1.getModel();
            model.removeRow(selectedRow);
        }
    }//GEN-LAST:event_jremove_buttonActionPerformed

    private void jButton_InventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_InventoryActionPerformed
    this.setVisible(false);  // Close ManagementSystem
    Inventory inv = new Inventory();
    inv.setVisible(true);  
       
    }//GEN-LAST:event_jButton_InventoryActionPerformed

    private void jButton_ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_ExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton_ExitActionPerformed

    private void jButton_burger11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_burger11ActionPerformed

    }//GEN-LAST:event_jButton_burger11ActionPerformed

    private void jButton_burger10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_burger10ActionPerformed

    }//GEN-LAST:event_jButton_burger10ActionPerformed

    private void jButton_burger9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_burger9ActionPerformed

    }//GEN-LAST:event_jButton_burger9ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton_01;
    private javax.swing.JButton jButton_02;
    private javax.swing.JButton jButton_03;
    private javax.swing.JButton jButton_burger;
    public javax.swing.JButton jButton_burger10;
    public javax.swing.JButton jButton_burger11;
    public javax.swing.JButton jButton_burger9;
    private javax.swing.JButton jButton_dessert;
    private javax.swing.JButton jButton_fries;
    private javax.swing.JButton jButton_pay;
    private javax.swing.JButton jButton_sandwich;
    private javax.swing.JButton jButton_soda;
    private javax.swing.JButton jButton_wrap;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelBurger;
    private javax.swing.JPanel jPanelDessert;
    private javax.swing.JPanel jPanelFries;
    private javax.swing.JPanel jPanelSandwich;
    private javax.swing.JPanel jPanelSoda;
    private javax.swing.JPanel jPanelWrap;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane_Burger;
    private javax.swing.JScrollPane jScrollPane_Dessert;
    private javax.swing.JScrollPane jScrollPane_Fries;
    private javax.swing.JScrollPane jScrollPane_Sandwich;
    private javax.swing.JScrollPane jScrollPane_Soda;
    private javax.swing.JScrollPane jScrollPane_Wrap;
    private javax.swing.JTable jTable_1;
    private javax.swing.JPanel jparentMain;
    private javax.swing.JButton jremove_button;
    private javax.swing.JTextField jtextAmount;
    private javax.swing.JTextField jtextRest;
    private javax.swing.JTextField jtxtTotal;
    // End of variables declaration//GEN-END:variables

}