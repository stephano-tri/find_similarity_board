package eom.demo.ejh_board.service

import org.elasticsearch.client.RestClient
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient
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

    fun loadHighFrequencyWords(minDocCount: Long): NativeQuery {
        TODO("aggregation query needed")
    }

}
