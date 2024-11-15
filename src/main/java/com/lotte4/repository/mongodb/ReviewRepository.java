package com.lotte4.repository.mongodb;

import com.lotte4.document.ReviewDocument;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<ReviewDocument, String> {
    public Optional<ReviewDocument> findByUid(String uid);
    public Page<ReviewDocument> findByProdId(int prodId, Pageable pageable);
    public Page<ReviewDocument> findByUid(String uid, Pageable pageable);
    public Optional<ReviewDocument> deleteByUid(String uid);
}
