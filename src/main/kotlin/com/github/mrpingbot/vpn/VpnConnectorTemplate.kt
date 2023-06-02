package com.github.mrpingbot.vpn

interface VpnConnectorTemplate : VpnConnector {
    fun execute(action: () -> Unit) {
        start()
        try {
            action()
        } catch (e: Exception) {
            throw e;
        } finally {
            close()
        }
    }

    fun <T> execute(action: () -> T): T {
        start()
        try {
            return action()
        } catch (e: Exception) {
            throw e;
        } finally {
            close()
        }
    }
}
