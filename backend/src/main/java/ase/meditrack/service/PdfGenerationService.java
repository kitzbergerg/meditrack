package ase.meditrack.service;


import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.User;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class PdfGenerationService {

    private final UserService userService;
    private final MonthlyPlanService monthlyPlanService;

    /** is the constructor of the class, as expected.
     * @param userService service to load users
     * @param monthlyPlanService service to load monthly plan
     */
    public PdfGenerationService(UserService userService, MonthlyPlanService monthlyPlanService) {
        this.userService = userService;
        this.monthlyPlanService = monthlyPlanService;
    }

    /**
     * Generates a PDF document for the monthly plan of the given team.
     * This method compiles the monthly plan for a team, retrieves relevant user shifts,
     * constructs a table for the entire month, and then outputs the data into a PDF.
     * @param principal The principal representing the user making the request.
     * @param year The year for which the monthly plan is generated.
     * @param month The month for which the monthly plan is generated.
     * @return A byte array containing the generated PDF document.
     * @throws NotFoundException If the monthly plan for the specified month and year is not found.
     */
    public byte[] generatePdf(Principal principal, Year year, Month month) throws NotFoundException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4.rotate());
        document.setMargins(10, 8, 10, 8);

        MonthlyPlan monthlyPlan = monthlyPlanService.getMonthlyPlan(month.getValue(), year.getValue(), principal);

        // Title
        Paragraph title =
                new Paragraph("Monthly Plan" + " "
                        + monthlyPlan.getTeam().getName() + " "
                        + month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " "
                        + year.toString())
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold();
        document.add(title);

        YearMonth yearMonthObject = YearMonth.of(year.getValue(), month.getValue());
        int daysInMonth = yearMonthObject.lengthOfMonth();
        float[] columnWidths = new float[daysInMonth + 1];
        Arrays.fill(columnWidths, 4f);
        columnWidths[0] = 10; //employees column bigger

        // Create table with days +1 columns (1 for employees) +1
        Table table = new Table(columnWidths);
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMaxWidth(UnitValue.createPercentValue(100));
        table.setFixedLayout();

        // Table Header
        table.addHeaderCell(new Cell().add(new Paragraph("Employees").setBold().setFontSize(9f)));
        for (int day = 1; day <= daysInMonth; day++) {
            table.addHeaderCell(getCenteredCell(String.valueOf(day), 9f).setBold());
        }

        List<User> users = userService.findByTeam(principal);

        for (User user : users) {
            table.addCell(new Cell().add(new Paragraph(getUserName(user)).setFontSize(7)));
            //filter shifts that belong to user
            List<Shift> shifts = monthlyPlan.getShifts().stream().filter(
                    x -> x.getUsers().get(0).getId() == user.getId()).toList();

            for (int day = 1; day <= daysInMonth; day++) {
                String a = getShiftOfEmployeeAtDay(
                        LocalDate.of(year.getValue(), month.getValue(), day), shifts);
                table.addCell(getCenteredCell(a, 8f));
            }
        }

        document.add(table);
        document.close();
        return baos.toByteArray();

    }

    /**
     * Retrieves the shift abbreviation for a specific employee on a given day.
     * This method iterates through the list of user shifts and returns the shift abbreviation
     * for the specified date. If no shift is found for that date, it returns an empty string.
     * @param time The date for which the shift is being retrieved.
     * @param userShifts The list of shifts for the specific user.
     * @return The abbreviation of the shift type for the specified date, or an empty string if no shift is found.
     */
    private String getShiftOfEmployeeAtDay(LocalDate time, List<Shift> userShifts) {
        for (Shift shift : userShifts) {
            if (shift.getDate().equals(time)) {
                return shift.getShiftType().getAbbreviation();
            }
        }
        return "";
    }

    /**
     * Creates a new table cell with centered text.
     * @param content The content to be placed in the cell.
     * @param fontSize the size of the font for that cell.
     * @return A cell with centered text.
     */
    private Cell getCenteredCell(String content, Float fontSize) {
        return new Cell().add(new Paragraph(content).setFontSize(fontSize).setTextAlignment(TextAlignment.CENTER));
    }

    /**
     * gets username as text.
     * @param user user to get name from.
     * @return username.
     */
    private String getUserName(User user) {
        return user.getUserRepresentation().getFirstName() + " " + user.getUserRepresentation().getLastName();
    }
}
