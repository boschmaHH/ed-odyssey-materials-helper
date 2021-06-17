package nl.jixxed.eliteodysseymaterials.watchdog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.jixxed.eliteodysseymaterials.AppConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Consumer;

public class JournalWatcher {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Optional<File> watchedFile = Optional.empty();

    public void watch(final File folder, final Consumer<File> fileModifiedProcessor, final Consumer<File> fileCreatedProcessor) {
        findLatestFile(folder);
        this.watchedFile.ifPresent(fileCreatedProcessor);
        new FileWatcher(folder).addListener(new FileAdapter() {
            @Override
            public void onCreated(final FileEvent event) {
                final File file = event.getFile();
                if (file.isFile()) {
                    if (file.getName().startsWith(AppConstants.JOURNAL_FILE_PREFIX)) {
                        JournalWatcher.this.watchedFile = Optional.of(file);
                        fileCreatedProcessor.accept(file);
                    }
                }
            }

            @Override
            public void onModified(final FileEvent event) {
                final File file = event.getFile();
                if (file.isFile()) {
                    if (file.getName().startsWith(AppConstants.JOURNAL_FILE_PREFIX) && hasFileHeader(file)) {
                        JournalWatcher.this.watchedFile = Optional.of(file);
                        fileModifiedProcessor.accept(file);
                    }
                }
            }
        }).watch();
    }

    private void findLatestFile(final File folder) {
        try {
            this.watchedFile = Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                    .filter(file -> file.getName().startsWith(AppConstants.JOURNAL_FILE_PREFIX))
                    .filter(this::hasFileHeader)
                    .filter(this::isOdysseyJournal)
                    .max(Comparator.comparingLong(file -> Long.parseLong(file.getName().substring(8, 20) + file.getName().substring(21, 23))));
            System.out.println("Registered watched file: " + this.watchedFile.map(File::getName).orElse("No file"));
        } catch (final NullPointerException ex) {
            System.out.println("Failed to Registered watched file at " + folder.getAbsolutePath());
        }
    }

    private boolean isOdysseyJournal(final File file) {
        try (final Scanner scanner = new Scanner(file)) {
            final String firstLine = scanner.nextLine();

            final JsonNode journalMessage = this.objectMapper.readTree(firstLine);
            final JsonNode isOdysseyNode = journalMessage.get("Odyssey");
            return isOdysseyNode != null && isOdysseyNode.asBoolean(false);

        } catch (final FileNotFoundException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean hasFileHeader(final File file) {
        try (final Scanner scanner = new Scanner(file)) {
            scanner.nextLine();
            return true;
        } catch (final NoSuchElementException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}