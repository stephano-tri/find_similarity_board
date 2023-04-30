package eom.demo.ejh_board.model

import com.fasterxml.jackson.annotation.JsonProperty
import eom.demo.ejh_board.persistence.BoardDocument
import eom.demo.ejh_board.persistence.BoardReferredDocument
import jakarta.validation.constraints.*

data class Board(
    @field:JsonProperty("board_id") @field:Positive val boardId: String? = null,
    @field:JsonProperty("title") @field:NotNull val title: String,
    @field:JsonProperty("author") @field:NotNull val author: String,
    @field:JsonProperty("content") @field:NotBlank(message = "내용을 반드시 입력해주세요.") val content: String,
    @field:JsonProperty("date") @field:Pattern(regexp = "^[0-9]{4}.[0-9]{2}.[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$") val date: String? = null,
    @field:JsonProperty("views") @field:Positive val views: Int? = 0,
    @field:JsonProperty("likes") @field:Positive val likes: Int? = 0,
    @field:JsonProperty("dislikes") @field:Positive val dislikes: Int? = 0,
    @field:JsonProperty("referred") val referred : List<BoardReferredDocument>? = null,
//    @field:JsonProperty("version") @field:Positive val version: Long? = null,
    ){
    fun convert2Entity(board: Board? = null): BoardDocument {
        return BoardDocument(
            id = this.boardId,
            title = this.title,
            content = this.content,
            author = this.author,
            views = this.views ?: 0,
            likes = this.likes ?: 0,
            dislikes = this.dislikes ?: 0,
//            version = this.version ?: 0,
            statement = "active"
        )
    }

}
