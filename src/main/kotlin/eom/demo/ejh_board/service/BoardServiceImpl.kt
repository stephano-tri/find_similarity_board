package eom.demo.ejh_board.service

import eom.demo.ejh_board.controller.BoardService
import eom.demo.ejh_board.exception.InvalidInputException
import eom.demo.ejh_board.exception.NotFoundException
import eom.demo.ejh_board.model.Board
import eom.demo.ejh_board.model.Pagination
import eom.demo.ejh_board.model.ReferredInfo
import eom.demo.ejh_board.persistence.BoardDocument
import eom.demo.ejh_board.persistence.BoardReferredDocument
import eom.demo.ejh_board.util.UtilFunctions
import org.slf4j.Logger
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import kotlin.math.ceil

/**
 * @description todo list
 *
 * 1. Authorization must be implemented
 * 2. Basic CRUD
 * 3. TF-IDF search logic in board
 */

@Service
class BoardServiceImpl(
    private val operations : ReactiveElasticsearchOperations,
    private val queries : BoardQuery,
    private val utilFunctions: UtilFunctions,
    private val requestService: RequestService
): BoardService {
    val logger: Logger = org.slf4j.LoggerFactory.getLogger(BoardServiceImpl::class.java)

    override fun loadBoard(headers : HttpHeaders, id : String) : Mono<Board> {
        return if(headers.isNotEmpty()){
            operations.search(queries.loadBoardById(id), BoardDocument::class.java)
                .elementAt(0)
                .flatMap { it.content.convert2Pojo().toMono() }
        } else { InvalidInputException("유효하지 않은 요청 형식입니다.").toMono() }
    }

    override fun createBoard(headers: HttpHeaders, boards: List<Board>): Mono<List<Board>> {
        return if(headers.isNotEmpty()){
            Flux.fromIterable(boards)
                .flatMap { board ->
                    operations.save(board.convert2Entity())
                    .flatMap { boardDoc ->
                        this.saveReferredBoard(boardDoc.id!!)
                            .flatMap { boardDoc.toMono() }
                    }
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
            Flux.fromIterable(boards)
                .flatMap { board ->
                    operations.search(queries.loadBoardById(board.boardId!!), BoardDocument::class.java)
                        .switchIfEmpty(NotFoundException("[제목 : ${board.title}]은 존재하지 않는 게시물입니다.").toMono())
                        .flatMap { it.content.toMono() }
                        .flatMap {
                            operations.save(it.apply { this.statement = "dead" })
                                .then()
                        }
                }
        } else { InvalidInputException("유효하지 않은 요청 형식입니다.").toFlux() }
    }

    override fun listBoard(headers: HttpHeaders, page: Int, limit: Int): Mono<Pagination<Board>> {
        return if(headers.isNotEmpty()){
            operations.search(queries.loadBoards(page, limit), BoardDocument::class.java)
                .flatMap { it.content.toMono() }
                .flatMap { it.convert2Pojo().toMono() }
                .collectList()
                .flatMap { boardList ->
                    requestService.loadCount("similarity.board")
                        .flatMap {
                            Mono.just(Pagination(boardList, page, limit, ceil(it.count / limit.toDouble()).toInt() ))
                        }
                }
        } else { InvalidInputException("유효하지 않은 요청 형식입니다.").toMono() }
    }

    /**
     * @description 게시물 등록 , 수정시 유사성 높은 게시물을 연결합니다
     */
    fun updateReferred(boards: List<Board>): Flux<Board> {
        TODO("유사성 높은 게시물을 연결합니다")
    }

    /**
     * @description 연관 게시물 정보를 등록합니다.
     */
    fun saveReferredBoard(boardId: String, referredList: List<ReferredInfo>? = null): Mono<BoardReferredDocument> {
        return operations.save(BoardReferredDocument(
            boardId = boardId,
            referred = referredList ?: listOf()
        ))
    }

}
