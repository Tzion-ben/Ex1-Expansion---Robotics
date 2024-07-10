import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Method to load data from the CSV file
    public void loadDataFromFile(String inputFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(", ");
                data.add(values);
            }
            System.out.println("Data loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to analyze the data and print key metrics
    public void analyzeData() {
        // Perform your data analysis here
        // Example: Calculate average of a specific column
        double total = 0;
        int count = 0;
        for (String[] row : data) {
            if (row.length > 1) { // Adjust index based on your CSV structure
                total += Double.parseDouble(row[1]); // Assuming the second column contains numeric data
                count++;
            }
        }
        double average = total / count;
        System.out.println("Average value of column 2: " + average);
    }

    // Method to calculate and print coverage analysis
    public void coverageAnalysis() {
        Map<String, Integer> nonNullCount = new HashMap<>();
        Map<String, Integer> nonZeroCount = new HashMap<>();
        int totalRows = data.size();

        // Initialize counts
        for (String column : data.get(0)) {
            nonNullCount.put(column, 0);
            nonZeroCount.put(column, 0);
        }

        // Count non-null and non-zero values
        for (String[] row : data) {
            for (int i = 0; i < row.length; i++) {
                if (row[i] != null && !row[i].isEmpty()) {
                    nonNullCount.put(data.get(0)[i], nonNullCount.get(data.get(0)[i]) + 1);
                    if (!row[i].equals("0")) {
                        nonZeroCount.put(data.get(0)[i], nonZeroCount.get(data.get(0)[i]) + 1);
                    }
                }
            }
        }

        // Print coverage analysis
        System.out.println("Coverage Analysis:");
        for (String column : data.get(0)) {
            double nonNullCoverage = (nonNullCount.get(column) / (double) totalRows) * 100;
            double nonZeroCoverage = (nonZeroCount.get(column) / (double) totalRows) * 100;
            System.out.printf("Column: %s, Non-Null Coverage: %.2f%%, Non-Zero Coverage: %.2f%%%n",
                    column, nonNullCoverage, nonZeroCoverage);
        }
    }
}
