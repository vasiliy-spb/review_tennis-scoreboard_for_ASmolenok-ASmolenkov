package io.github.asmolenkov.tennismatchscoreboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchesPage {
    private List<MatchDto> matches;
    private PageInfo pageInfo;
}
