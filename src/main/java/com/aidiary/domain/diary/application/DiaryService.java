package com.aidiary.domain.diary.application;

import com.aidiary.domain.bookmark.domain.repository.BookmarkRepository;
import com.aidiary.domain.diary.domain.Diary;
import com.aidiary.domain.diary.domain.repository.DiaryRepository;
import com.aidiary.domain.diary.dto.*;
import com.aidiary.domain.diary.dto.condition.DiariesSearchCondition;
import com.aidiary.domain.s3.service.S3Service;
import com.aidiary.domain.user.domain.User;
import com.aidiary.domain.user.domain.repository.UserRepository;
import com.aidiary.global.config.security.token.UserPrincipal;
import com.aidiary.global.payload.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final BookmarkRepository bookmarkRepository;
    private final S3Service s3Service;

    @Value("${openai.api.url}")
    private String apiURL;

    @Transactional
    public CreateDiaryRes writeDiary(UserPrincipal userPrincipal, CreateDiaryReq createDiaryReq) {

        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(EntityNotFoundException::new);

        Diary diary = Diary.builder()
                .user(user)
                .content(createDiaryReq.content())
                .diaryEntryDate(createDiaryReq.diaryEntryDate())
                .url(createImage(createDiaryReq.content()))
                .build();

        diaryRepository.save(diary);

        CreateDiaryRes createDiaryRes = CreateDiaryRes.builder()
                .id(diary.getId())
                .content(diary.getContent())
                .diaryEntryDate(diary.getDiaryEntryDate())
                .url(diary.getUrl())
                .build();


        return createDiaryRes;
    }

    @Transactional
    public EditDiaryRes editDiary(UserPrincipal userPrincipal, EditDiaryReq editDiaryReq, Long id) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(EntityNotFoundException::new);

        Diary diary = diaryRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (user.getId().equals(diary.getUser().getId())) {
            diary.updateContent(editDiaryReq.content());

            EditDiaryRes editDiaryRes = EditDiaryRes.builder()
                    .userId(user.getId())
                    .DiaryId(diary.getId())
                    .content(editDiaryReq.content())
                    .diaryEntryDate(diary.getDiaryEntryDate())
                    .build();

            return editDiaryRes;
        }

        return null;
    }

    @Transactional
    public Message removeDiary(UserPrincipal userPrincipal, Long id) {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(EntityNotFoundException::new);

        if (!user.equals(diary.getUser())) {

            return Message.builder()
                    .message("일기 삭제에 실패했습니다.")
                    .build();

        }

            bookmarkRepository.deleteAllByDiary(diary);
            diaryRepository.delete(diary);

            return Message.builder()
                    .message("일기를 삭제하였습니다.")
                    .build();
    }

    @Transactional
    public DiaryDetailsRes viewDiary(UserPrincipal userPrincipal, Long diaryId) throws AccessDeniedException {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(EntityNotFoundException::new);

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(EntityNotFoundException::new);

        if (!diary.getUser().equals(user)) {
            throw new AccessDeniedException("해당 일기에 접근 권한이 없습니다.");
        }


        DiaryDetailsRes oneByUserIdAndDiaryId = diaryRepository.findOneByUserIdAndDiaryId(userPrincipal.getId(), diaryId);

        return oneByUserIdAndDiaryId;

    }

    @Transactional
    public Page<SearchDiariesRes> findDiariesByContent(UserPrincipal userPrincipal, DiariesSearchCondition diariesSearchCondition, Pageable pageable) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(EntityNotFoundException::new);

        return diaryRepository.findDiaries(user, diariesSearchCondition, pageable);
    }

    @Transactional
    public List<DiaryDetailsRes> findMonthlyDiaries(UserPrincipal userPrincipal, int year, int month) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(EntityNotFoundException::new);
        return diaryRepository.findByUserIdWithYearAndMonth(user.getId(), year, month);
    }

    public String createImage(String content) {
        try {
            String imageUrl = generateImageFromText(content);
            if (imageUrl != null) {
//                saveImage(imageUrl, "diary_image.png");
                s3Service.upload(imageUrl);
                System.out.println("'diary_image.png'. 이름으로 이미지가 생성되었습니다.");
                return imageUrl;
            } else {
                System.out.println("이미지 생성에 실패하였습니다.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


//    private void saveImage(String imageUrl, String filePath) throws Exception {
//        try (InputStream in = new URL(imageUrl).openStream()) {
//            Files.copy(in, Paths.get(filePath));
//        }
//    }


    private  String generateImageFromText(String content) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String requestBody = "{ \"model\": \"dall-e-3\", \"prompt\": \"" + content + "\", \"n\": 1, \"size\": \"1024x1024\" }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiURL))
                .header("Authorization", "Bearer " + "OPENAI_API_KEY")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        System.out.println("HTTP Status Code: " + statusCode);
        String responseBody = response.body();
        System.out.println("Response Body: " + responseBody);

        if (statusCode == 200) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseBody);

            if (rootNode.has("data") && rootNode.get("data").isArray()) {
                return rootNode.get("data").get(0).get("url").asText();
            } else {
                System.out.println("No data field found in the response.");
                return null;
            }
        } else {
            System.out.println("Request failed with status code: " + statusCode);
            return null;
        }
    }
}
