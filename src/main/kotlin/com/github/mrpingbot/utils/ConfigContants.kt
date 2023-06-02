package com.github.mrpingbot.utils

// region vpn
const val VPN_CONNECTOR_CONFIG_PROPERTIES_PREFIX: String = "vpn.connector"
const val VPN_CONNECTOR_CONFIG_PROPERTIES_TYPE_NAME: String = "type"

var VPN_CONNECTOR_CONFIG_ENV_PREFIX: String =
    VPN_CONNECTOR_CONFIG_PROPERTIES_PREFIX.uppercase().replaceAllNotLetter("_")

var VPN_CONNECTOR_CONFIG_ENV_TYPE_NAME: String =
    VPN_CONNECTOR_CONFIG_PROPERTIES_TYPE_NAME.uppercase().replaceAllNotLetter("_")

// endregion
