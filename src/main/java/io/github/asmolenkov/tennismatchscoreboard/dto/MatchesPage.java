package io.github.asmolenkov.tennismatchscoreboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder // Строитель для DTO избыточен — обычно такие объекты всегда создаются в одном месте и в специальном классе
@NoArgsConstructor
@AllArgsConstructor
public class MatchesPage {

    // Можно сделать record

    private List<MatchDto> matches;
    private PageInfo pageInfo;
}
