package com.example

import pl.treksoft.kvision.Application
import pl.treksoft.kvision.html.main
import pl.treksoft.kvision.i18n.DefaultI18nManager
import pl.treksoft.kvision.i18n.I18n
import pl.treksoft.kvision.panel.root
import pl.treksoft.kvision.panel.stackPanel
import pl.treksoft.kvision.startApplication
import pl.treksoft.kvision.utils.perc

class App : Application() {

    override fun start(state: Map<String, Any>) {
        I18n.manager =
            DefaultI18nManager(
                mapOf(
                    "en" to pl.treksoft.kvision.require("i18n/messages-en.json"),
                    "pl" to pl.treksoft.kvision.require("i18n/messages-pl.json")
                )
            )
        val root = root("kvapp") {
            minHeight = 100.perc
            height = 100.perc
            add(NavTop())
            add(NavSide())
            main(classes = setOf("col-md-9", "ml-sm-auto", "col-lg-10", "px-md-4")) {
                // The main contents of the dashboard is a stack panel
                // The route specifies what link will load the item from the stack
                val panel = stackPanel {
                    // Add status panel with default route to root of site
                    add(StatusPanel(), route = "/")
                    // Additional panels should specify a route that matches the link in the NavSide
                    // Note that the route here does not include #! but must be included in the nav link
                    // to connect the link to the routing
                    // add(DetailsPanel(), route = "/details")
                }
            }
        }
    }
}

fun main() {
    startApplication(::App)
}
