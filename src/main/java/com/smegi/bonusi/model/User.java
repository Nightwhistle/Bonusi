/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smegi.bonusi.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sergej
 */
public class User {

    private int id;
    private String name;
    private List<Transaction> transactions = new ArrayList<>();
    private Map<String, Integer> excelBonusi = new HashMap<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Map<String, Integer> getExcelBonusi() {
        return excelBonusi;
    }

    public void setExcelBonusi(Map<String, Integer> excelBonusi) {
        this.excelBonusi = excelBonusi;
    }
    
    public void addExcelBonusi(String calendar, int bonusi) {
        excelBonusi.put(calendar, bonusi);
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public int getTotalPaymentsForMonth(int month, int year) {
        return calculatePaymentsForMonth(month, year);
    }

    public int getTotalPaymentsForCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        return calculatePaymentsForMonth(month, year);
    }
    
    public int getMissingToBonusThisMonth() {
        return 600 - (getTotalPaymentsForCurrentMonth() % 600);
    }
    
    public int getTotalPaymentsForCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        int total = 0;
        int year = calendar.get(Calendar.YEAR);
        for (int i = 0; i <= 12; i++) {
            total += calculatePaymentsForMonth(i, year);
        }
        return total;
    }
    
    public int calculateMissingBonuses(int month, int year) {
        int excelBonuses = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(year).append("-").append(month);
        String date = sb.toString();
        for (Map.Entry<String, Integer> set : excelBonusi.entrySet()) {
            if (set.getKey().equalsIgnoreCase(date)) {
                excelBonuses = set.getValue();
            }
        }
        int requiredBonuses = calculateRequiredBonuses(month, year);
        return requiredBonuses - excelBonuses;
    }

    private int calculatePaymentsForMonth(int month, int year) {
        int total = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getCalendar().get(Calendar.MONTH) == month && transaction.getCalendar().get(Calendar.YEAR) == year) {
                total += Math.round(transaction.getPayment());
            }
        }
        return total;
    }

    private int calculateRequiredBonuses(int month, int year) {
        int paid = calculatePaymentsForMonth(month, year);
        return Math.floorDiv(paid, 600);
    }
}
