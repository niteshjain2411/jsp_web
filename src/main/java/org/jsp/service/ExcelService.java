package org.jsp.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsp.model.VolunteerRegistrationData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExcelService {
    private static final String EXCEL_FILE_PATH = "registrations/JSP_volunteers_registration.xlsx";
    private static final String BACKUP_FOLDER = "registrations";

    public ExcelService() {
        try {
            Files.createDirectories(Paths.get(BACKUP_FOLDER));
        } catch (IOException e) {
            System.err.println("Error creating backup folder: " + e.getMessage());
        }
    }

    /**
     * Save registration data to Excel file
     */
    public synchronized boolean saveRegistrationToExcel(VolunteerRegistrationData registration) {
        try {
            Workbook workbook;
            Sheet sheet;
            int rowCount;

            File file = new File(EXCEL_FILE_PATH);

            // Check if file exists, if yes open it, otherwise create new
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheetAt(0);
                rowCount = sheet.getPhysicalNumberOfRows();
                fis.close();
            } else {
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Registrations");
                createHeaderRow(sheet);
                rowCount = 1;
            }

            // Add data row
            Row row = sheet.createRow(rowCount);
            fillRegistrationRow(row, registration, rowCount);

            // Auto-size columns
            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
            workbook.close();

            System.out.println("Registration saved successfully: " + registration.getFullName());
            return true;

        } catch (IOException e) {
            System.err.println("Error saving registration to Excel: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create header row for Excel sheet
     */
    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "S.No", "Full Name", "Email", "Phone", "Gender", "Age", "City", "Profession/Skills", "Selected Activities"
        };

        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * Fill registration data into Excel row
     */
    private void fillRegistrationRow(Row row, VolunteerRegistrationData registration, int rowNumber) {
        CellStyle centerStyle = row.getSheet().getWorkbook().createCellStyle();
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // S.No
        Cell cell0 = row.createCell(0);
        cell0.setCellValue(rowNumber);
        cell0.setCellStyle(centerStyle);

        Cell cell1 = row.createCell(1);
        cell1.setCellValue(registration.getFullName());
        cell1.setCellStyle(centerStyle);

        Cell cell2 = row.createCell(2);
        cell2.setCellValue(registration.getEmail());

        Cell cell3 = row.createCell(3);
        cell3.setCellValue(registration.getPhone());

        Cell cell4 = row.createCell(4);
        cell4.setCellValue(registration.getGender());

        Cell cell5 = row.createCell(5);
        cell5.setCellValue(registration.getAge());
        cell5.setCellStyle(centerStyle);

        Cell cell6 = row.createCell(6);
        cell6.setCellValue(registration.getCity());
        cell6.setCellStyle(centerStyle);

        // Professional Skills
        Cell cell7 = row.createCell(7);
        cell7.setCellValue(registration.getProfessionSkills());

        // Selected Activities
        Cell cell8 = row.createCell(8);
        cell8.setCellValue(registration.getSelectedActivities().stream().map(String::toString).reduce((a, b) -> a + ", " + b).orElse(""));
    }

    /**
     * Get all registrations from Excel
     */
    public List<VolunteerRegistrationData> getAllRegistrations() {
        List<VolunteerRegistrationData> registrations = new ArrayList<>();
        try {
            File file = new File(EXCEL_FILE_PATH);
            if (!file.exists()) {
                return registrations;
            }

            FileInputStream fis = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    VolunteerRegistrationData reg = new VolunteerRegistrationData();
                    reg.setFullName(getCellValueAsString(row.getCell(1)));
                    reg.setEmail(getCellValueAsString(row.getCell(2)));
                    reg.setPhone(getCellValueAsString(row.getCell(3)));
                    reg.setGender(getCellValueAsString(row.getCell(4)));
                    reg.setAge(getCellValueAsString(row.getCell(5)).isEmpty() ? null : Integer.parseInt(getCellValueAsString(row.getCell(5))));
                    reg.setCity(getCellValueAsString(row.getCell(6)));
                    reg.setProfessionSkills(getCellValueAsString(row.getCell(7)));
                    String activitiesStr = getCellValueAsString(row.getCell(8));
                    if (!activitiesStr.isEmpty()) {
                        reg.setSelectedActivities(reg.getSelectedActivities());
                    }
                    registrations.add(reg);
                }
            }

            fis.close();
            workbook.close();
        } catch (IOException e) {
            System.err.println("Error reading registrations from Excel: " + e.getMessage());
        }

        return registrations;
    }

    /**
     * Helper method to get cell value as string
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}

