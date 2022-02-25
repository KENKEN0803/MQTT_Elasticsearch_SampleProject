package com.duzon.mymosquito.service;

import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ElasticSearchService {

    private static RestHighLevelClient client;

    @Value("${es.url}")
    private String hostname; // localhost

    @Value("${es.port}")
    private Integer port; // 9200

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        System.out.println("======================= SearchEngine started at " + hostname + port);
        this.client = new RestHighLevelClient(RestClient.builder(new HttpHost(hostname, port, "http")));
        return new RestHighLevelClient(RestClient.builder(new HttpHost(hostname, port, "http")));
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

    public void createIndex(String indexName) throws IOException {

        CreateIndexRequest request = new CreateIndexRequest(indexName);

        request.settings(Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0)
        );

        client.indices().create(request, RequestOptions.DEFAULT);
    }
}