package dev.ostara.agent.config.condition

import dev.ostara.agent.config.ServiceDiscoveryProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

@Target(AnnotationTarget.CLASS)
@ConditionalOnProperty(prefix = ServiceDiscoveryProperties.PREFIX, name = ["kubernetes.enabled"], havingValue = "true")
annotation class ConditionalOnKubernetesEnabled
