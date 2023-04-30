package eom.demo.ejh_board.service

import com.google.gson.GsonBuilder
import eom.demo.ejh_board.model.ElasticSearchCountResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class RequestService (
    private val gsonBuilder: GsonBuilder,
    ) {
    val webClient: WebClient = WebClient
                                .builder()
                                .baseUrl("http://localhost:9200")
                                .build()

    fun loadCount(index: String): Mono<ElasticSearchCountResponse> =
        webClient.get()
            .uri("/$index/_count")
            .retrieve()
            .bodyToMono(ElasticSearchCountResponse::class.java)

}
