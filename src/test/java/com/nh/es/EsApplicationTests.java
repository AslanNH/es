package com.nh.es;

import com.nh.es.entity.Lol;
import com.nh.es.repository.LolMapper;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest(classes = EsApplication.class)
@RunWith(SpringRunner.class)
class EsApplicationTests {


    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private LolMapper lolMapper;

    /**
     * 创建索引
     */
    @Test
    public void createIndex() {
        this.elasticsearchRestTemplate.deleteIndex(Lol.class);
        this.elasticsearchRestTemplate.createIndex(Lol.class);//报错先不管
    }

    /**
     * 删除索引
     * 索引删除将会同步删除mapping
     */
    @Test
    public void deleteIndex() {
        this.elasticsearchRestTemplate.deleteIndex(Lol.class);
    }

    /**
     * 新增
     */
    @Test
    public void create(){
        Lol lol = new Lol(0L,"荒野屠夫","雷克顿","所有人,都该死",123,"上单");
        this.lolMapper.save(lol);
    }

    /**
     * 根据某个条件删除
     */
    @Test
    public void delete(){
        Lol lol = new Lol(0L,"荒野屠夫1","雷克顿","所有人,都该死",234,"上单");
        this.lolMapper.delete(lol);
    }
    /**
     * 批量新增
     */
    @Test
    public void createBatch(){
        List<Lol> list = new ArrayList<>();
        Lol gl = new Lol(1L,"德玛西亚之力","盖伦","人在塔在",128,"上单");
        list.add(gl);
        Lol ys = new Lol(2L,"疾风剑豪","亚索","死亡如风，常伴吾身",987,"中单");
        list.add(ys);
        Lol cs = new Lol(3L,"魂锁典狱长","锤石","纳命来",198,"辅助");
        list.add(cs);
        Lol lxa = new Lol(4L,"圣枪游侠","卢锡安","死亡在敲门",111,"ADC");
        list.add(lxa);
        Lol glfs = new Lol(5L,"法外狂徒格雷福斯","格雷福斯","上下上下，左右左右，BABA",92,"打野");
        list.add(glfs);
        Lol lks = new Lol(6L,"光辉女郎","拉克丝","德玛西亚",12,"中单");
        list.add(lks);
        Lol fan = new Lol(7L,"无双剑姬","菲奥娜","优雅",90,"上单");
        list.add(fan);

        lolMapper.saveAll(list);
    }

    /**
     * 删除全部
     */
    @Test
    public void deleteAll(){
        this.lolMapper.deleteAll();
    }

    /**
     * 修改，相同id在保存一次就可以覆盖
     */
    @Test
    public void update(){
        Lol lol = new Lol(0L,"荒野屠夫","雷克顿","所有人,都得死",99,"上单");
        this.lolMapper.save(lol);
    }

    /**
     * 查询ById
     */
    @Test
    public void findById(){
        Optional<Lol> lol = this.lolMapper.findById("1");
        System.out.println("查询结果" +lol.get());
    }

    /**
     * 查询排序,降序
     */
    @Test
    public void findAllSort(){
        Iterable<Lol> lols = this.lolMapper.findAll(Sort.by("age").descending());

        lols.forEach(System.out::println);
    }

    /**
     * 条件查询
     */
    @Test
    public void findByName(){
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("desc","左右 AB");
        Iterable<Lol> search = this.lolMapper.search(matchQueryBuilder);
        search.forEach(System.out::println);
    }

    /**
     * 分页
     */
    @Test
    public void nativeSearch(){
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("realName","盖伦");

        nativeSearchQueryBuilder.withQuery(matchQueryBuilder);

        // 分页=======开始
        int page = 0;
        int size = 1;
        nativeSearchQueryBuilder.withPageable(PageRequest.of(page,size));
        // 分页=======结束

        // 排序=======开始
        SortBuilder sortBuilder = SortBuilders.fieldSort("age");
        sortBuilder.order(SortOrder.ASC);
        nativeSearchQueryBuilder.withSort(sortBuilder);
        // 排序=======结束

        Page<Lol> search = this.lolMapper.search(nativeSearchQueryBuilder.build());

        System.out.println(search.getTotalElements());
        System.out.println(search.getTotalPages());
        System.out.println(search.getSize());
        System.out.println(search.getNumber());
        search.forEach(System.out::println);
    }

    /**
     * 分组
     */
    @Test
    public void aggSearch(){
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("zyAgg").field("zy.keyword"));

        //结果集过滤不包含任何字段
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));

        AggregatedPage<Lol> search = (AggregatedPage<Lol>)this.lolMapper.search(nativeSearchQueryBuilder.build());


        ParsedStringTerms zyAgg = (ParsedStringTerms)search.getAggregation("zyAgg");

        List<? extends Terms.Bucket> buckets = zyAgg.getBuckets();

        buckets.forEach((bucket ->{
            System.out.println("bucket.getKeyAsString()="+bucket.getKeyAsString());
            System.out.println("bucket.getDocCount()="+bucket.getDocCount());
        }));
    }
}