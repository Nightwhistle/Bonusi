/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smegi.bonusi.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Sergej
 */
public class Excel {
    private final String path = "D:\\Posao\\Firme\\Portal\\Evidencija Portalaca\\Bonusi\\2016.xlsx";
//    private final String path = "d:\\Temp\\2016.xlsx";
    private List<User> users = new ArrayList<>();

    public Excel(List<User> users) {
        this.users = users;
    }

    public void getExcelBonuses(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, month - 1, Calendar.DAY_OF_MONTH);
        try {
            FileInputStream inputStream = new FileInputStream(new File(path));
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(month - 1);

            for (int i = 2; i < sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                String username = "";
                int bonuses = 0;
                for (int j = 1; j < row.getLastCellNum(); j++) {
                    //getting username

                    Cell cell = row.getCell(j);
                    if (j == 1) {
                        username = cell.toString();
                        continue;
                    }

                    if (cell.toString() != "") {
                        bonuses++;
                    }
                }

                // updating user in userslist
                for (User user : users) {
                    if (user.getName().equalsIgnoreCase(username)) {
//                        System.out.println("Found username in base: " + username);
//                        System.out.println("    - adding bonuses: " + bonuses);
                        StringBuilder sb = new StringBuilder();
                        sb.append(calendar.get(Calendar.YEAR))
                                .append("-")
                                .append((calendar.get(Calendar.MONTH)));
                        user.addExcelBonusi(sb.toString(), bonuses);
                        break;
                    }
                }

            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Excel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Excel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
