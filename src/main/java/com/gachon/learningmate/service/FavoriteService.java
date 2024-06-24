package com.gachon.learningmate.service;

import com.gachon.learningmate.data.entity.Favorite;
import com.gachon.learningmate.data.entity.Study;
import com.gachon.learningmate.data.entity.User;
import com.gachon.learningmate.data.repository.FavoriteRepository;
import com.gachon.learningmate.data.repository.StudyRepository;
import com.gachon.learningmate.data.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final StudyRepository studyRepository;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository, StudyRepository studyRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.studyRepository = studyRepository;
    }

    // 즐겨찾기 추가
    @Transactional
    public boolean addFavorite(String userId, int studyId) {
        User user = userRepository.findByUserId(userId);
        Study study = studyRepository.findByStudyId(studyId);

        if (user == null)
            throw new IllegalArgumentException("잘못된 사용자 정보입니다.");
        if (study == null)
            throw new IllegalArgumentException("잘못된 스터디 정보입니다.");

        if (favoriteRepository.existsByUserAndStudy(user, study))
            return false;

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setStudy(study);
        favoriteRepository.save(favorite);
        return true;
    }

    // 즐겨찾기 삭제
    @Transactional
    public boolean removeFavorite(String userId, int studyId) {
        User user = userRepository.findByUserId(userId);
        Study study = studyRepository.findByStudyId(studyId);

        if (user == null)
            throw new IllegalArgumentException("잘못된 사용자 정보입니다.");
        if (study == null)
            throw new IllegalArgumentException("잘못된 스터디 정보입니다.");

        Favorite favorite = favoriteRepository.findByUserAndStudy(user, study);
        if (favorite == null) {
            throw new IllegalArgumentException("즐겨찾기에 존재하지 않습니다.");
        }

        favoriteRepository.delete(favorite);
        return true;
    }

    // 즐겨찾기 등록 상태 확인
    public boolean isFavorite(String userId, int studyId) {
        User user = userRepository.findByUserId(userId);
        Study study = studyRepository.findByStudyId(studyId);

        if (user == null || study == null) {
            return false;
        }

        return favoriteRepository.existsByUserAndStudy(user, study);
    }

}
