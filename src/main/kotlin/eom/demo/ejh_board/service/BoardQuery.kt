package eom.demo.ejh_board.service

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation
import co.elastic.clients.util.MapBuilder
import org.elasticsearch.client.RestClient
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations
import org.springframework.stereotype.Component

@Component
class BoardQuery(
    private val elasticsearchClient: ReactiveElasticsearchClient,
    private val restClient: RestClient,
    private val operations: ReactiveElasticsearchOperations,
) {
    fun loadBoardById(id: String): NativeQuery {
        return NativeQuery.builder()
            .withQuery { q ->
                q.term{ t ->
                    t.field("_id")
                        .value(id)
                }
            }
            .withQuery { q2->
                q2.term { t ->
                    t.field("statement")
                        .value("active")
                }
            }
            .build()
    }

    fun loadBoards(page: Int, limit: Int): NativeQuery {
        return NativeQuery.builder()
            .withQuery { q ->
                q.term {
                    t ->
                        t.field("statement")
                            .value("active")
                }
            }
            .withPageable(PageRequest.of(page - 1, limit))
            .build()
    }

    fun loadHighFrequencyWords(minDocCount: Int, targetPost: String? = null): Map<String,*> {
        //NativeQuery for Aggregation
        val targetMap = targetPost ?. let {
            MapBuilder.of(
                "query",
                MapBuilder.of(
                    "match",
                    MapBuilder.of("_id", targetPost)
                )
            )
        } ?: run {
            MapBuilder.of("query",
                MapBuilder.of("match_all" ,
                      mutableMapOf()
                    ))
        }

        val defaultQueryMap = MapBuilder.of("size","0","aggregations" ,
                                MapBuilder.of("top_words",
                                    MapBuilder.of("terms",
                                        MapBuilder.of("field", "content", "min_doc_count", minDocCount))))

        return targetMap + defaultQueryMap
    }

}
