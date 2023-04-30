package eom.demo.ejh_board

import eom.demo.ejh_board.controller.BoardService
import eom.demo.ejh_board.persistence.BoardDocument
import eom.demo.ejh_board.seed.Boards
import eom.demo.ejh_board.service.BoardServiceImpl
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations
import org.springframework.http.HttpHeaders
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.MethodName::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EjhBoardApplicationTests {

    @Autowired
    lateinit var boardService: BoardServiceImpl
    @Autowired
    lateinit var operations : ReactiveElasticsearchOperations


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
    fun loadWords(){
        StepVerifier.create(boardService.loadHighFrequencyWords())
            .expectSubscription()
            .verifyComplete()
    }

    @Test
    @Disabled
    fun dataSeeding() {
        val boards = Boards().boardTestData
        StepVerifier.create(boardService.createBoard(httpHeaders, boards))
            .expectSubscription()
            .verifyComplete()
    }

}
