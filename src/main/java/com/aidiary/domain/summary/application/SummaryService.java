package com.aidiary.domain.summary.application;

import com.aidiary.domain.diary.domain.Diary;
import com.aidiary.domain.diary.domain.repository.DiaryRepository;
import com.aidiary.domain.emotion.dto.ChatGPTReq2;
import com.aidiary.domain.emotion.dto.ChatGPTRes;
import com.aidiary.domain.summary.domain.DiarySummary;
import com.aidiary.domain.summary.domain.repository.DiarySummaryRepository;
import com.aidiary.domain.s3.service.S3Service;
import com.aidiary.domain.user.domain.User;
import com.aidiary.domain.user.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;



@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SummaryService {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    @Value("${openai.api.image}")
    private String imageApiUrl;

    private final RestTemplate restTemplate;

    private final S3Service s3Service;


    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final DiarySummaryRepository diarySummaryRepository;

    private static final int BATCH_SIZE = 10;

    @Transactional
    public void summarize() {
        List<Long> userIdList = userRepository.findAllUserId();

        for (Long userId : userIdList) {
            User user = userRepository.findById(userId)
                    .orElseThrow(EntityNotFoundException::new);
            List<Diary> diaryList = diaryRepository.findAllByUserId(userId);
            int totalDiaries = diaryList.size();

            String summary = summarizeDiaries(diaryList, totalDiaries);

            DiarySummary diarySummary = user.getDiarySummary();
            if (diarySummary == null) {
                diarySummary = DiarySummary.builder()
                        .summarizedDiary(summary)
                        .user(user)
                        .build();
                diarySummaryRepository.save(diarySummary);
                user.updateDiarySummary(diarySummary);
            } else {
                diarySummary.updateSummarizedDiary(summary);
            }
        }
    }

    private String summarizeDiaries(List<Diary> diaryList, int totalDiaries) {
        if (totalDiaries < BATCH_SIZE) {
            return summarizeDiaries(diaryList);
        } else {
            StringBuilder finalSummaryBuilder = new StringBuilder();

            for (int i = 0; i < totalDiaries; i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, totalDiaries);
                List<Diary> batch = diaryList.subList(i, end);

                // Summarize the batch
                String summary = summarizeDiaries(batch);
                finalSummaryBuilder.append(summary).append(" ");
            }

            return finalSummaryBuilder.toString().trim();
        }
    }

    private String summarizeDiaries(List<Diary> diaries) {
        String contentToSummarize = diaries.stream()
                .map(diary -> diary.getDiaryEntryDate().toString() + ": " + diary.getContent())
                .collect(Collectors.joining("\n\n"));

        String systemMessage = "일기가 아닌 이상한 내용이라고 판단되면 날짜와 '평범한 날이었다.'만 리턴하고 하고 일기라고 판단되면 중요한 사건을 위주로 있는 사실로 요약해줘. 응답 예시는 다음과 같아 " +
                "2024-07-26: 기운이 없고 우울한 하루를 보냈다. 수업, 과제, 그룹 프로젝트, 친구들과의 저녁 식사 모두 집중하기 어려웠다.\n" +
                "\n" +
                "2024-07-24: 친구들과 공원에서 피크닉을 즐기고 자전거를 타며 행복한 시간을 보냈다.\n" +
                "\n" +
                "2024-07-23: 가족들과 놀이공원에서 즐거운 하루를 보냈다. ";

        ChatGPTReq2 request = new ChatGPTReq2(model, systemMessage, contentToSummarize);
        ChatGPTRes chatGPTRes = restTemplate.postForObject(apiURL, request, ChatGPTRes.class);

        if (chatGPTRes != null && chatGPTRes.getChoices() != null && !chatGPTRes.getChoices().isEmpty()) {
            return chatGPTRes.getChoices().get(0).getMessage().getContent().trim();
        }

        return contentToSummarize; // In case of failure, return the original content
    }


     private void createImage(Diary diary) {
        try {
            String imageUrl = generateImageFromText(diary);
            if (imageUrl != null) {
                s3Service.upload(imageUrl);
                System.out.println("'diary_image.png'. 이름으로 이미지가 생성되었습니다.");
            } else {
                System.out.println("이미지 생성에 실패하였습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createImage(String content) {
        try {
            String imageUrl = generateImageFromText(content);
            if (imageUrl != null) {
                s3Service.upload(imageUrl);
                System.out.println("'diary_image.png'. 이름으로 이미지가 생성되었습니다.");
            } else {
                System.out.println("이미지 생성에 실패하였습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  String generateImageFromText(String content) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String requestBody = "{ \"model\": \"dall-e-3\", \"prompt\": \"" + content + "\", \"n\": 1, \"size\": \"1024x1024\" }";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(imageApiUrl))
            .header("Authorization", "Bearer " + OPENAI_API_KEY)
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


    private  String generateImageFromText(Diary diary) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String requestBody = "{ \"model\": \"dall-e-3\", \"prompt\": \"" + diary.getContent() + "\", \"n\": 1, \"size\": \"1024x1024\" }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(imageApiUrl))
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
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
