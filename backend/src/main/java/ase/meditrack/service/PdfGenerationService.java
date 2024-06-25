package ase.meditrack.service;


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
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;

@Service
@Slf4j
public class PdfGenerationService {

    private final UserService userService;
    private final MonthlyPlanService monthlyPlanService;

    public PdfGenerationService(UserService userService, MonthlyPlanService monthlyPlanService, TeamService teamService) {
        this.userService = userService;
        this.monthlyPlanService = monthlyPlanService;
    }

    public byte[] generatePdf(Principal principal, Year year, Month month) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4.rotate());
        document.setMargins(10, 8, 10, 8);

        MonthlyPlan monthlyPlan = monthlyPlanService.getMonthlyPlan(month.getValue(), year.getValue(), principal);

        // Title
        Paragraph title =
                new Paragraph("Monthly Plan" + " " +
                        monthlyPlan.getTeam().getName() + " " +
                        month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " +
                        year.toString()) //todo dynamic locale
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold();
        document.add(title);


        Calendar cal = getThisMonthCalendarInstance(year, month);

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
        table.addHeaderCell(new Cell().add(new Paragraph("Employees").setBold().setFontSize(7)));
        for (int day = 1; day <= daysInMonth; day++) {
            table.addHeaderCell(getCenteredCell(String.valueOf(day)).setBold());
        }

/*        for (String day : new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}) {

        }*/





        List<User> users = userService.findByTeam(principal);

        for (User user : users) {
            table.addCell(new Cell().add(new Paragraph(getUserName(user)).setFontSize(7)));
            //filter shifts that belong to user
            List<Shift> shifts = monthlyPlan.getShifts().stream().filter(
                    x -> x.getUsers().get(0).getId() == user.getId()).toList();

            for (int day = 1; day <= daysInMonth; day++) {
                String a = getShiftOfEmployeeAtDay(
                        LocalDate.of(year.getValue(), month.getValue(), day), shifts);
                table.addCell(getCenteredCell(a).setBold());
            }
        }

        document.add(table);
        document.close();
        return baos.toByteArray();

    }

    private String getShiftOfEmployeeAtDay(LocalDate time, List<Shift> userShifts) {
        for (Shift shift : userShifts) {
            if (shift.getDate().equals(time)) {
                return shift.getShiftType().getAbbreviation();
            }
        }
        return "";
    }

    private Calendar getThisMonthCalendarInstance(Year year, Month month) {
        Calendar cal = Calendar.getInstance();  // or pick another time zone if necessary
        cal.set(Calendar.MONTH, month.getValue());
        cal.set(Calendar.DAY_OF_MONTH, 1);      // 1st day of month
        cal.set(Calendar.YEAR, year.getValue());
        return cal;
    }

    private Cell getCenteredCell(String content) {
        return new Cell().add(new Paragraph(content).setFontSize(5).setTextAlignment(TextAlignment.CENTER));
    }

    private static List<Map<String, Object>> getShifts() {
        // Replace with actual data fetching logic
        return List.of(
                Map.of("date", "2024-06-01", "shiftType", "Type A", "users", "User 1, User 2"),
                Map.of("date", "2024-06-02", "shiftType", "Type B", "users", "User 3, User 4")
        );
    }

    private String getUserName(User user) {
        return user.getUserRepresentation().getFirstName() + " " + user.getUserRepresentation().getLastName();
    }
}
