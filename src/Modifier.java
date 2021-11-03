import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple program that allows the easy modification of file creation and modification dates.
 */
public class Modifier {

    private File currentFile;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

    public Modifier(){
        handleGUI();
    }

    /**
     * Get the current file's creation date.
     * @return creation date of current file in String format for display.
     */
    private String getCreatedDate(){
        Path path = Paths.get(currentFile.getAbsolutePath());
        try {
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            FileTime dateTime = attr.creationTime();
            return dateFormat.format(dateTime.toMillis());

        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Get the current file's last modified date.
     * @return the modification date of current file in String format for display.
     */
    private String getModifiedDate(){
        Path path = Paths.get(currentFile.getAbsolutePath());
        try {
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            FileTime dateTime = attr.lastModifiedTime();
            return dateFormat.format(dateTime.toMillis());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Update the current file with the new dates entered by the user.
     * @param created - The new creation date in String form.
     * @param modified - The new modification date in String form.
     * @return true if the file update was successful, false if not, null if no file.
     */
    private Boolean updateDates(String created, String modified){
        if (currentFile == null){
            return null;
        }
        Path path = Paths.get(currentFile.getAbsolutePath());

        try {
            // Date created
            Calendar createdCal = Calendar.getInstance();
            createdCal.setTime(dateFormat.parse(created));

            // Update the creation time
            Files.setAttribute(path, "creationTime", FileTime.fromMillis(createdCal.getTimeInMillis()));

            // Date modified
            Calendar modifiedCal = Calendar.getInstance();
            modifiedCal.setTime(dateFormat.parse(modified));

            // Update the time last modified
            Files.setAttribute(path, "lastModifiedTime", FileTime.fromMillis(modifiedCal.getTimeInMillis()));

            return getCreatedDate().equals(created) && getModifiedDate().equals(modified);
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Create and Display the GUI.
     * Also handles event-driven activity.
     */
    private void handleGUI(){
        JFrame frame = new JFrame("Modifier");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel filepathLabel = new JLabel("No File Selected");

        // Create panel that displays/edits creation date.
        JPanel createdPanel = new JPanel();
        JLabel createdLabel = new JLabel("File First Created");
        JTextArea createdDate = new JTextArea("No File Selected");
        createdDate.setPreferredSize(new Dimension(150, 25));
        createdDate.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        createdPanel.add(createdLabel);
        createdPanel.add(createdDate);

        // Create panel that displays/edits last modification date.
        JPanel modifiedPanel = new JPanel();
        JLabel modifiedLabel = new JLabel("File Last Modified");
        JTextArea modifiedDate = new JTextArea("No File Selected");
        modifiedDate.setPreferredSize(new Dimension(150, 25));
        modifiedDate.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        modifiedPanel.add(modifiedLabel);
        modifiedPanel.add(modifiedDate);

        // Create file selection panel, and update the GUI with the new information once file selected.
        JButton chooseButton = new JButton("Select File");
        chooseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setMultiSelectionEnabled(false);
            int result = chooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION){
                File selectedFile = chooser.getSelectedFile();
                currentFile = selectedFile;

                if (selectedFile != null){
                    filepathLabel.setText(selectedFile.getName());
                }
                else {
                    filepathLabel.setText("No File Selected");
                    createdDate.setEditable(false);
                    modifiedDate.setEditable(false);
                    modifiedDate.setText("No File Selected");
                    createdDate.setText("No File Selected");
                }

                createdDate.setEditable(true);
                modifiedDate.setEditable(true);
                modifiedDate.setText(getModifiedDate());
                createdDate.setText(getCreatedDate());

            }
        });

        // Label to display date format.
        JPanel labelPanel = new JPanel();
        JLabel dateFormatMessage = new JLabel("Date Format: dd/mm/yyyy hh:mm:ss");
        labelPanel.add(dateFormatMessage);

        // Apply button panel and logic
        JPanel bottomPanel = new JPanel();
        JButton applyButton = new JButton("Apply");
        bottomPanel.add(applyButton);
        applyButton.addActionListener(e -> {
            Boolean success = updateDates(createdDate.getText(), modifiedDate.getText());
            if (success == null){
                JOptionPane.showMessageDialog(frame, "No file selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (success){
                JOptionPane.showMessageDialog(frame, "File was updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                JOptionPane.showMessageDialog(frame, "An Error Occurred. Check Date Format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add styling to filepath label.
        filepathLabel.setPreferredSize(new Dimension(300, 25));
        filepathLabel.setOpaque(true);
        filepathLabel.setHorizontalAlignment(SwingConstants.LEFT);
        filepathLabel.setBackground(Color.WHITE);
        filepathLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Create the file panel.
        JPanel filePanel = new JPanel();
        filePanel.add(chooseButton);
        filePanel.add(filepathLabel);

        // Add components to frame.
        frame.add(filePanel);
        frame.add(labelPanel);
        frame.add(createdPanel);
        frame.add(modifiedPanel);
        frame.add(bottomPanel);

        // Set the look and feel to match system if possible.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ignored) {}

        // Set final layout information and show GUI
        GridLayout gridLayout = new GridLayout(5, 1);
        frame.setLayout(gridLayout);
        frame.setSize(600, 200);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Modifier();
    }
}

