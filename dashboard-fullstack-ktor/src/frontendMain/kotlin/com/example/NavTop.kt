package com.example

import pl.treksoft.kvision.core.BsBgColor
import pl.treksoft.kvision.html.span
import pl.treksoft.kvision.navbar.*
import pl.treksoft.kvision.utils.perc

/**
 * Class to place a navbar across the top of the page
 */
class NavTop : Navbar {

    /**
     * Constructor to create the KVision Navbar object with
     *  - A "brand" label
     *  - A link on the brand
     *  - Dark background
     *  - Dark color theme
     *  - Placement of "sticky top" which will keep the bar at the top of the browser
     *    even when scrolling down the page
     *  - A set of CSS classes
     *    shadow: creates a drop shadow under the bar
     *    flex-md-nowrap: prevents bar from wrapping to the next row (TODO: confirm it's correct and needed)
     */
    constructor() : super("Dashboard",
            "https://github.com/rjaros/kvision/",
            bgColor = BsBgColor.DARK,
            nColor = NavbarColor.DARK,
            type = NavbarType.STICKYTOP,
            classes = setOf("flex-md-nowrap", "shadow")) {

        width = 100.perc

        // Add a nav container div to place nav links
        nav {
            width = 100.perc

            // Add a span to fill up unused space in the middle of the nav bar
            // Note: the boot strap example puts a search input here
            span { width = 100.perc }
            // Add a signout nav link which currently does nothing
            navLink("Sign out", className = "text-nowrap")
        }
    }

}