package org.example.deadlinescheduler.database.controller;

import org.example.deadlinescheduler.database.dto.DeadlineDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class VoiceParserController {
    @PostMapping("/voice-parse")
    public ResponseEntity<DeadlineDTO> parseCommand(@RequestBody Map<String, String> payload) {
        String command = payload.get("command");
        System.out.println("Received voice command: " + command);

        try {
            // Custom parsing logic without relying on OpenAI
            DeadlineDTO dto = parseVoiceCommand(command);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            System.err.println("Error processing voice command: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    private DeadlineDTO parseVoiceCommand(String command) {
        // Convert to lowercase for easier parsing
        command = command.toLowerCase();

        DeadlineDTO dto = new DeadlineDTO();

        // Parse title (anything between "for" and "from")
        Pattern titlePattern = Pattern.compile("for\\s+([^\\s].+?)(?:\\s+from|\\s+to|\\s+with|$)");
        Matcher titleMatcher = titlePattern.matcher(command);
        if (titleMatcher.find()) {
            String extractedTitle = titleMatcher.group(1).trim();
            dto.setUniversityName(extractedTitle);
            System.out.println("Set universityName to: " + extractedTitle);
        }

        // Parse dates
        Pattern datePattern = Pattern.compile("(from|on)\\s+([\\w\\s]+?)\\s+(?:to|till|until)\\s+([\\w\\s]+?)(?:\\s+with|$)");
        Matcher dateMatcher = datePattern.matcher(command);

        if (dateMatcher.find()) {
            String startDateStr = dateMatcher.group(2).trim();
            String endDateStr = dateMatcher.group(3).trim();
            System.out.println("Parsed start date: " + startDateStr);
            System.out.println("Parsed end date: " + endDateStr);

            LocalDate startDate = parseDate(startDateStr);
            LocalDate endDate = parseDate(endDateStr);

            if (startDate != null) {
                dto.setStartDate(startDate);
                System.out.println("Set startDate to: " + startDate);
            }

            if (endDate != null) {
                dto.setEndDate(endDate);
                System.out.println("Set endDate to: " + endDate);
            }
        } else {
            // Try to find a single date
            Pattern singleDatePattern = Pattern.compile("(on|at)\\s+([\\w\\s]+?)(?:\\s+with|$)");
            Matcher singleDateMatcher = singleDatePattern.matcher(command);

            if (singleDateMatcher.find()) {
                String dateStr = singleDateMatcher.group(2).trim();
                LocalDate date = parseDate(dateStr);

                if (date != null) {
                    dto.setStartDate(date);
                    dto.setEndDate(date);
                }
            }
        }

        // Parse notes
        Pattern notesPattern = Pattern.compile("with\\s+(?:note|notes)\\s+(.+?)$");
        Matcher notesMatcher = notesPattern.matcher(command);

        if (notesMatcher.find()) {
            dto.setNotes(notesMatcher.group(1).trim());
        } else {
            dto.setNotes("");
        }

        // Set defaults if parsing failed
        if (dto.getUniversityName() == null) {
            dto.setUniversityName("Untitled Deadline");
        }

        if (dto.getStartDate() == null) {
            dto.setStartDate(LocalDate.now());
        }

        if (dto.getEndDate() == null) {
            dto.setEndDate(LocalDate.now().plusDays(1));
        }

        return dto;
    }

    private LocalDate parseDate(String dateStr) {
        try {
            int currentYear = LocalDate.now().getYear();

            Map<String, Integer> monthMap = Map.ofEntries(
                    Map.entry("january", 1), Map.entry("jan", 1),
                    Map.entry("february", 2), Map.entry("feb", 2),
                    Map.entry("march", 3), Map.entry("mar", 3),
                    Map.entry("april", 4), Map.entry("apr", 4),
                    Map.entry("may", 5),
                    Map.entry("june", 6), Map.entry("jun", 6),
                    Map.entry("july", 7), Map.entry("jul", 7),
                    Map.entry("august", 8), Map.entry("aug", 8),
                    Map.entry("september", 9), Map.entry("sep", 9),
                    Map.entry("october", 10), Map.entry("oct", 10),
                    Map.entry("november", 11), Map.entry("nov", 11),
                    Map.entry("december", 12), Map.entry("dec", 12)
            );

            // Handle ordinal numbers (1st, 2nd, 3rd, etc.)
            dateStr = dateStr.replaceAll("(\\d+)(?:st|nd|rd|th)", "$1");

            // Parse formats like "1st May", "May 1", "1 May"
            Pattern datePattern = Pattern.compile("(\\d+)\\s+([a-zA-Z]+)|([a-zA-Z]+)\\s+(\\d+)");
            Matcher dateMatcher = datePattern.matcher(dateStr);

            int day;
            int month;

            if (dateMatcher.find()) {
                // Format: "1 May" or "1st May"
                if (dateMatcher.group(1) != null) {
                    day = Integer.parseInt(dateMatcher.group(1));
                    String monthStr = dateMatcher.group(2).toLowerCase();
                    month = monthMap.getOrDefault(monthStr, 1); // Default to January if unknown
                }
                // Format: "May 1" or "May 1st"
                else {
                    String monthStr = dateMatcher.group(3).toLowerCase();
                    month = monthMap.getOrDefault(monthStr, 1);
                    day = Integer.parseInt(dateMatcher.group(4));
                }

                return LocalDate.of(currentYear, month, day);
            }

            return null;
        } catch (Exception e) {
            System.err.println("Error parsing date '" + dateStr + "': " + e.getMessage());
            return null;
        }
    }
}