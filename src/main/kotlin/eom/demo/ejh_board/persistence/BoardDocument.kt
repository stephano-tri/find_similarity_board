package eom.demo.ejh_board.persistence

import eom.demo.ejh_board.model.Board
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.elasticsearch.annotations.Document
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun epochMillisToDate(epochMillis: Long): String = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault()).format(
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

@Document(indexName = "similarity.board")
data class BoardDocument(
    @Id val id: String? = null,
    val title: String,
    val content: String,
    val author: String,
    val views: Int,
    val likes: Int,
    val dislikes: Int,
    var created : Long? = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli(),
    var modified : Long? = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli(),
//    @Version var version: Long? = null
) {
    fun convert2Pojo(boardEntity: BoardDocument? = null): Board {
        return Board(
            boardId = this.id,
            title = this.title,
            content = this.content,
            author = this.author,
            views = this.views,
            likes = this.likes,
            dislikes = this.dislikes,
//            version = this.version,
            date = epochMillisToDate(this.created!!)
        )
    }
}
