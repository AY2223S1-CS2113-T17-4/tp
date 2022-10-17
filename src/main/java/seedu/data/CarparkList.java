package seedu.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import seedu.exception.NoCarparkFoundException;
import seedu.exception.NoFileFoundException;
import seedu.files.FileReader;
import seedu.parser.search.Sentence;
import seedu.parser.search.Word;

/**
 * Container for all the {@link Carpark} classes. Contains method for finding the carpark.
 */
public class CarparkList {
    private final HashMap<String, Carpark> carparkHashMap = new HashMap<String, Carpark>();
    private List<Carpark> carparks;

    /**
     * Constructor for the {@link CarparkList} class. Loads from a {@link Path} object
     * that points to a {@code .json} file.
     *
     * @param filepath Filepath to load from.
     * @param filepathBackup Backup filepath to load from.
     * @throws NoFileFoundException If no valid file is found in either location.
     */
    public CarparkList(Path filepath, Path filepathBackup) throws NoFileFoundException {
        carparks = FileReader.loadLtaJson(filepath, filepathBackup);
        combineByLotType();
    }

    /**
     * Constructor for the {@link CarparkList} class. Initializes an object from a given
     * list of {@link Carpark} objects.
     *
     * @param carparks {@link List} of {@link Carpark} objects.
     */
    public CarparkList(List<Carpark> carparks) {
        this.carparks = carparks;
        combineByLotType();
    }

    /**
     * Finds carpark based on an exact string (case-insensitive) for the carpark ID.
     *
     * @param searchString string that should be matched to
     * @return returns the carpark with this unique ID
     * @throws NoCarparkFoundException If no carpark was found
     */
    public Carpark findCarpark(String searchString) throws NoCarparkFoundException {
        if (carparkHashMap.get(searchString.toLowerCase()) == null) {
            throw new NoCarparkFoundException();
        } else {
            return carparkHashMap.get(searchString.toLowerCase());
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Carpark carpark : carparks) {
            result.append(carpark.getListViewString()).append("\n");
        }
        return result.toString();
    }

    /**
     * Filter {@link CarparkList#carparks} with a {@link Sentence} object, where
     * every word in the query must be present or a prefixing substring of a word
     * in the {@link Carpark} object's development string.
     *
     * @param searchQuery {@link Sentence} object to use as a search.
     * @return Filtered {@link CarparkList} object.
     */
    public CarparkList filterByAllStrings(Sentence searchQuery) {
        HashSet<Carpark> carparkListBuffer = new HashSet<>(carparks);
        for (Word word : searchQuery.getWords()) {
            carparkListBuffer = filterBySubstring(carparkListBuffer, word.toString());
        }
        return new CarparkList(new ArrayList<>(carparkListBuffer));
    }

    private HashSet<Carpark> filterBySubstring(HashSet<Carpark> carparkList, String wordString) {
        HashSet<Carpark> bufferList = new HashSet<>();
        for (Carpark carpark : carparkList) {
            for (Word word : carpark.getDevelopmentSentence().getWords()) {
                if (word.toString().toLowerCase().startsWith(wordString.toLowerCase())) {
                    word.makeBold(true);
                    bufferList.add(carpark);
                    break;
                }
            }
        }
        return bufferList;
    }

    /**
     * Resets the {@link Word#isBold} attribute in all {@link Carpark} objects.
     */
    public void resetBoldForAllCarparks() {
        for (Carpark carpark : carparks) {
            carpark.resetBold();
        }
    }

    /**
     * Gets a formatted string for use with the {@link seedu.commands.Search Search} command.
     * @return
     */
    public String getSearchListString() {
        StringBuilder bufferString = new StringBuilder();
        for (Carpark carpark : carparks) {
            bufferString.append(carpark.getListViewString()).append("\n");
        }
        return bufferString.toString();
    }

    /**
     * Combines multiple {@link Carpark} objects that have the same {@link Carpark#carparkId} value, and groups them
     * based on lot type.
     */
    public void combineByLotType() {
        for (Carpark carpark : carparks) {
            String carparkId = carpark.getCarparkId().toLowerCase();
            if (!carparkHashMap.containsKey(carparkId)) {
                carparkHashMap.put(carparkId, carpark);
            } else {
                carparkHashMap.get(carparkId).addCarparkLotType(carpark);
            }
        }
        carparks = new ArrayList<>(carparkHashMap.values());
    }
}
