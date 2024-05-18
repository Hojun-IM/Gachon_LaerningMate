package com.gachon.learningmate.config;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class PageItem {

    private final int page;

    private final boolean isCurrent;

    public PageItem(int page, boolean isCurrent) {
        this.page = page;
        this.isCurrent = isCurrent;
    }

    // 표시할 페이지 번호 목록 생성 (각 객체는 페이지 번호와 현재 페이지 여부 포함)
    public static List<PageItem> createPageItems(int currentPage, int totalPages) {
        // 현재 페이지가 속한 페이지 범위
        int currentRange = (int) Math.ceil((double) currentPage / 10);
        // 현재 페이지 범위의 시작 페이지
        int startPage = (currentRange - 1) * 10 + 1;
        // 현재 페이지 범위의 끝 페이지
        int endPage = Math.min(totalPages, currentRange * 10);

        // 페이지 아이템 리스트 생성
        return IntStream.rangeClosed(startPage, endPage)
                .mapToObj(page -> new PageItem(page, page == currentPage))
                .collect(Collectors.toList());
    }

}
