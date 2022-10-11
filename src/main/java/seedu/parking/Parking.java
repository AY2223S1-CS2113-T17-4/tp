package seedu.parking;

import java.io.IOException;

import seedu.api.Api;
import seedu.common.CommonFiles;
import seedu.data.CarparkList;
import seedu.exception.EmptyResponseException;
import seedu.exception.InvalidFindCommandException;
import seedu.exception.NoCarparkFoundException;
import seedu.exception.ParkingException;
import seedu.ui.Ui;
import seedu.parser.Parser;
import seedu.parser.Command;
import seedu.commands.Find;
import seedu.data.Carpark;

/**
 * Main class of the program.
 */
public class Parking {
    private static CarparkList carparkList;

    /**
     * Main entry-point for the java.duke.Duke application.
     */
    public static void main(String[] args) {
        Parser parser = new Parser();
        Find find = new Find();
        Ui ui = new Ui();
        ui.greetUser();

        Api api = new Api();
        api.asyncExecuteRequest(); // Send request to API and wait asynchronously

        // More code here while waiting for data to come back


        // This should be the last code block of the initialising phase
        try {
            System.out.println("Trying to fetch data"); // Debug line
            api.fetchData();
            System.out.println("Completed fetch data!"); // Debug line
        } catch (EmptyResponseException e) {
            ui.showFetchError();
        } catch (IOException e) {
            ui.showSaveError();
        } finally {
            System.out.println("Fetching and save data sequence terminated"); // Debug line
        }

        // Load file from json
        ui.showLoadingDataMessage();
        try {
            carparkList = new CarparkList(CommonFiles.LTA_FILE_PATH, CommonFiles.LTA_BACKUP_FILE_PATH);
            ui.showLoadingDataSuccess();
        } catch (ParkingException e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Load data sequence terminated"); // Debug line
        }

        // Main loop for user to use program
        boolean isExit = false;
        while (!isExit) {
            String input = ui.getCommand();
            Command command = parser.parseInputString(input);
            switch (command) {
            case INVALID:
                ui.showInvalidCommandError();
                break;
            case BYE:
                ui.showByeMessage();
                isExit = true;
                break;
            case FIND:
                try {
                    String carparkID = find.getCarparkID(input);
                    Carpark carpark = carparkList.findCarpark(carparkID);
                    ui.print(carpark.toString());
                    ui.print(String.format("Available lots: %s", carpark.getAvailableLots()));
                } catch (InvalidFindCommandException | NoCarparkFoundException exception) {
                    System.out.println(exception.getMessage());
                }
                break;
            }
        }

    }
}
