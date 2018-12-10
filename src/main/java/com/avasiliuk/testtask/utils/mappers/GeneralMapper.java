package com.avasiliuk.testtask.utils.mappers;

import org.mapstruct.Mapper;

import java.sql.Timestamp;
import java.time.Instant;

@Mapper(componentModel = "spring")
public class GeneralMapper {
    Instant toInstant(Timestamp timestamp) {
        return timestamp.toInstant();
    }
}
