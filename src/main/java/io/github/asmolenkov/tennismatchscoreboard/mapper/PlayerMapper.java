package io.github.asmolenkov.tennismatchscoreboard.mapper;

import io.github.asmolenkov.tennismatchscoreboard.dto.PlayerDto;
import io.github.asmolenkov.tennismatchscoreboard.entity.Player;


public class PlayerMapper {

    // Класс спроектирован как утилитный, но при этом не объявлен как final и имеет публичный конструктор.
        // Можно использовать @UtilityClass из Lombok

    public static Player toEntity(PlayerDto dto){
        if(dto == null){
            return null;
        }
        return new Player(dto.id(), dto.name());
    }

    public static PlayerDto toDto(Player entity){ // Можно назвать аргумент Player player
        if(entity == null){
            return null;
        }
        return new PlayerDto(entity.getId(), entity.getName());
    }
}
