package eom.demo.ejh_board.persistence

import com.fasterxml.jackson.annotation.JsonProperty
import eom.demo.ejh_board.model.ReferredInfo
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName = "similarity.board.referred")
data class BoardReferredDocument(
    @field:JsonProperty("board_id") val boardId: String? = null,
    @field:JsonProperty("referred") val referred : List<ReferredInfo>? = null,
)
