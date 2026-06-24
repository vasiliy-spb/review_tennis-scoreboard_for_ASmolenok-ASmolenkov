package io.github.asmolenkov.tennismatchscoreboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {
    private int currentPage;
    private int pageSize;
    private long totalItems;
    private int totalPages;


    public boolean isHasPrevious() {
        return currentPage > 1; }

    public boolean isHasNext() {
        return currentPage < totalPages; }

    public int getPreviousPage() {
        return currentPage - 1; }

    public int getNextPage() {
        return currentPage + 1; }
}
