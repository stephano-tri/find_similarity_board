package eom.demo.ejh_board.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration

@Configuration
@ConfigurationProperties(prefix = "elastic")
class ElasticSearchConfiguration : ReactiveElasticsearchConfiguration() {

    lateinit var host: String
    lateinit var port: String

    override fun clientConfiguration() : ClientConfiguration {
        return ClientConfiguration.builder()
            .connectedTo("$host:$port")
            .build()
    }
}
