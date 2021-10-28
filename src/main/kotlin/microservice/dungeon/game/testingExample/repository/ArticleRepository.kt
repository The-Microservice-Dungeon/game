package microservice.dungeon.game.testingExample.repository

import microservice.dungeon.game.testingExample.model.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : JpaRepository<Article, Int>