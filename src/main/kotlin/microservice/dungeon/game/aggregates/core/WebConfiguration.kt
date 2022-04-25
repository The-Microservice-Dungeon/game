package microservice.dungeon.game.aggregates.core

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfiguration : WebMvcConfigurer {
  @Override
  override fun addCorsMappings(registry: CorsRegistry) {
    registry.addMapping("/**")
      .allowedHeaders("*")
      .allowedMethods("*")
      .allowedOrigins("*")
  }
}
