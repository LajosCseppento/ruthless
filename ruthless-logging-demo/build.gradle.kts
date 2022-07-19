// For testing Ruthless Logging
tasks.register("fail") {
    doLast {
        throw GradleException("Failing the build", IllegalStateException("Failure cause"))
    }
}

tasks.register("log") {
    doLast {
        println("Hello via println!")

        logger.error("Error log")
        logger.error(
            "[error] Error log with exception",
            GradleException("Logged exception", IllegalStateException("Logged exception cause"))
        )
        logger.quiet("Quiet log")
        logger.warn("Warn log")
        logger.lifecycle("Lifecycle log")
        logger.info("Info log")
        logger.debug("Debug log")
    }
}
