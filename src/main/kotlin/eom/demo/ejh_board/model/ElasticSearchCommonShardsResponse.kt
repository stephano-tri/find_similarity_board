package eom.demo.ejh_board.model

import com.fasterxml.jackson.annotation.JsonProperty
import eom.demo.ejh_board.annotation.NoArgsConstructor

@NoArgsConstructor
data class ElasticSearchCommonShardsResponse (
    @field:JsonProperty("total") var total: Int = 0,
    @field:JsonProperty("successful") var successful: Int = 0,
    @field:JsonProperty("skipped") var skipped: Int = 0,
    @field:JsonProperty("failed")var failed: Int = 0
)
