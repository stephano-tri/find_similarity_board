package eom.demo.ejh_board.model

import com.fasterxml.jackson.annotation.JsonProperty
import eom.demo.ejh_board.annotation.NoArgsConstructor

@NoArgsConstructor
data class ElasticSearchCountResponse(
    @field:JsonProperty("count") val count: Long,
    @field:JsonProperty("_shards") val _shards: ElasticSearchCommonShardsResponse
)
