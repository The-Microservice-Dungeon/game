package microservice.dungeon.game.testingExample.services

import microservice.dungeon.game.testingExample.model.Article
import microservice.dungeon.game.testingExample.repository.ArticleRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ArticleService(private val articleRepository: ArticleRepository) {

    fun getAllArticles(): List<Article> = articleRepository.findAll()

    fun save(article: Article): Article = articleRepository.save(article)

    fun getArticleById(articleId: Int): Optional<Article>  =  articleRepository.findById(articleId)

    fun updateArticleById(articleId: Int,newArticle: Article) : Optional<Article>{
        return articleRepository.findById(articleId).map { existingArticle ->
            val updatedArticle: Article = existingArticle
                .copy(title = newArticle.title, contents = newArticle.contents)
            articleRepository.save(updatedArticle)
        }
    }

    fun deleteArticleById(articleId: Int) : Optional<Unit>  {
        return articleRepository.findById(articleId).map { article ->
            articleRepository.delete(article)
        }
    }
}