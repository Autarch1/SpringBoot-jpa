package com.ypacode.springbootstudent.util;

import com.ypacode.springbootstudent.model.Student;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.util.List;

public class StudentExcelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Student> getAllStudent;

    public StudentExcelExporter(List<Student> getAllStudent) {
        this.getAllStudent = getAllStudent;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Students");

        Row row = sheet.createRow(0);
        sheet.setDefaultRowHeightInPoints(35);
        sheet.setDefaultColumnWidth(40);


        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "Student Id", style);
        createCell(row, 1, "Student Dob", style);
        createCell(row, 2, "Student Education", style);
        createCell(row, 3, "Student Gender", style);
        createCell(row, 4, "Student Name", style);
        createCell(row, 5, "Student Phone", style);
        createCell(row, 6, "Student Photo", style);


    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Student student : getAllStudent) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, student.getStudentId(), style);
            createCell(row, columnCount++, student.getStudentDob(), style);
            createCell(row, columnCount++, student.getStudentEducation(), style);
            createCell(row, columnCount++, student.getStudentGender(), style);
            createCell(row, columnCount++, student.getStudentName(), style);
            createCell(row, columnCount++, student.getStudentPhone(), style);

            // Add student photo as an image (one line)
            if (student.getStudentPhoto() != null) {
                addPhotoToCell(student.getStudentPhoto(), sheet, row, columnCount++);
            }
        }
    }

    private void addPhotoToCell(byte[] photoData, XSSFSheet sheet, Row row, int column) {
        int pictureIdx = workbook.addPicture(photoData, Workbook.PICTURE_TYPE_JPEG);
        CreationHelper helper = workbook.getCreationHelper();
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = helper.createClientAnchor();
        anchor.setCol1(column);
        anchor.setRow1(row.getRowNum());

        XSSFPicture picture = (XSSFPicture) drawing.createPicture(anchor, pictureIdx);

        // Calculate the aspect ratio
        int imageWidth = picture.getImageDimension().width;
        int imageHeight = picture.getImageDimension().height;

        double widthRatio  = (double) 1.9 / imageWidth;
        double heightRatio = (double) 1.6 / imageHeight;
        double aspectRatio = Math.min(widthRatio, heightRatio);

        // Calculate the new dimensions while preserving the aspect ratio
        int newWidth = (int) (imageWidth * aspectRatio);
        int newHeight = (int) (imageHeight * aspectRatio);

        // Set the new dimensions
        picture.resize(newWidth, newHeight);
    }


    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
}