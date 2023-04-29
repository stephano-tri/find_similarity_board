package eom.demo.ejh_board

import eom.demo.ejh_board.controller.BoardService
import eom.demo.ejh_board.seed.Boards
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EjhBoardApplicationTests {
    @Autowired
    lateinit var boardService: BoardService

    companion object {
        var httpHeaders = HttpHeaders()

        @BeforeAll
        @JvmStatic
        internal fun testConstructor() {
            val validToken = "am i human?"
            val dummyHeader = mutableMapOf<String, String>("Authorization" to validToken)
            httpHeaders = httpHeaders.apply { this.setAll(dummyHeader) }
        }
    }

    @Test
    fun contextLoads() {
    }

    @Test
    fun dataSeeding() {
        val boards = Boards().boardTestData
        StepVerifier.create(boardService.createBoard(httpHeaders, boards))
            .expectSubscription()
            .verifyComplete()
    }
}
