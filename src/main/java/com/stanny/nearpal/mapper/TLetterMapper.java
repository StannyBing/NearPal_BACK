package com.stanny.nearpal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stanny.nearpal.dto.response.LetterUserResponseDto;
import com.stanny.nearpal.entity.TLetter;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TLetterMapper extends BaseMapper<TLetter> {

    List<LetterUserResponseDto> selectAcceptLetters(Integer userid);

    List<LetterUserResponseDto> selectSendLetters(Integer userid);

    List<LetterUserResponseDto> selectMyRandomLetter(Integer userid);

    List<LetterUserResponseDto> selectLettersWithId(Integer userid, Integer withid);

}