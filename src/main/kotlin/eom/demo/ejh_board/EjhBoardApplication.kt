package eom.demo.ejh_board

import com.google.gson.GsonBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import java.time.ZonedDateTime

@SpringBootApplication(scanBasePackages = ["eom.demo"])
class EjhBoardApplication

@Bean
fun builder(): WebClient.Builder = WebClient.builder().exchangeStrategies(
    ExchangeStrategies.builder()
        .codecs { configurer ->
            configurer
                .defaultCodecs()
                .maxInMemorySize(8 * 1024 * 1024)
        }
        .build())

@Bean
fun gsonBuilder(): GsonBuilder {
    val builder = GsonBuilder()
    builder.registerTypeAdapter(ZonedDateTime::class.java, ZonedDatetimeAdapter())
    return builder
}

fun main(args: Array<String>) {
    runApplication<EjhBoardApplication>(*args)
}
