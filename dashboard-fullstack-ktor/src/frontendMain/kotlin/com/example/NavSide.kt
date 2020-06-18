package com.example

import pl.treksoft.kvision.navbar.Navbar
import pl.treksoft.kvision.navbar.nav
import pl.treksoft.kvision.navbar.navLink
import pl.treksoft.kvision.utils.rem

/**
 * Class to place a navbar on the side of the page
 */
class NavSide : Navbar {
    /**
     * Constructor to create the KVision Navbar object on the side
     *  - Uses most defaults so that we have no "brand" label or link which is in the top nav bar,
     *    the default color light color schemes, and default placement which will scroll with the page
     *  - Css classes set to
     *    sidebar: this is defined in the boot strap example "dashboard.css" which we have not taken yet
     *    others: were also specified in boot strap example, not sure which if any are needed
     */
    constructor() : super(classes = setOf("sidebar", "col-md-3", "col-lg-2", "d-md-block")) {
        // Create nav container to hold links on the side bar
        // flex-column: This forces nav links to be placed in a column and what makes it a side nav
        // sidebar-sticky: Provided in dashboard.css to keep the side bar nav in view (I assume)
        nav(classes = setOf("sidebar-sticky", "flex-column")) {
            paddingTop = 1.rem
            // Add a nav link for the status which is the default page loaded
            navLink("Status", "", className = "active")
            // Future nav links should be added as below with the url set to
            // "#!/navdestination" so that the javascript routing will load a panel from the stack pane
            // defined in the App.kt
            //navLink("Detail", "#!/details")
        }
    }
}