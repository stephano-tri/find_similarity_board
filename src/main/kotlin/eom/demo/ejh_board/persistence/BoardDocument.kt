package eom.demo.ejh_board.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.elasticsearch.annotations.Document
import java.time.LocalDateTime
import java.time.ZoneOffset

@Document(indexName = "similarity.board")
data class BoardDocument(
    @Id val id: Long? = null,
    val title: String,
    val content: String,
    val author: String,
    val views: Int,
    val likes: Int,
    val dislikes: Int,
    var created : Long? = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli(),
    var modified : Long? = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli(),
    @Version
    var version: Long? = null
)
