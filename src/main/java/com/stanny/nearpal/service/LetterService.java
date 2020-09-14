package com.stanny.nearpal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stanny.nearpal.dto.response.LetterUserResponseDto;
import com.stanny.nearpal.entity.TLetter;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Xiangb on 2019/12/17.
 * 功能：
 */
@Service
public interface LetterService extends IService<TLetter> {

    List<LetterUserResponseDto> getAcceptLettes(Integer userid);

    List<LetterUserResponseDto> getSendLettes(Integer userid);

    List<LetterUserResponseDto> getRandomLetters(Integer userid);

    List<LetterUserResponseDto> getLettersWithId(Integer userid, Integer withid);

}
