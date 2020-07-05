package com.example

import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson {
        }
    }

    routing {
        get("/snippets") {
            call.respond(SnippetsDataFactory.SNIPPETS_DATA)
        }
        post {
            SnippetsDataFactory.SNIPPETS_DATA += call.receive<Snippets>()
            call.respond(mapOf("OK" to true))
        }
    }
}




