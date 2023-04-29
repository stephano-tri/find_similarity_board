package eom.demo.ejh_board.service

import eom.demo.ejh_board.controller.BoardController
import eom.demo.ejh_board.exception.InvalidInputException
import eom.demo.ejh_board.model.Board
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

/**
 * @description todo list
 *
 * 1. Authorization must be implemented
 * 2. Basic CRUD
 * 3. TF-IDF search logic in board
 */

@Service
class BoardControllerImpl(
    private val reactiveElasticsearchTemplate: ReactiveElasticsearchOperations,
): BoardController {
    override fun createBoard(headers: HttpHeaders, boards: List<Board>): Flux<Board> {
        return if(headers.isNotEmpty()){
            Flux.fromIterable(boards)
                .flatMap { board ->
                    reactiveElasticsearchTemplate.save()
                }
        } else { InvalidInputException("유효하지 않은 요청 형식입니다.").toFlux() }
    }

    override fun updateBoard(headers: HttpHeaders, board: Board): Mono<Board> {
        TODO("Not yet implemented")
    }

    override fun removeBoard(headers: HttpHeaders, boards: List<Board>): Flux<Void> {
        TODO("Not yet implemented")
    }

    override fun listBoard(headers: HttpHeaders, page: Int, renderItem: Int): Flux<Board> {
        TODO("Not yet implemented")
    }

}
