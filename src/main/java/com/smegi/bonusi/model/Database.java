/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smegi.bonusi.model;

import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergej
 */
public class Database {
    private final String path = "C:\\Program Files\\Smartlaunch\\Server\\Data\\DB\\Smartlaunch.mdb";
//    private final String path = "d:\\Temp\\Smartlaunch.mdb";
    private List<User> usersList;

    public List<User> getUsersList() {
        usersList = new ArrayList<>();

        try {
            Table table = DatabaseBuilder.open(new File(path)).getTable("Users");
            for (Row row : table) {
                User user = new User();
                String username = row.get("Username").toString();
                int id = (int) row.get("ID");
                user.setName(username);
                user.setId(id);
                usersList.add(user);
            }

        } catch (IOException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        getTransactions(usersList);
        return usersList;

    }

    public void setNews() {
        Calendar dateTime = Calendar.getInstance();
        Calendar dateTimeExpire = Calendar.getInstance();
        dateTimeExpire.add(Calendar.DATE, 7);
        
        StringBuilder top = new StringBuilder("\"Top uplate: \" ");
        StringBuilder nedostaje = new StringBuilder("\"Nedostaje manje od 300din: \" ");

        // Top payers
        Collections.sort(usersList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return Integer.compare(o2.getTotalPaymentsForCurrentMonth(), o1.getTotalPaymentsForCurrentMonth());
            }
        });
        int topUsers = 5; // how much top users to display
        List<User> topPayingUsers = usersList.subList(0, topUsers);
        for (User user : topPayingUsers) {
            top.append("& vbnewline & \"" + user.getName() + "\" & vbTab & \"" + user.getTotalPaymentsForCurrentMonth() + "\" ");
        }

        // Missing to bonus
        int leastMissingTreshold = 300;
        List<User> leastMissing = new ArrayList<>();
        for (User user : usersList) {
            if (user.getMissingToBonusThisMonth() <= leastMissingTreshold) {
                leastMissing.add(user);
            }
        }
        Collections.sort(leastMissing, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return Integer.compare(o1.getMissingToBonusThisMonth(), o2.getMissingToBonusThisMonth());
            }
        });
        for (User user : leastMissing) {
            nedostaje.append("& vbnewline & \""+ user.getName() + "\" & vbTab & \"" + user.getMissingToBonusThisMonth() + "\"");
        }
        
        System.out.println(top.toString() + nedostaje.toString());
        
        File file = new File("E:\\Games\\bonusi.vbs");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write("msgbox " + top.toString() + " & vbNewLine & vbNewLine & " + nedostaje.toString());
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    private void getTransactions(List<User> usersList) {
        try {
            Table table = DatabaseBuilder.open(new File(path)).getTable("FinancialTransactions");
            for (Row row : table) {
                Transaction transaction = new Transaction();

                int transactionUserId = (int) row.get("UserID");
                double payment = (double) row.get("TotalAmount");

                // Date convert from DB to Calendar
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                String transactionDateString = row.get("Date").toString();
                try {
                    cal.setTime(sdf.parse(transactionDateString));
                } catch (ParseException ex) {
                    Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                }

                // Add to existing users
                for (User user : usersList) {
                    if (user.getId() != transactionUserId) {
                        continue; // Continue if not user
                    }
                    transaction.setCalendar(cal);
                    transaction.setPayment(payment);
                    user.addTransaction(transaction);
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
