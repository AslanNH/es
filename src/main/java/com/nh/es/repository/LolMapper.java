package com.nh.es.repository;

import com.nh.es.entity.Lol;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LolMapper extends ElasticsearchRepository<Lol,String> {
}