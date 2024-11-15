package com.lotte4.repository.mongodb;

import com.lotte4.document.ReviewDocument;
import com.lotte4.document.UserLogDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLogRepository extends MongoRepository<UserLogDocument, String> {


}
