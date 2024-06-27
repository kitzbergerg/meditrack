package ase.meditrack.service;

import ase.meditrack.exception.NotFoundException;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.User;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

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
        columnWidths[0] = 10; // employees column bigger

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

        List<User> users = new ArrayList<User>(userService.findByTeam(principal));
        Comparator<User> userComparator = Comparator.comparing(user -> user.getUserRepresentation().getLastName());
        // Sort users by their last name
        users.sort(userComparator);
        users = users.stream().filter(user -> user.getRole().getAbbreviation() != null).toList();


        for (User user : users) {
            table.addCell(new Cell().add(new Paragraph(getUserName(user)).setFontSize(7)));
            //filter shifts that belong to user
            List<Shift> shifts = monthlyPlan.getShifts().stream().filter(
                    x -> x.getUsers().get(0).getId() == user.getId()).toList();

            for (int day = 1; day <= daysInMonth; day++) {
                String[] shiftData = getShiftOfEmployeeAtDay(
                        LocalDate.of(year.getValue(), month.getValue(), day), shifts);
                table.addCell(getCenteredCell(shiftData[0], 8f)
                        .setBackgroundColor(new DeviceRgb(
                            Integer.valueOf(shiftData[1].substring(1, 3), 16),
                            Integer.valueOf(shiftData[1].substring(3, 5), 16),
                            Integer.valueOf(shiftData[1].substring(5, 7), 16)
                        ))
                        .setFontColor(new DeviceRgb(255, 255, 255))
                        .setVerticalAlignment(VerticalAlignment.MIDDLE));
            }
        }

        document.add(table);

        Set<ShiftType> shiftTypes = monthlyPlan.getShifts().stream()
                .map(Shift::getShiftType)
                .collect(Collectors.toSet());
        // Add the legend
        addLegend(document, shiftTypes);

        document.close();
        return baos.toByteArray();
    }

    /** add legend.
     * @param document document
     * @param shiftTypes shiftTypes
     */
    private void addLegend(Document document, Set<ShiftType> shiftTypes) {
        Paragraph legendTitle = new Paragraph("Shift Types Legend")
                .setFontSize(12)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);
        document.add(legendTitle);

        Table legendTable = new Table(new float[]{2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3});
        legendTable.setWidth(UnitValue.createPercentValue(100));
        legendTable.setFixedLayout();
        legendTable.setMarginTop(10);

        for (ShiftType shiftType : shiftTypes) {
            Color color = new DeviceRgb(
                    Integer.valueOf(shiftType.getColor().substring(1, 3), 16),
                    Integer.valueOf(shiftType.getColor().substring(3, 5), 16),
                    Integer.valueOf(shiftType.getColor().substring(5, 7), 16)
            );

            Cell colorCell = new Cell().add(new Paragraph(shiftType.getAbbreviation()).setFontSize(10).setBold()
                    .setTextAlignment(TextAlignment.CENTER));
            colorCell.setBackgroundColor(color);
            colorCell.setBorder(Border.NO_BORDER);
            colorCell.setFontColor(new DeviceRgb(255, 255, 255));
            colorCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
            legendTable.addCell(colorCell);

            Paragraph description = new Paragraph(
                    shiftType.getName() + "\n" + shiftType.getStartTime() + " - " + shiftType.getEndTime())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.LEFT);
            legendTable.addCell(new Cell().add(description)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
        }

        document.add(legendTable);
    }


    /**
     * Retrieves the shift abbreviation and color for a specific employee on a given day.
     * This method iterates through the list of user shifts and returns an array containing
     * the shift abbreviation and color for the specified date. If no shift is found for that date,
     * it returns an array with an empty string and default color.
     * @param time       The date for which the shift is being retrieved.
     * @param userShifts The list of shifts for the specific user.
     * @return An array containing the abbreviation of the shift type and its color for the specified date,
     * or an empty string and default color if no shift is found.
     */
    private String[] getShiftOfEmployeeAtDay(LocalDate time, List<Shift> userShifts) {
        for (Shift shift : userShifts) {
            if (shift.getDate().equals(time)) {
                return new String[]{shift.getShiftType().getAbbreviation(), shift.getShiftType().getColor()};
            }
        }
        return new String[]{"", "#FFFFFF"}; // Default color (white) for no shift
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
