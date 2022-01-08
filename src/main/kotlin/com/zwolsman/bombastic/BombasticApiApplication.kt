package com.zwolsman.bombastic

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableSwagger2
class BombasticApiApplication

fun main(args: Array<String>) {
    runApplication<BombasticApiApplication>(*args)
}
