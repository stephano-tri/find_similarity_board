package eom.demo.ejh_board.service

import eom.demo.ejh_board.controller.BoardService
import eom.demo.ejh_board.exception.InvalidInputException
import eom.demo.ejh_board.model.Board
import eom.demo.ejh_board.persistence.BoardDocument
import eom.demo.ejh_board.util.UtilFunctions
import org.elasticsearch.client.RestClient
import org.slf4j.Logger
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

/**
 * @description todo list
 *
 * 1. Authorization must be implemented
 * 2. Basic CRUD
 * 3. TF-IDF search logic in board
 */

@Service
class BoardServiceImpl(
    private val elasticsearchClient: ReactiveElasticsearchClient,
    private val restClient: RestClient,
    private val operations : ReactiveElasticsearchOperations,
    private val utilFunctions: UtilFunctions
): BoardService {
    val logger: Logger = org.slf4j.LoggerFactory.getLogger(BoardServiceImpl::class.java)

    override fun createBoard(headers: HttpHeaders, boards: List<Board>): Mono<List<Board>> {
        return if(headers.isNotEmpty()){
            Flux.fromIterable(boards)
                .flatMap { _board ->
                    operations.save(_board.convert2Entity())
                        .flatMap {
                            it.convert2Pojo().toMono()
                        }
                }
                .collectList()
        } else { InvalidInputException("유효하지 않은 요청 형식입니다.").toMono() }
    }

    override fun updateBoard(headers: HttpHeaders, board: Board): Mono<Board> {
        return if(headers.isNotEmpty()){
            operations.save(board.convert2Entity())
                .flatMap {
                    it.convert2Pojo().toMono()
                }
        } else { InvalidInputException("유효하지 않은 요청 형식입니다.").toMono() }
    }

    override fun removeBoard(headers: HttpHeaders, boards: List<Board>): Flux<Void> {
        return if(headers.isNotEmpty()){

        } else { InvalidInputException("유효하지 않은 요청 형식입니다.").toFlux() }
    }

    override fun listBoard(headers: HttpHeaders, page: Int, renderItem: Int): Flux<Board> {
        TODO("Not yet implemented")
    }

    /**
     * @description 게시물 등록 , 수정시 유사성 높은 게시물을 연결합니다
     */
    fun updateReferred(boards: List<Board>): Flux<Board> {
        TODO("유사성 높은 게시물을 연결합니다")
    }

}
