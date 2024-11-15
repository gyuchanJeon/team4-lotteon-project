package com.lotte4.service.mongodb;

import com.lotte4.document.ReviewDocument;
import com.lotte4.document.UserLogDocument;
import com.lotte4.dto.mongodb.ReviewDTO;
import com.lotte4.dto.mongodb.UserLogDTO;
import com.lotte4.repository.mongodb.UserLogRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserLogService {

    private final UserLogRepository userLogRepository;
    private final ModelMapper modelMapper;

    public UserLogDocument insertLog(UserLogDTO userLogDTO) {
        UserLogDocument userLogDocument = modelMapper.map(userLogDTO, UserLogDocument.class);
        return userLogRepository.save(userLogDocument); // insert 여부 파악을 위해 Return 값으로 설정
    }


}
