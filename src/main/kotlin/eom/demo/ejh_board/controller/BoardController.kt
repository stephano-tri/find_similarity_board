package eom.demo.ejh_board.controller

import eom.demo.ejh_board.model.Board
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface BoardController {

    @PostMapping(
        value = ["/api/v1/board"],
        consumes = ["application/json"],
        produces = ["application/json"])
    fun createBoard(@RequestHeader headers: HttpHeaders,
                    @RequestBody boards: List<Board>): Flux<Board>

    @PostMapping(
        value = ["/api/v1/board/update"],
        consumes = ["application/json"],
        produces = ["application/json"])
    fun updateBoard(@RequestHeader headers: HttpHeaders,
                    @RequestBody board: Board): Mono<Board>


    @DeleteMapping(
        value = ["/api/v1/board"])
    fun removeBoard(@RequestHeader headers: HttpHeaders,
                    @RequestBody boards: List<Board>): Flux<Void>

    @GetMapping(
        value = [
            "/api/v1/board/list/{page}/{renderItem}",
            "/api/v1/board/list/{page}",
            "/api/v1/board/list"
        ],
        produces = ["application/json"])
    fun listBoard(@RequestHeader headers: HttpHeaders,
                  @PathVariable(name = "page") page: Int,
                  @PathVariable(name = "renderItem") renderItem: Int): Flux<Board>
}
