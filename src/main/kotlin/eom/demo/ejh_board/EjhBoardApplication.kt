package eom.demo.ejh_board

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories

@SpringBootApplication
class EjhBoardApplication

fun main(args: Array<String>) {
    runApplication<EjhBoardApplication>(*args)
}
