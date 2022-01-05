package com.zwolsman.bombastic

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BombasticApiApplication

fun main(args: Array<String>) {
    runApplication<BombasticApiApplication>(*args)
}
