package io.github.asmolenkov.tennismatchscoreboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder // Строитель для DTO избыточен — обычно такие объекты всегда создаются в одном месте и в специальном классе
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {

    // Можно сделать record

    private int currentPage;
    private int pageSize;
    private long totalItems;
    private int totalPages;


    // Можно просто hasPrevious
    public boolean isHasPrevious() {
        return currentPage > 1; }

    // Можно просто hasNext
    public boolean isHasNext() {
        return currentPage < totalPages; }

    // Нет защиты от того, что метод isHasPrevious() вернёт false, а этот метод всё равно вернёт номер страницы
    public int getPreviousPage() {
        return currentPage - 1; }

    // Нет защиты от того, что метод isHasNext() вернёт false, а этот метод всё равно вернёт номер страницы
    public int getNextPage() {
        return currentPage + 1; }
}
