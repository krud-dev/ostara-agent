package dev.ostara.agent.service

import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths

@Service
class KubernetesAwarenessService {
  private val namespace by lazy {
    try {
      Files.readString(Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/namespace"))
    } catch (e: Exception) {
      null
    }
  }

  fun getHostname(): String? {
    return System.getenv(HOSTNAME)
  }

  fun resolveNamespace(): String? {
    return namespace
  }

  companion object {
    private const val HOSTNAME = "HOSTNAME"
  }
}
