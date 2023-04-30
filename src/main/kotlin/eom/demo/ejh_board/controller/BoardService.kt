package eom.demo.ejh_board.controller

import eom.demo.ejh_board.model.Board
import eom.demo.ejh_board.model.Pagination
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
interface BoardService {

    @GetMapping(
        value = ["/api/v1/board/{id}"],
        produces = ["application/json"])
    fun loadBoard(@RequestHeader headers: HttpHeaders,
                  @PathVariable id: String): Mono<Board>

    @PostMapping(
        value = ["/api/v1/board"],
        consumes = ["application/json"],
        produces = ["application/json"])
    fun createBoard(@RequestHeader headers: HttpHeaders,
                    @Validated @RequestBody boards: List<Board>): Mono<List<Board>>

    @PostMapping(
        value = ["/api/v1/board/update"],
        consumes = ["application/json"],
        produces = ["application/json"])
    fun updateBoard(@RequestHeader headers: HttpHeaders,
                    @Validated @RequestBody board: Board): Mono<Board>


    @DeleteMapping(
        value = ["/api/v1/board"])
    fun removeBoard(@RequestHeader headers: HttpHeaders,
                    @Validated @RequestBody boards: List<Board>): Flux<Void>

    @GetMapping(
        value = [
            "/api/v1/board/list/{page}/{limit}",
            "/api/v1/board/list/{page}",
            "/api/v1/board/list"
        ],
        produces = ["application/json"])
    fun listBoard(@RequestHeader headers: HttpHeaders,
                  @PathVariable(name = "page") page: Int,
                  @PathVariable(name = "limit") limit: Int): Mono<Pagination<Board>>

}
