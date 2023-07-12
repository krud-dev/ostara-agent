@file:OptIn(ExperimentalCli::class)

import dev.ostara.agent.AgentApplication
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import java.nio.file.Path
import java.util.*
import kotlin.system.exitProcess

val configPath = System.getProperty("config.file")?.let { Path.of(it) }
    ?: Path.of(System.getProperty("user.home"), ".ostara", "agent.yml")

object Commands {
    class Setup : Subcommand("setup", "Sets up the agent with initial configuration") {
        private val force by option(ArgType.Boolean, "force", "f", "Forces the setup to run even if the agent is already setup")
        override fun execute() {
            val file = configPath.toFile()
            if (file.exists() && force != true) {
                println("Agent already setup in ${configPath}, rerun with --force to overwrite")
                exitProcess(1)
            } else {
                println("Setting up agent config")
                val template = AgentApplication::class.java.getResource("/application-template.yml")?.readText()
                if (template == null) {
                    println("Failed to find template")
                    exitProcess(1)
                }
                val apiKey = UUID.randomUUID().toString()
                val replaced = template.replace("###REPLACE_ME###", apiKey)
                file.parentFile.mkdirs()
                file.createNewFile()
                file.writeText(replaced)
                println("""
                    Agent config written successfully
                    What next?
                    1. Edit the configuration file at $configPath
                    1. In the Ostara, create a new agent with the API key $apiKey
                    2. Start the agent with the command `ostara-agent start` or with `brew services start ostara-agent`
                """.trimIndent())
            }
        }
    }
    class Start(private val args: Array<String>) : Subcommand("start", "Starts the agent") {
        override fun execute() {
            if (!configPath.toFile().exists()) {
                println("Agent not setup, run setup first")
                exitProcess(1)
            }
            dev.ostara.agent.main(args + arrayOf("--spring.config.additional-location=$configPath"))
        }
    }

    class Version : Subcommand("version", "Prints the version") {
        private val buildInfo = try {
            AgentApplication::class.java.getResourceAsStream("/META-INF/build-info.properties").use {
                val props = Properties()
                props.load(it)
                props
            }
        } catch (e: Exception) {
            Properties()
        }

        val long by option(ArgType.Boolean, "long", "l", "Prints the long form of the version")
        override fun execute() {
            if (long == true) {
                println("Version: ${buildInfo["build.version"] ?: "Unknown"}")
                println("Time: ${buildInfo["build.time"] ?: "Unknown"}")
            } else {
                println(buildInfo["build.version"] ?: "Unknown")
            }
        }
    }
}

fun main(args: Array<String>) {
    val parser = ArgParser("ostara-agent")
    parser.subcommands(Commands.Version(), Commands.Setup(), Commands.Start(args))
    parser.parse(args)
}