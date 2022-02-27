package com.duzon.mymosquito.service;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchParseException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ElasticSearchService {

    private static RestHighLevelClient client;

    @Value("${es.url}")
    private String hostname; // localhost

    @Value("${es.port}")
    private Integer port; // 9200

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        client = new RestHighLevelClient(RestClient.builder(new HttpHost(hostname, port, "http")));
        System.out.println("======================= SearchEngine connected at " + hostname + port);
        return new RestHighLevelClient(RestClient.builder(new HttpHost(hostname, port, "http")));
    }

    public void createIndex(String indexName) throws IOException {

        CreateIndexRequest request = new CreateIndexRequest(indexName);

        request.settings(Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0)
        );

        client.indices().create(request, RequestOptions.DEFAULT);
    }

    public void createDocument(String index, String id, String jsonBody) throws IOException {
        IndexRequest request = new IndexRequest(index)
                .id(id)
                .source(jsonBody, XContentType.JSON);

        client.index(request, RequestOptions.DEFAULT);
    }

    public GetResponse getDocument(String index, String id) throws IOException {
        GetRequest request = new GetRequest(index, id);
        return client.get(request, RequestOptions.DEFAULT);
    }

    public List<Map<String, Object>> searchDocument(String index, String searchParam, String searchOption)
            throws Exception {

        List<Map<String, Object>> list = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchRequest.indices(index); // 찾을 인덱스 지정

        switch (searchOption) {
            case "exact":
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                boolQuery.must(QueryBuilders.matchQuery("msg", searchParam));
                searchSourceBuilder.query(boolQuery);
                break;
            case "wildCard":
                WildcardQueryBuilder wildcardQuery = new WildcardQueryBuilder("msg", "*" + searchParam + "*");
                searchSourceBuilder.query(wildcardQuery);
                break;
            default:
                throw new ElasticsearchParseException("검색 옵션이 지정되지 않았습니다");
        }

        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchHits = response.getHits();
            searchHits.forEach(hit -> {
                Map<String, Object> sourceMap = hit.getSourceAsMap();
                list.add(sourceMap);
            });
        } catch (IOException e) {
            throw e;
        }

        return list;
    }
}














