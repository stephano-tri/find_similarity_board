package eom.demo.ejh_board.model

import com.fasterxml.jackson.annotation.JsonProperty
import eom.demo.ejh_board.persistence.BoardDocument
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive

data class Board(
    @field:JsonProperty("board_id") @field:Positive val boardId: Long? = null,
    @field:JsonProperty("title") @field:NotNull val title: String,
    @field:JsonProperty("author") @field:NotNull val author: String,
    @field:JsonProperty("content") @field:NotNull val content: String,
    @field:JsonProperty("date") @field:Pattern(regexp = "^[0-9]{4}.[0-9]{2}.[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$") val date: String,
    @field:JsonProperty("views") @field:Positive val views: Int? = 0,
    @field:JsonProperty("likes") @field:Positive val likes: Int? = 0,
    @field:JsonProperty("dislikes") @field:Positive val dislikes: Int? = 0,
    @field:JsonProperty("version") @field:Positive val version: Long? = null,
    ){
    fun convert2Entity(board: Board): BoardDocument {
        return BoardDocument(
            id = board.boardId,
            title = board.title,
            content = board.content,
            author = board.author,
            views = board.views!!,
            likes = board.likes!!,
            dislikes = board.dislikes!!,
            version = board.version
        )
    }

}
