package com.lotte4.repository.mongodb;

import com.lotte4.dto.mongodb.RecommendationResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class UserBehaviorRepository {

    private final MongoTemplate mongoTemplate;

    public UserBehaviorRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<RecommendationResult> findRelatedProducts(int targetProdId, String currentUserId) {
        // Step 1: Get the users who viewed the target product, excluding the current user
        MatchOperation matchUsersWhoViewedTarget = Aggregation.match(
                Criteria.where("prodId").is(targetProdId)
                        .and("uid").ne(currentUserId)
        );

        // Step 2: Lookup all products viewed by these users
        GroupOperation groupByUser = Aggregation.group("uid").first("uid").as("uid"); // Group by uid to get unique users
        LookupOperation lookupProductsViewedByUsers = Aggregation.lookup("userLogs", "uid", "uid", "userViews");

        // Step 3: Unwind the user views to treat each view as a separate document
        UnwindOperation unwindViews = Aggregation.unwind("userViews");

        // Step 4: Filter out the target product from the results
        MatchOperation matchOtherProducts = Aggregation.match(
                Criteria.where("userViews.prodId").ne(targetProdId)
        );

        // Step 5: Group by prodId and count views for each product
        GroupOperation groupByProdId = Aggregation.group("userViews.prodId")
                .count().as("viewCount");

        // Step 6: Project the results into a format with relatedProdId and viewCount
        ProjectionOperation projectToRelatedProduct = Aggregation.project("viewCount")
                .and("_id").as("relatedProdId"); // Include viewCount in projection

        // Step 7: Sort by view count and limit to top 5
        SortOperation sortByViewCount = Aggregation.sort(Sort.by(Sort.Direction.DESC, "viewCount"));
        Aggregation aggregation = Aggregation.newAggregation(
                matchUsersWhoViewedTarget,
                groupByUser,
                lookupProductsViewedByUsers,
                unwindViews,
                matchOtherProducts,
                groupByProdId,
                projectToRelatedProduct,
                sortByViewCount,
                Aggregation.limit(5)
        );

        // Execute the aggregation
        AggregationResults<RecommendationResult> results = mongoTemplate.aggregate(aggregation, "userLogs", RecommendationResult.class);
        return results.getMappedResults();
    }
}