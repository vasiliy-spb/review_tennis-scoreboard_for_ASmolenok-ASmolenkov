package io.github.asmolenkov.tennismatchscoreboard.mapper;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.entity.Player;


public class PlayerMapper {

    public static Player toEntity(PlayerDto dto){
        if(dto == null){
            return null;
        }
        return new Player(dto.id(), dto.name());
    }

    public static PlayerDto toDto(Player entity){
        if(entity == null){
            return null;
        }
        return new PlayerDto(entity.getId(), entity.getName());
    }
}
