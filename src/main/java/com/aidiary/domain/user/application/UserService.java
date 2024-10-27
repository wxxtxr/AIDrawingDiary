package com.aidiary.domain.user.application;

import com.aidiary.domain.diary.domain.Diary;
import com.aidiary.domain.diary.domain.repository.DiaryRepository;
import com.aidiary.domain.user.domain.User;
import com.aidiary.domain.user.domain.repository.UserRepository;
import com.aidiary.global.payload.Message;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public Message addDiaryContent(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);

        String extractedText = parsedPdfToText(file);

        System.out.println("extractedText = " + extractedText);


        Map<LocalDate, String> diaryEntries = splitTextByDate(extractedText);

        System.out.println("diaryEntries = " + diaryEntries);

        for (Map.Entry<LocalDate, String> entry : diaryEntries.entrySet()) {
            LocalDate date = entry.getKey();
            String content = entry.getValue();

            if (diaryRepository.findByUserAndDiaryEntryDate(user, date).isPresent()) {
                System.out.println("pass");
                continue;
            }

            Diary diary = Diary.builder()
                    .user(user)
                    .content(content)
                    .diaryEntryDate(date)
                    .build();

            System.out.println("diary = " + diary);
            System.out.println("flag");

            diaryRepository.save(diary);
        }

        return Message.builder()
                .message("일기가 성공적으로 추가되었습니다.")
                .build();


    }

    private String parsedPdfToText(MultipartFile file) {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        } catch (IOException e) {
            throw new RuntimeException("PDF 파일을 처리하는 중 오류가 발생했습니다.", e);
        }
    }

    private Map<LocalDate, String> splitTextByDate(String text) {
        Map<LocalDate, String> diaryEntries = new LinkedHashMap<>();

        String datePattern = "yyyy년 M월 d일";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

        String[] lines = text.split("\n");
        LocalDate currentDate = null;
        StringBuilder currentEntry = new StringBuilder();


        for (String line : lines) {
            if (line.contains("년") && line.contains("월") && line.contains("일") && line.contains(":") && line.contains("요일")) {
                System.out.println("Found date line: " + line);
                if (currentDate != null && currentEntry.length() > 0) {
                    diaryEntries.put(currentDate, currentEntry.toString().trim());
                    currentEntry.setLength(0);
                }

                currentDate = LocalDate.parse(line.substring(0, line.indexOf('일') + 1), formatter);
            } else if (currentDate != null) {
                currentEntry.append(line).append("\n");
            }
        }

        if (currentDate != null && currentEntry.length() > 0) {
            diaryEntries.put(currentDate, currentEntry.toString().trim());
        }

        return diaryEntries;
    }
}
