package microservice.dungeon.game.model

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "articles")
data class Article (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    val id: Int = 0,

    @get: NotBlank
    val title: String = "",

    @get: NotBlank
    val contents: String = ""
)