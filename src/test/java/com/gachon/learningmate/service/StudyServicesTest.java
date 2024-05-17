package com.gachon.learningmate.service;

import com.gachon.learningmate.data.dto.StudyDto;
import com.gachon.learningmate.data.entity.StudyStatus;
import com.gachon.learningmate.data.entity.User;
import com.gachon.learningmate.data.repository.StudyRepository;
import com.gachon.learningmate.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class StudyServicesTest {

    @Autowired
    private StudyServices studyServices;

    @Autowired
    private StudyRepository studyRepository;

    private User testUser;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateRandomStudies() {

        testUser = userRepository.findByUserId("hojun8094");

        Random random = new Random();

        IntStream.range(0, 100).forEach(i -> {
            StudyDto studyDto = StudyDto.builder()
                    .creatorId(testUser)
                    .studyName("Study " + i)
                    .description("Description for study " + i)
                    .status(StudyStatus.Open)
                    .category("Category " + (i % 5))
                    .location("Location " + (i % 10))
                    .maxMember(10 + random.nextInt(10))
                    .currentMember(random.nextInt(10))
                    .photoPath("/img/default-study.jpg")
                    .build();

            studyServices.createStudy(studyDto);
        });
        // 삽입된 스터디가 100개인지 확인
        long count = studyRepository.count();
        assertThat(count).isEqualTo(204);
    }
}
