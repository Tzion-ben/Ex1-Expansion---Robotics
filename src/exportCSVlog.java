import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class exportCSVlog {

    private String fileName;
    private List<String[]> data;
    private boolean isHeaderWritten;

    public exportCSVlog(String fileName) {
        this.fileName = fileName;
        this.data = new ArrayList<>();
        this.isHeaderWritten = false;
    }

    // Method to add a row to the CSV
    public void addRow(String[] row) {
        data.add(row);
    }

    // Method to save data to the CSV file
    public void saveToFile() {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Iterate over each row and write to the file
            for (String[] row : data) {
                writer.append(String.join(", ", row));
                writer.append("\n");
            }
            // Flush and close the writer
            writer.flush();
            System.out.println("CSV file saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHeaderAndSave(String[] header) {
        if (!isHeaderWritten) {
            try (FileWriter writer = new FileWriter(fileName, true)) {
                writer.append(String.join(", ", header));
                writer.append("\n");
                writer.flush();
                isHeaderWritten = true;
                System.out.println("Header added successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to add a row and save it immediately to the CSV file
    public void addRowAndSave(String[] row) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.append(String.join(", ", row));
            writer.append("\n");
            writer.flush();
            System.out.println("Row added and saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isHeaderWritten() {
        return isHeaderWritten;
    }

    public void setHeaderWritten(boolean headerWritten) {
        isHeaderWritten = headerWritten;
    }
}
