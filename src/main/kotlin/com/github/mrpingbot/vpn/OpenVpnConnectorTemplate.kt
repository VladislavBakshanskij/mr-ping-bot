package com.github.mrpingbot.vpn

import com.github.mrpingbot.utils.*
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import kotlin.io.path.notExists


@Component
@ConditionalOnProperty(
    prefix = VPN_CONNECTOR_CONFIG_PROPERTIES_PREFIX,
    name = [VPN_CONNECTOR_CONFIG_PROPERTIES_TYPE_NAME],
    havingValue = "openvpn",
    matchIfMissing = false
)
class OpenVpnConnectorTemplate(
    @Value("\${$VPN_CONNECTOR_CONFIG_PROPERTIES_PREFIX.openvpn.path-config-file}") private val pathConfigFile: String,
) : VpnConnectorTemplate {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(OpenVpnConnectorTemplate::class.java)

        private const val INITIALIZATION_CODE_PHRASE = "Initialization Sequence Completed"

        private const val OPENVPN_COMMAND_NAME = "openvpn"
        private const val OPENVPN_COMMAND_CONFIG_KEY_NAME = "--config"
    }

    private val processes: Queue<Process> = ArrayBlockingQueue(10)

    @PostConstruct
    fun checkFileOnExists() {
        val notExistsConfigFile = Paths.get(pathConfigFile).notExists()
        if (notExistsConfigFile) {
            throw VpnConfigurationException(
                """
                При включении опции 
                ${VPN_CONNECTOR_CONFIG_ENV_PREFIX}_$VPN_CONNECTOR_CONFIG_ENV_TYPE_NAME
                в true нужно установить путь к файлу кофигурации openvpn опцией 
                ${VPN_CONNECTOR_CONFIG_ENV_PREFIX}_OPENVPN_PATH_CONFIG_FILE.
            """.trimIndent()
            )
        }
    }

    @PreDestroy
    fun stopProcesses() {
        while (processes.isNotEmpty()) {
            close()
        }
    }

    override fun start() {
        val process = ProcessBuilder(OPENVPN_COMMAND_NAME, OPENVPN_COMMAND_CONFIG_KEY_NAME, pathConfigFile).start()
        logger.info("Process with pid {} start", process?.pid())

        // check initialization
        process.inputStream.use {
            BufferedReader(InputStreamReader(it)).use { buffered ->
                var line: String
                val start = System.nanoTime()
                do {
                    line = buffered.readLine() ?: break
                } while (line.notContains(INITIALIZATION_CODE_PHRASE))
                logger.info("Vpn start completed {} ns", System.nanoTime() - start)
            }
        }

        processes.add(process)
    }

    override fun close() {
        if (processes.isNotEmpty()) {
            val process = processes.poll()
            val pid = process.pid()
            process.destroy()
            logger.info("Process with pid {} close", pid)
        }
    }
}
