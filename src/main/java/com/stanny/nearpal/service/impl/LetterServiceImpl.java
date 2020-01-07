package com.stanny.nearpal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stanny.nearpal.mapper.TLetterMapper;
import com.stanny.nearpal.dto.response.LetterUserResponseDto;
import com.stanny.nearpal.entity.TLetter;
import com.stanny.nearpal.service.LetterService;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Xiangb on 2019/12/17.
 * 功能：
 */
@Service
class LetterServiceImpl extends ServiceImpl<TLetterMapper, TLetter> implements LetterService {
    @Override
    public List<LetterUserResponseDto> getAcceptLettes(Integer userid) {
        return baseMapper.selectAcceptLetters(userid);
    }

    @Override
    public List<LetterUserResponseDto> getLettersWithId(Integer userid, Integer withid) {
        return baseMapper.selectLettersWithId(userid, withid);
    }
}
