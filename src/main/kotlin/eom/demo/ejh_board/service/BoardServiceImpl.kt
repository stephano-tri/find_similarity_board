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
                .flatMap {
                    it.content.convert2Pojo().toMono()
                }
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

    override fun aggsBoard(headers : HttpHeaders) : Mono<Any> {
        return if(headers.isNotEmpty()){
            requestService.loadCount("similarity.board")
                .flatMap { it.count.toMono() }
                .flatMap { cnt ->
                    loadRelatedWords((cnt * 0.4).toInt() , "bigger")
                }
        } else { InvalidInputException("유효하지 않은 요청 형식입니다.").toMono() }
    }

    /**
     * @description 게시물 등록 , 수정시 유사성 높은 게시물을 연결합니다
     */
    fun updateReferred(boards: List<Board>): Flux<Board> {
        return Flux.fromIterable(boards)
            .flatMap { targetPost ->
                val targetId = targetPost.boardId!!
                val withoutTargetPost = boards.filter { it.boardId != targetId }
                Flux.fromIterable(withoutTargetPost)
                    .flatMap { otherPost ->
                        val otherId = otherPost.boardId!!
                        isItReferredPost(targetId, otherId)
                            .flatMap {
                                if(it){
                                    ReferredInfo(
                                        otherId,0.0
                                    ).toMono()
                                }
                                else {
                                    Mono.empty()
                                }
                            }
                    }
                    .collectList()
                    .flatMap { referredList ->
                        saveReferredBoard(targetId, referredList)
                            .flatMap { targetPost.toMono() }
                    }
            }
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

    /**
     * @description 연관 게시글을 찾습니다.
     */

    fun isItReferredPost(targetPostId: String, otherPostId: String): Mono<Boolean> {
        /**
         * @need
         * i) target board's word list with frequency
         * ii) find similar words in other post with target board's word list
         */

        val targetPostWords = queries.loadHighFrequencyWords(1, targetPostId).toMono()
            .flatMap { query ->
                getResultFromAggregation(
                    requestService.searchHighFrequencyWords("similarity.board", query),
                    "top_words"
                )
            }
            .flatMapIterable { it }
            .flatMap { word ->
                val key = word["key"] as String
                val docCount = word["doc_count"] as Int
                key.toMono()
            }
            .collectList()

        val otherPostWords = queries.loadHighFrequencyWords(1,  otherPostId).toMono()
            .flatMap { query ->
                getResultFromAggregation(
                    requestService.searchHighFrequencyWords("similarity.board", query),
                    "top_words"
                )
            }
            .flatMapIterable { it }
            .flatMap { word ->
                val key = word["key"] as String
                val docCount = word["doc_count"] as Int
                key.toMono()
            }
            .collectList()

        return Mono.zip(targetPostWords, otherPostWords)
            .flatMap{
                it.t1.intersect(it.t2).toMono()
            }
            .flatMap { intersectedSet ->
                if(intersectedSet.size >= 2) {
                    true.toMono()
                }
                else {
                    false.toMono()
                }
            }
    }

    fun loadRelatedWords(percentage: Int, operation: String) : Mono<List<String>> {
       return queries.loadHighFrequencyWords(1).toMono()
                    .flatMap { reqBody ->
                        getResultFromAggregation(
                                requestService.searchHighFrequencyWords("similarity.board", reqBody) ,
                            "top_words")
                            .flatMapIterable { it }
                            .flatMap { word ->
                                val key = word["key"] as String
                                val docCount = word["doc_count"] as Int
                                if(operation == "bigger") {
                                    if(docCount >= percentage){
                                        key.toMono()
                                    }
                                    else {
                                        Mono.empty()
                                    }
                                }
                                else {
                                    if(docCount <= percentage){
                                        key.toMono()
                                    }
                                    else {
                                        Mono.empty()
                                    }
                                }
                            }
                            .collectList()
                    }
    }


    private fun getResultFromAggregation(aggs: Mono<Map<String, *>>, aggName: String) : Mono<List<Map<String, Any>>> {
        return aggs
                .flatMap { it["aggregations"].toMono() }
                .flatMap {
                    val target = it as Map<String, Any>
                    val buckets = target[aggName] as Map<String,Any>
                    buckets["buckets"].toMono()
                }
                .flatMap { buckets ->
                    val bucketList = buckets as List<Map<String, Any>>
                    val words = bucketList.toMono()
                    words.toMono()
                }
    }

}

