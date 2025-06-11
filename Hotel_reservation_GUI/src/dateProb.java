import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class dateProb {
    public static void main(String[] args) {
        String date1 = "12APR2002";
        String date2 = "22AUG2024";

        // Define the date format to match "ddMMMyyyy"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMMyyyy", Locale.ENGLISH);

        try {
            // Parse the dates
            LocalDate startDate = LocalDate.parse(date1, formatter);
            LocalDate endDate = LocalDate.parse(date2, formatter);

            // Calculate the number of days between the two dates
            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

            // Output the result
            System.out.println("Number of days between " + date1 + " and " + date2 + " is: " + daysBetween);
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing dates: " + e.getMessage());
        }
    }
}
