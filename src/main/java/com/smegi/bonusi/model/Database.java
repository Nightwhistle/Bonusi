/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smegi.bonusi.model;

import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergej
 */
public class Database {

    private String path = "d:\\Temp\\Smartlaunch.mdb";
    private List<User> usersList;

    public List<User> getUsersList() {
        usersList = new ArrayList<User>();

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
